package bateau;

import connection.BddObject;

public class TypeBateau extends BddObject {
    
    String nom;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) throws IllegalArgumentException {
        if (nom == null) throw new IllegalArgumentException("Nom du type de bateau est null");
        if (nom.isEmpty()) throw new IllegalArgumentException("Nom du type de bateau est vide");
        this.nom = nom;
    }

    public TypeBateau() throws Exception {
        super();
        this.setTable("type");
        this.setPrimaryKeyName("idtype");
        this.setConnection("PostgreSQL");
    }

}
