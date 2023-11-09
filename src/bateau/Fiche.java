package bateau;

import connection.BddObject;
import connection.annotation.ColumnName;

import java.sql.Connection;
import java.sql.Date;

public class Fiche extends BddObject {

    Date debut, fin;
    @ColumnName("date_creation")
    Date creation;
    double salaire, montant;
    @ColumnName("idParent")
    Fiche fiche;
    Fiche[] fiches;

    public Fiche getFiche() {
        return fiche;
    }

    public Fiche setFiche(Fiche fiche) {
        this.fiche = fiche;
        return this;
    }

    public double getMontant() {
        return montant;
    }

    public Fiche setMontant(double montant) {
        this.montant = montant;
        return this;
    }

    public Fiche[] getFiches() {
        return fiches;
    }

    public Fiche setFiches(Fiche[] fiches) {
        this.fiches = fiches;
        return this;
    }

    public Date getCreation() {
        return creation;
    }

    public Fiche setCreation(Date creation) {
        this.creation = creation;
        return this;
    }

    public Date getDebut() {
        return debut;
    }

    public Fiche setDebut(Date debut) {
        this.debut = debut;
        return this;
    }

    public Date getFin() {
        return fin;
    }

    public Fiche setFin(Date fin) {
        this.fin = fin;
        return this;
    }

    public double getSalaire() {
        return salaire;
    }

    public Fiche setSalaire(double salaire) {
        this.salaire = salaire;
        return this;
    }

    public Fiche() throws Exception {
        this.setTable("fiche");
        this.setFunctionPK("nextval('sFiche')");
        this.setPrimaryKeyName("idFiche");
        this.setConnection("PostgreSQL");
        this.setPrefix("FH");
        this.setCountPK(5);
    }

    public Fiche(String designation, double montant) throws Exception {
        this.setId(designation);
        this.setMontant(montant);
    }

    public void ajouterDesignation(Fiche fiche, Connection connection) throws Exception {
        fiche.setTable("detailfiche");
        fiche.setPrimaryKeyName("idDesignation");
        fiche.setSerial(false);
        fiche.insert(connection);
    }

    public void ajouterDesignation(Fiche fiche) throws Exception {
        try (Connection connection = this.getConnection()) {
            this.ajouterDesignation(fiche, connection);
        }
    }

    public void ajouterPrime(double rendement, double anciennete) throws Exception {
        Fiche[] fiches = new Fiche[2];
        fiches[0] = new Fiche("DES0003", rendement);
        fiches[1] = new Fiche("DES0004", anciennete);
        Connection connection = null;
        try {
            connection = this.getConnection();
            for (Fiche fiche : fiches) {
                this.ajouterDesignation(fiche, connection);
            }
            connection.commit();
        } catch (Exception e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (connection != null) connection.close();
        }
    }

    public Fiche getById() throws Exception {
        Fiche f = null;
        try (Connection connection = this.getConnection()) {
            f = (Fiche) super.getById(connection);
            Fiche[] details = (Fiche[]) ((BddObject) new Fiche().setPrimaryKeyName("idDesignation").setTable("v_designation")).findAll(connection, null);
            f.setFiches(details);
        }
        return f;
    }

    public static void main(String[] args) throws Exception {
        try (Connection connection = new Fiche().getConnection()) {
            Fiche fiche = (Fiche) new Fiche().setId("").getById(connection);
        }
    }

}
