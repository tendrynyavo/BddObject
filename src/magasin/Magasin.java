package magasin;

import connection.BddObject;

public class Magasin extends BddObject {

    String nom;
    
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public Magasin() throws Exception {
        super();
        this.setTable("magasin");
        this.setPrimaryKeyName("id_magasin");
        this.setConnection("PostgreSQL");
    }

    public Magasin(String id) throws Exception {
        super();
        this.setId(id);
    }

}
