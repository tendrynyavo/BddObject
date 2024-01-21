package model.immobilisation;

import java.sql.Connection;

import connection.Bdd;
import connection.BddObject;
import model.adresse.Adresse;
import model.bien.Bien;
import model.categorie.Categorie;
import model.reception.Reception;

public class Immobilisation extends BddObject {

    String nom;
    Categorie categorie;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom.isEmpty())
            throw new IllegalArgumentException("Nom est vide");
        this.nom = nom;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Immobilisation() throws Exception {
        super();
        this.setTable("immobilisation");
        this.setConnection("Oracle");
        this.setPrimaryKeyName("id_immobilisation");
    }

    public Reception recevoir(String date, String idAdresse, Connection connection) throws Exception {
        boolean connect = false;
        Reception reception = new Reception();
        reception.setDate(date);
        try {
            if (connection == null) {
                connection = this.getConnection();
                connect = true;
            }

            Immobilisation immobilisation = (Immobilisation) new Immobilisation().setId(this.getId()).getById(connection);
            if (immobilisation == null) {
                throw new IllegalArgumentException(String.format("Immobilisation %s n'existe pas", this.getId()));
            }
            
            Adresse adresse = (Adresse) new Adresse().setId(idAdresse).getById(connection);
            if (adresse == null) {
                throw new IllegalArgumentException(String.format("Adresse %s n'existe pas", idAdresse));
            }

            reception.setImmobilisation(immobilisation);
            reception.setAdresse(adresse);

            String code = reception.generateCode(connection);
            reception.setCode(code);

        } finally {
            if (connect) {
                connection.close();
            }
        }
        return reception;
    }

    public Bien recevoir(String date, String idAdresse, String designation, String idMarque, Connection connection) throws Exception {
        boolean connect = false;
        Bien bien = null;
        Reception reception = null;
        try {
            if (connection == null) {
                connection = this.getConnection();
                connect = true;
            }
            
            reception = this.recevoir(date, idAdresse, connection);
            bien = reception.recuperer(designation, idMarque, connection);
            
            reception.insert(connection);
            bien.insert(connection);
            
            if (connect) {
                connection.commit();
            }
        } catch (Exception e) {
            if (connect) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connect) {
                connection.close();
            }
        }
        return bien;
    }

    public static void main(String[] args) throws Exception {
        // Bien bien = new Reception().recevoir("IM001", "2024-01-20", "AD00001", "Maybach", "MA0001", null);
        Immobilisation immobilisation = new Immobilisation();
        immobilisation.setId("IM001");
        try (Connection connection = Bdd.getOracle()) {
            Reception reception = immobilisation.recevoir("2024-01-20", "AD00001", connection);
            reception.insert(connection);
            connection.commit();
        }
    }

}