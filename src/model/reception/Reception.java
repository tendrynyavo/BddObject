package model.reception;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import connection.BddObject;
import connection.Sequence;
import connection.annotation.ColumnName;
import model.adresse.Adresse;
import model.bien.Bien;
import model.immobilisation.Immobilisation;
import model.marque.Marque;

public class Reception extends BddObject {

    Immobilisation immobilisation;
    @ColumnName("date_reception")
    Date date;
    Adresse adresse;
    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code == null || code.isEmpty())
            throw new IllegalArgumentException("Code est vide");
        this.code = code;
    }

    public Immobilisation getImmobilisation() {
        return immobilisation;
    }

    public void setImmobilisation(Immobilisation immobilisation) {
        this.immobilisation = immobilisation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        if (date.before(new Date(System.currentTimeMillis())))
            throw new IllegalArgumentException("Date est invalide");
        this.date = date;
    }

    public void setDate(String date) {
        if (date.isEmpty())
            throw new IllegalArgumentException("Date est vide");
        this.setDate(Date.valueOf(date));
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

     public static String toFormatString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String day = Integer.toString(calendar.get(Calendar.DATE));
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        if (month.length() < 2) month = "0" + month;
        if (day.length() < 2) day = "0" + day;
        return month + day + year;
    }

    public String generateCode(Connection connection) throws SQLException {
        Sequence sequence = new Sequence();
        sequence.setCountPK(5);
        sequence.setFunctionPK("seq_bien.nextval");
        sequence.setPrefix("");
        return toFormatString(this.getDate()) + this.getImmobilisation().getCategorie().getCode() + this.getAdresse().getNom().substring(0, 4) + sequence.buildPrimaryKey(connection);
    }

    public Reception() throws Exception {
        super();
        this.setTable("reception");
        this.setPrimaryKeyName("id_reception");
        this.setConnection("Oracle");
        this.setFunctionPK("seq_reception.nextval");
        this.setCountPK(7);
        this.setPrefix("REC");
    }

    public Bien recuperer(String designation, String idMarque, Connection connection) throws Exception {
        boolean connect = false;
        Bien bien = new Bien();
        bien.setNom(designation);
        try {
            if (connection == null) {
                connection = this.getConnection();
                connect = true;
            }

            Marque marque = (Marque) new Marque().setId(idMarque).getById(connection);
            if (marque == null) {
                throw new IllegalArgumentException(String.format("Marque %s n'existe pas", idMarque));
            }

            String code = this.generateCode(connection);
            this.setCode(code);
            bien.setCode(code);
            bien.setMarque(marque);

        } finally {
            if (connect) {
                connection.close();
            }
        }
        return bien;
    }

    public Bien recevoir(String idImmobilisation, String date, String idAdresse, String designation, String idMarque, Connection connection) throws Exception {
        Immobilisation immobilisation = new Immobilisation();
        immobilisation.setId(idImmobilisation);
        return immobilisation.recevoir(date, idAdresse, designation, idMarque, connection);
    }
    
}