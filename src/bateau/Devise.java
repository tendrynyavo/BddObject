package bateau;

import connection.BddObject;

public class Devise extends BddObject {

    String valeur;

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) throws IllegalArgumentException {
        if (valeur == null) throw new IllegalArgumentException("Le champ valeur est null");
        if (valeur.isEmpty()) throw new IllegalArgumentException("Le champ valeur est vide");
        this.valeur = valeur;
    }

    public Devise() throws Exception {
        super();
        this.setTable("devise");
        this.setPrimaryKeyName("iddevise");
    }

}
