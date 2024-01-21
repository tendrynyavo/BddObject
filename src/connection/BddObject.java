package connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.primitives.Primitives;
import formulaire.Formulaire;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BddObject extends Bdd {

/// Field
    @JsonIgnore
    transient ArrayList<Column> columns;
    @JsonIgnore
    transient Boolean serial = true;
    @JsonIgnore
    transient Boolean containsID = false;
    String id;

    public BddObject setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setContainsID(Boolean containsID) {
        this.containsID = containsID;
    }

    public Boolean isContainsID() {
        return containsID;
    }

    public Boolean isSerial() {
        return serial;
    }

    public BddObject setSerial(Boolean serial) {
        this.serial = serial;
        return this;
    }

    public BddObject setPrefix(String prefix) {
        this.getSequence().setPrefix(prefix);
        return this;
    }

    public void setCountPK(int countPK) throws IllegalArgumentException {
        this.getSequence().setCountPK(countPK);
    }

    public BddObject setFunctionPK(String function) {
        this.getSequence().setFunctionPK(function);
        return this;
    }

    public BddObject addColumn(Column colonne) throws Exception {
        this.getColumns().add(colonne);
        return this;
    }

    public List<Column> getColumns() throws Exception {
        Column column = this.columns.get(this.columns.size() - 1);
        column.setName(this.getPrimaryKeyName());
        return columns;
    }

    public void setColumns(ArrayList<Column> columns) {
        this.columns = columns;
    }

/// Constructor
    public BddObject() throws Exception {
        this.initColumns();
    }
/// Functions

    public void initColumns() throws Exception {
        this.setColumns(this.getAllColumns());
    }

/// Prendre des données dans la base de données avec "SELECT"
    public Object[] findAll(Connection connection, String order) throws Exception {
        String sql = "SELECT * FROM " + this.getTable() + this.predicat(); // Requete SQL avec les pedicats si nécessaire
        if (order != null) sql += " ORDER BY " + order;
        return this.getData(sql, connection);
    }

/// Tous requete peut etre en input sur cette fonction
    public Object[] getData(String query, Connection connection) throws Exception {
        Object[] objects = null;
        boolean connect = false;
        Statement statement = null;
        ResultSet result = null;
        try {
            if (connection == null) {
                connection = this.getConnection();
                connect = true;
            }

            statement = connection.createStatement();
            result = statement.executeQuery(query);
            objects = this.convertToObject(result, listColumn(query, connection), connection);
        } finally {
            if (result != null) {
                result.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect) {
                connection.close();
            }
        }
        return objects;
    }

/// Convertir les réponse SQL en Object (T[])

// TODO : Optimiser l'ecriture de cette fonction
    public Object[] convertToObject(ResultSet result, String[] attribut, Connection connection) throws Exception {
        List<Object> objects = new ArrayList<>(); // Initialisation du vector pour sauver les donnees
        while (result.next()) {
            BddObject object = this.getClass().getConstructor().newInstance(); // Nouveau instance de l'object qui hérite ce BddObject
            object.setPrimaryKeyName(this.getPrimaryKeyName());
            for (int i = 0; i < attribut.length; i++) {
                Column column = this.getColumn(attribut[i]);
                if (column != null) { // Voir si cette column existe dans cette classe
                    Object value = column.getValue(this);
                    Field field = column.getField();

                    // * Condition pour eviter la reccurence de cette fonction
                    if (value == null || Primitives.isWrapperType(value.getClass())) {
                            
                        if (column.isForeignKey()) {
                            
                            // Pour charger les objet pour les foreignKeys
                            if (column.isEnable()) {
                                BddObject fkObject = (BddObject) field.getType().getConstructor().newInstance();
                                Column primaryKey = fkObject.getFieldPrimaryKey();
                                Method setter = fkObject.getClass().getMethod("set" + toUpperCase(primaryKey.getField().getName()), primaryKey.getField().getType());
                                Object dbValue = ResultSet.class.getMethod("get" + toUpperCase(primaryKey.getField().getType().getSimpleName()), String.class).invoke(result, attribut[i]);
                                setter.invoke(fkObject, dbValue);
                                
                                // ! Probleme sur la performance
                                value = (dbValue == null) ? null : fkObject.getById(connection);
                            }

                        } else {
                            
                            // Prendre les donnees de la table par defaut
                            value = ResultSet.class.getMethod("get" + toUpperCase((field.getType().getSimpleName().equals("Integer")) ? "int" : field.getType().getSimpleName()), String.class).invoke(result, attribut[i]);
                        
                        }
                            
                    }

                    
                    Object tmp = object;
                    
                    // * Notion de set une valeur dans un objet
                    if (column.isNotInObject()) {
                        column.inside(this);
                        tmp = column.getValue(object);
                    }
                    
                    tmp.getClass().getMethod("set" + toUpperCase(field.getName()), field.getType()).invoke(tmp, value);
                }

            }
            
            // * Charger les colonnes de type Array
            for (Column column : this.getArrayColumn()) {
                ManyToMany view = column.getManyToMany();
                BddObject obj = (BddObject) column.getField().getType().getComponentType().getConstructor().newInstance();
                if (view.getView() != null && view.getView().isEmpty()) obj.setTable(view.getView());
                if (view.getColumn().isEmpty()) view.setColumn(this.getFieldPrimaryKey().getName());
                Column c = obj.getColumn(view.getColumn()); // Prendre la colonne de reference pour chercher les donnees
                obj.getClass().getMethod("set" + toUpperCase(c.getField().getName()), c.getField().getType()).invoke(obj, object);
                object.getClass().getMethod("set" + toUpperCase(column.getField().getName()), column.getField().getType()).invoke(object, (Object) obj.findAll(connection, view.getOrder()));
            }

            objects.add(object);
        }
        return objects.toArray((Object[]) Array.newInstance(this.getClass(), objects.size())); // Fonction pour creer un tableau avec le generic
    }

    /**
     * Fonction pour creer le predicat de la requête SQL
     * 
     * @return 
     * @throws Exception
     */
    public String predicat() throws Exception {
        StringBuilder sql = new StringBuilder(" WHERE "); // Condition avec AND CLAUSE
        for (Column column : this.getColumns()) {
            String predicat = column.getName();
            if (!column.isNotInObject()) {
                // Toutes les attributtes ayant une valeur non null seront sur le WHERE CLAUSE
                Object value = this.getClass().getMethod("get" + toUpperCase(column.getField().getName())).invoke(this);
                // Sauf les types primitives
                if (value != null && !Primitives.isWrapperType(value.getClass())) {
                    // Pour les types BddObject la valeur est son ID
                    if (value instanceof BddObject) {
                        BddObject obj = (BddObject) value;
                        predicat = obj.getFieldPrimaryKey().getName();
                        value = value.getClass().getMethod("get" + toUpperCase(obj.getFieldPrimaryKey().getField().getName())).invoke(obj);
                    }
                    sql.append(predicat + "=" + convertToLegal(value) + " AND ");
                }
            }
        }
        return (sql.toString().equals(" WHERE ")) ? "" : sql.substring(0, sql.length() - 5); // Delete last " AND " in sql
    }

    public List<Column> getColumnsNotNull() throws Exception {
        Vector<Column> columns = new Vector<>();
        for (Column column : this.getColumns()) {
            Object value = this.getClass().getMethod("get" + toUpperCase(column.getField().getName())).invoke(this);
            if (value != null) columns.add(column);
        }
        return columns;
    }
    
    public void insert(Connection connection, Column... args) throws Exception {
        boolean connect = false;
        try {
            if (connection == null) {
                connection = this.getConnection();
                connect = true;
            }
            
            if (Boolean.TRUE.equals(this.isSerial())) {
                Column primaryKey = this.getFieldPrimaryKey();
                Method setter = this.getClass().getMethod("set" + toUpperCase(primaryKey.getField().getName()), primaryKey.getField().getType());
                setter.invoke(this, this.getSequence().buildPrimaryKey(connection));
            }
            
            try (Statement statement = connection.createStatement()) {
                List<Column> columns = this.getColumnsNotNull();
                for (Column arg : args) columns.add(arg);
                String sql = "INSERT INTO " + this.getTable() + " " + createColumn(columns) + " VALUES ("; // Insert with all column
                for (Column colonne : columns) {
                    Object value = colonne.getValue(this);
                    sql += convertToLegal(value) + ",";
                }
                sql = sql.substring(0, sql.length() - 1) + ")";
                System.out.println( "Query is :::: " + sql );
                statement.executeUpdate(sql);
            }
            
        } finally {
            if (connect) {connection.commit(); connection.close();}
        }
    }
    
    public String createColumn(List<Column> columns) throws Exception {
        String result = "(";
        for (Column colonne : columns) {
            String name = colonne.getName();
            result += name  + ",";
        }
        result = result.substring(0, result.length() - 1) + ")";
        return result;
    }
    
    public void update(Connection connection, Column... args) throws Exception {
        boolean connect = false;
        try {
            if (connection == null) {connection = getConnection(); connect = true;}
            try (Statement statement = connection.createStatement()) {
                String sql = "UPDATE " + this.getTable() + " SET ";
                List<Column> columns = this.getColumnsNotNull();
                for (Column arg : args) columns.add(arg);
                Column primaryKey = this.getFieldPrimaryKey();
                for (Column column : columns) {
                    if (!column.getName().equals(primaryKey.getName())) sql += column.getName() + " = " + convertToLegal(column.getValue(this)) + ", ";
                }
                sql = sql.substring(0, sql.length() - 2);
                sql += " WHERE " + primaryKey.getName() + " = " + convertToLegal(primaryKey.getValue(this));
                statement.executeUpdate(sql);
            }
        } finally {
            if (connect) {connection.commit(); connection.close();}
        }
    }

    public Column getFieldPrimaryKey() throws Exception {
        for (Column field : this.getColumns()) {
            if (field.getName().equals(this.getPrimaryKeyName())) return field;
        }
        throw new Exception("Il n'y pas de cle primaire dans la classe " + this.getClass().getSimpleName());
    }

    public Object[] findAll(String order) throws Exception {
        Object[] objects = null;
        try (Connection connection = getConnection()) {
            objects = this.findAll(connection, order);
        }
        return objects;
    }

    public Object getById() throws Exception {
        Object object = null;
        try (Connection connection = getConnection()) {
            object = this.getById(connection);
        }
        return object;
    }

    public Object getById(Connection connection) throws Exception {
        Column primaryKey = this.getFieldPrimaryKey();
        if (primaryKey == null) throw new Exception("Pas de cles primaire dans cette classe " + this.getClass().getSimpleName());
        Object[] objects = this.findAll(connection, null);
        return (objects.length > 0) ? objects[0] : null;
    }

    public ArrayList<Column> getAllColumns() throws Exception {
        Class<?> c = this.getClass();
        ArrayList<Column> columns = new ArrayList<>();
        while (c != BddObject.class) {
            for (Field field : c.getDeclaredFields()) columns.add(new Column(this, field));
            c = c.getSuperclass();
        }
        Column column = new Column(this, c.getDeclaredField("id"));
        columns.add(column);
        return columns;
    }

    public ArrayList<Column> getArrayColumn() throws Exception {
        ArrayList<Column> columns = new ArrayList<>();
        for (Column c : this.getColumns()) {  
            if (c.getManyToMany() != null) columns.add(c);
        }
        return columns;
    }

    public Column getColumn(String column) throws Exception {
        for (Column c : this.getColumns()) {
            if (c.isColumn(column)) return c;
        }
        return null;
    }
    
    public boolean equals(BddObject object) throws Exception {
        if (!this.getClass().getName().equals(object.getClass().getName())) return false;
        Column primaryKey = this.getFieldPrimaryKey();
        String id1 = (String) this.getClass().getMethod("get" + toUpperCase(primaryKey.getField().getName())).invoke(this);
        String id2 = (String) object.getClass().getMethod("get" + toUpperCase(primaryKey.getField().getName())).invoke(object);
        return id1.equals(id2);
    }

    public void disableForeignKey() throws Exception {
        this.changeForeignKeyStatus(false);
    }

    public void enableForeignKey() throws Exception {
        this.changeForeignKeyStatus(true);
    }

    public void changeForeignKeyStatus(boolean status) throws Exception {
        for (Column column : this.getColumns())
            column.setEnable(status);
    }
    
    public void disableManyToMany() throws Exception {
        for (Column column : this.getColumns())
            column.setManyToMany(null);
    }

    public Formulaire createFormulaire(String action) throws Exception {
        return Formulaire.createFormulaire(this, action);
    }

    @Override
    public String toString() {
        return this.getId();
    }
    
}