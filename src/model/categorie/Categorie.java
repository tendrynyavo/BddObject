package model.categorie;

import connection.BddObject;

public class Categorie extends BddObject {

    String nom;
    String code;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom.isEmpty())
            throw new IllegalArgumentException("Nom est vide");
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code.isEmpty())
            throw new IllegalArgumentException("Code est vide");
        this.code = code;
    }

    public Categorie() throws Exception {
        super();
        this.setTable("categorie");
        this.setConnection("Oracle");
        this.setPrimaryKeyName("id_categorie");
    }
    
}
