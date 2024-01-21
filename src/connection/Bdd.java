package connection;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import connection.xml.XmlConnection;

public class Bdd implements Serializable {
    
    @JsonIgnore
    transient Sequence sequence = new Sequence();
    @JsonIgnore
    transient String connection;
    @JsonIgnore
    transient String primaryKeyName;
    @JsonIgnore
    transient String table; // table de cette object

    public Sequence getSequence() {
        return sequence;
    }

    public Bdd setSequence(Sequence sequence) {
        this.sequence = sequence;
        return this;
    }

    public Bdd setConnection(String connection) {
        this.connection = connection;
        return this;
    }

    public Connection getConnection() throws Exception {
        Connection c = null;
        if (this.connection == null || this.connection.isEmpty()) throw new Exception("Connection is not define in " + this.getClass().getName());
        switch (this.connection) {
            case "PostgreSQL":
                c = getPostgreSQL();
                break;
            case "Oracle":
                c = getOracle();
                break;
            case "MySQL":
                c = getMySQL();
                break;
            case "SQLite":
                c = getSQLite();
                break;
            default:
                throw new IllegalArgumentException("Connection not found");
        }
        return c;
    }

    public Bdd setPrimaryKeyName(String primaryKey) {
        this.primaryKeyName = primaryKey;
        return this;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public Bdd setTable(String table) { 
        this.table = table;
        return this;
    }

    public String getTable() {
        if (this.table == null) throw new NullPointerException("Pas de table pour l'object de type " + this.getClass().getSimpleName());
        return table;
    }

    /// Fonction pour prendre un connexion en Oracle
    // ! Configuration de la base de donnee dans un fichier xml
    public static Connection getOracle() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        XmlConnection config = XmlConnection.createConnection("Oracle");
        String configuration = String.format("jdbc:oracle:thin:@%s:%s:orcl", config.getHost(), config.getPort());
        Connection connection = DriverManager.getConnection(configuration, config.getUser(), config.getPassword());
        connection.setAutoCommit(false);
        return connection;
    }

/// Fonction pour prendre un connexion en PostgreSQL
    public static Connection getPostgreSQL() throws Exception {
        Class.forName("org.postgresql.Driver");
        XmlConnection config = XmlConnection.createConnection("PostgreSQL");
        String configuration = String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s", config.getHost(), config.getPort(), config.getDatabase(), config.getUser(), config.getPassword());
        Connection connection = DriverManager.getConnection(configuration);
        connection.setAutoCommit(false);
        return connection;
    }
    
/// Fonction pour prendre un connexion en PostgreSQL
    public static Connection getSQLServer() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        XmlConnection config = XmlConnection.createConnection("ServerSQL");
        String configuration = String.format("jdbc:sqlserver://%s/%s", config.getHost(), config.getDatabase());
        Connection connection = DriverManager.getConnection(configuration, config.getUser(), config.getPassword());
        connection.setAutoCommit(false);
        return connection;
    }
    
/// Fonction pour prendre un connexion en PostgreSQL
    public static Connection getSQLite() throws Exception {
        Class.forName("org.sqlite.JDBC");
        XmlConnection config = XmlConnection.createConnection("SQLite");
        // db parameters
        String url = String.format("jdbc:sqlite:%s", config.getDatabase());
        // create a connection to the database
        Connection connection = null;
        connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
        return connection;
    }

    public static Connection getMySQL() throws Exception {
        Class.forName("org.mariadb.jdbc.Driver");
        XmlConnection config = XmlConnection.createConnection("MySQL");
        String configuration = String.format("jdbc:mariadb://%s:%s/%s", config.getHost(), config.getPort(), config.getDatabase());
        Connection connection = DriverManager.getConnection(configuration, config.getUser(), config.getPassword());
        connection.setAutoCommit(false);
        return connection;
    }

    /// Fonction pour prendre les listes de colonnes dans un requete
    public static String[] listColumn(String query, Connection connection) throws Exception {
        String[] colonnes = null;
        try (Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query)) {
            ResultSetMetaData rsMetaData = result.getMetaData(); // Classe avec des données plus détaillé de la requete
            int count = rsMetaData.getColumnCount();
            colonnes = new String[count];
            int increment = 0;
            for(int i = 1; i <= count; i++) {
                colonnes[increment] = rsMetaData.getColumnName(i);
                increment++;
            }
        }
        return colonnes;
    }

    public String convertToLegal(Object args) throws Exception {
        return (args == null) ? "null"
        : (args.getClass() == java.util.Date.class) ? "TO_TIMESTAMP('"+ new java.sql.Timestamp(((java.util.Date) args).getTime()) +"', 'YYYY-MM-DD HH24:MI:SS.FF')"
        : (args.getClass() == Date.class) ? "TO_DATE('" + args + "', 'YYYY-MM-DD')"
        : (args.getClass() == Timestamp.class) ? "TO_TIMESTAMP('"+ args +"', 'YYYY-MM-DD HH24:MI:SS.FF')"
        : ((args.getClass() == String.class) || (args.getClass() == Time.class)) ? "'"+ args +"'"
        : (Number.class.isAssignableFrom(args.getClass())) ? args.toString()
        : (BddObject.class.isAssignableFrom(args.getClass())) ? "'" + convertToLegalBddObject((BddObject) args) + "'"
        : "'" + args.toString() + "'";
    }

    public String convertToLegalBddObject(BddObject args) throws Exception {
        Column primaryKey = args.getFieldPrimaryKey();
        Method getter = args.getClass().getMethod("get" + primaryKey.getField().getName().substring(0, 1).toUpperCase()  + primaryKey.getField().getName().substring(1));
        return (String) getter.invoke(args);
    }

    public static String toUpperCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}