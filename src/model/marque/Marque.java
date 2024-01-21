package model.marque;

import connection.BddObject;
import model.immobilisation.Immobilisation;

public class Marque extends BddObject {

    String nom;
    Immobilisation immobilisation;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Immobilisation getImmobilisation() {
        return immobilisation;
    }

    public void setImmobilisation(Immobilisation immobilisation) {
        this.immobilisation = immobilisation;
    }

    public Marque() throws Exception {
        super();
        this.setTable("marque");
        this.setPrimaryKeyName("id_marque");
        this.setConnection("Oracle");
    }

}
