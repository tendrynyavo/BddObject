package bateau;

import connection.BddObject;

public class Pavillon extends BddObject {

    String nom;
    Devise devise;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) throws IllegalArgumentException {
        if (nom == null) throw new IllegalArgumentException("Nom est null");
        if (nom.isEmpty()) throw new IllegalArgumentException("Nom est vide");
        this.nom = nom;
    }

    public void setDevise(Devise devise) throws IllegalArgumentException {
        if (devise == null) throw new IllegalArgumentException("idPavillon est null");
        this.devise = devise;
    }

    public Devise getDevise() {
        return devise;
    }

    public Pavillon() throws Exception {
        super();
        this.setTable("pavillon");
        this.setPrimaryKeyName("idpavillon");
        this.setConnection("PostgreSQL");
    }
    
}
