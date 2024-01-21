package model.adresse;

import connection.BddObject;

public class Adresse extends BddObject {

    String nom;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom.isEmpty())
            throw new IllegalArgumentException("Nom est vide");
        this.nom = nom;
    }

    public Adresse() throws Exception {
        super();
        this.setTable("adresse");
        this.setPrimaryKeyName("id_addresse");
        this.setConnection("Oracle");
    }
    
}
