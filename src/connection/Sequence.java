package connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sequence {
    
    // Sequence
    String prefix; // Prefix de L'ID de cette Object
    String functionPK; // fonction PlSQL pour prendre la sequence
    int countPK; // nombre de caractere de l'ID

    public String getPrefix() { 
        if (prefix == null) this.setPrefix("");
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setCountPK(int countPK) throws IllegalArgumentException {
        if (countPK < 0) throw new IllegalArgumentException("Count ne doit pas etre négative de type " + this.getClass().getSimpleName());
        this.countPK = countPK;
    }

    public void setFunctionPK(String function) { this.functionPK = function; }

    public String getFunctionPK() throws NullPointerException {
        if (functionPK == null) throw new NullPointerException("Pas de fonction de sequence pour l'object de type " + this.getClass().getSimpleName());
        return functionPK;
    }
    
    public int getCountPK() { return countPK; }

    public String buildPrimaryKey(Connection connection) throws SQLException {
        return (this.getPrefix() == null) ? this.getSequence(connection) : this.getPrefix() + completeZero(getSequence(connection), this.getCountPK() - this.getPrefix().length());
    }

    public String getSequence(Connection connection) throws SQLException {
        String sql = (connection.getMetaData().getDatabaseProductName().equals("PostgreSQL")) 
                    ? "SELECT " + this.getFunctionPK() 
                    : "SELECT " + this.getFunctionPK() + " FROM DUAL";
        String sequence = null;
        try (Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql)) {
            result.next();
            sequence = result.getString(1);
        }
        return sequence;
    }

    public static String completeZero(String seq, int count) {
        int length = count - seq.length();
        String zero = "";
        for (int i = 0; i < length; i++) zero += "0";
        return zero + seq;
    }

    public Sequence(String prefix, int count, String function) throws IllegalArgumentException {
        this.setPrefix(prefix);
        this.setCountPK(count);
        this.setFunctionPK(function);
    }

    public Sequence() {

    }

}
