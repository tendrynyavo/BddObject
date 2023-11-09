package connection;

import java.lang.reflect.Field;

import connection.annotation.*;

public class Column {
    
    String name;
    Field field;
    Object value;
    boolean primaryKey = false, foreignKey = false, notInObject = false, enable = true;
    Column inside;
    ManyToMany manyToMany;

    public ManyToMany getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(ManyToMany manyToMany) {
        this.manyToMany = manyToMany;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Column getInside() {
        return inside;
    }

    public void setInside(Column inside) {
        this.inside = inside;
    }

    public boolean isNotInObject() {
        return notInObject;
    }

    public void setNotInObject(boolean notInObject) {
        this.notInObject = notInObject;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setForeignKey(boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    public boolean isForeignKey() {
        return foreignKey;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public Column(String colonne, Object object) {
        this.setName(colonne);
        this.setValue(object);
    }

    public Column(String colonne, Column inside, String insideColonne) throws Exception {
        this.setName(colonne);
        this.setInside(inside);
        BddObject obj = (BddObject) inside.getField().getType().getConstructor().newInstance();
        Column column = obj.getColumn(insideColonne);
        this.setForeignKey(column.isForeignKey());
        this.setField(column.getField());
        this.setNotInObject(true);
    }

    public Column(BddObject obj, Field field) throws Exception {
        this.setField(field);
        this.setName((field.isAnnotationPresent(ColumnName.class)) ? field.getAnnotation(ColumnName.class).value() : field.getName());
        if (isBddObjectType(field.getType()) && !field.getType().isArray()) {
            this.setForeignKey(true);
            if (!(field.isAnnotationPresent(ColumnName.class))) {
                String name = (field.getType().isAssignableFrom(obj.getClass())) ? obj.getPrimaryKeyName() : ((BddObject) field.getType().getConstructor().newInstance()).getPrimaryKeyName();
                this.setName(name);
            }
        }
        if (field.isAnnotationPresent(Reference.class)) {
            Reference reference = field.getAnnotation(Reference.class);
            ManyToMany manyToMany = new ManyToMany(reference.value());
            if (!reference.table().isEmpty()) manyToMany.setView(reference.table());
            this.setManyToMany(manyToMany);
        }
    }

    public boolean isColumn(String column) throws Exception {
        return column.toLowerCase().equals(this.getName().toLowerCase());
    }

    public Object getValue(Object instance) throws Exception {
        return (this.getValue() != null) ? this.getValue() : instance.getClass().getMethod("get" + BddObject.toUpperCase(this.getField().getName())).invoke(instance);
    }

    public void inside(BddObject object) throws Exception {
        this.setField(this.getInside().getField());
    }

    // Fonction pour vérifier la class si c'est un BddObject
    public static boolean isBddObjectType(Class<?> c) {
        return BddObject.class.isAssignableFrom(c);
    }

}
