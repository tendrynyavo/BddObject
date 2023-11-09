package sortie;

import entree.Entree;

public class Mouvement extends Sortie {

    Entree entree;

    public Entree getEntree() {
        return entree;
    }

    public void setEntree(Entree entree) {
        this.entree = entree;
    }

    public Mouvement() throws Exception {
        super();
        this.setTable("mouvement");
        this.setPrimaryKeyName("id_sortie");
        this.setSerial(false);
        this.setConnection("PostgreSQL");
    }

    public Mouvement(String sortie, Entree entree, double quantite) throws Exception {
        this();
        this.setId(sortie);
        this.setEntree(entree);
        this.setQuantite(quantite);
    }
    
}
