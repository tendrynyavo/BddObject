package model.bien;

import model.marque.Marque;
import model.reception.Reception;

public class Bien extends Reception {

    String nom;
    Marque marque;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom.isEmpty())
            throw new IllegalArgumentException("Nom est vide");
        this.nom = nom;
    }

    public Marque getMarque() {
        return marque;
    }

    public void setMarque(Marque marque) {
        this.marque = marque;
    }

    public Bien() throws Exception {
        super();
        this.setTable("bien");
        this.setConnection("Oracle");
        this.setPrimaryKeyName("code");
        this.setSerial(false);
    }
    
}