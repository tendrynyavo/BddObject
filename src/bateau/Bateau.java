package bateau;

import connection.BddObject;
import formulaire.Formulaire;

public class Bateau extends BddObject {

    String nom;
    double profondeur;
    double remorquage;
    Pavillon pavillon;
    TypeBateau type;

    public void setType(TypeBateau type) {
        this.type = type;
    }

    public TypeBateau getType() {
        return type;
    }

    public void setNom(String nom) throws IllegalArgumentException {
        if (nom == null) throw new IllegalArgumentException("Nom est null");
        if (nom.isEmpty()) throw new IllegalArgumentException("Nom est vide");
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public void setRemorquage(double remorquage) throws IllegalArgumentException {
        if (remorquage < 0) throw new IllegalArgumentException("remorquage doit etre positif");
        this.remorquage = remorquage;
    }

    public void setRemorquage(String remorquage) throws IllegalArgumentException {
        if (remorquage == null) throw new IllegalArgumentException("Remorquage est null");
        if (remorquage.isEmpty()) throw new IllegalArgumentException("Champ remorquage est vide");
        if (!isNumeric(remorquage)) throw new IllegalArgumentException("Champ remorquage doit etre un nombre");
        this.setRemorquage(Double.parseDouble(remorquage));
    }

    public Double getRemorquage() {
        return remorquage;
    }

    public void setProfondeur(double profondeur) throws IllegalArgumentException {
        if (profondeur < 0) throw new IllegalArgumentException("Profondeur doit etre positive");
        this.profondeur = profondeur;
    }

    public void setProfondeur(String profondeur) throws IllegalArgumentException {
        if (profondeur == null) throw new IllegalArgumentException("Profondeur est null");
        if (profondeur.isEmpty()) throw new IllegalArgumentException("Champ profondeur est vide");
        if (!isNumeric(profondeur)) throw new IllegalArgumentException("Champ profondeur doit etre un nombre");
        this.setProfondeur(Double.parseDouble(profondeur));
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public double getProfondeur() {
        return profondeur;
    }

    public Pavillon getPavillon() {
        return pavillon;
    }

    public void setPavillon(Pavillon Pavillon) throws IllegalArgumentException {
        this.pavillon = Pavillon;
    }

    public Bateau(String idBateau, String nom) throws Exception {
        this(idBateau);
        this.setNom(nom);
    }

    public Bateau(String idBateau, String nom, double profondeur) throws Exception {
        this(idBateau, nom);
        this.setProfondeur(profondeur);
    }

    public Bateau(String idBateau, String nom, String profondeur) throws Exception {
        this(idBateau, nom);
        this.setProfondeur(profondeur);
    }

    public Bateau(String idBateau) throws Exception {
        this();
        this.setId(idBateau);
    }

    public Bateau(String idBateau, String nom, double profondeur, double remorquage) throws Exception {
        this(idBateau, nom, profondeur);
        this.setRemorquage(remorquage);
    }

    public Bateau(String idBateau, String nom, String profondeur, String remorquage) throws Exception {
        this(idBateau, nom, profondeur);
        this.setRemorquage(remorquage);
    }

    public Bateau() throws Exception {
        super();
        this.setTable("bateau");
        this.setPrimaryKeyName("idbateau");
        this.setCountPK(7);
        this.setFunctionPK("nextval('seq_id_bateau')");
        this.setPrefix("BAT");
        this.setConnection("PostgreSQL");
    }

    @Override
    public Formulaire createFormulaire(String error) throws Exception {
        Formulaire form = super.createFormulaire("/gestion-port/insert");
        form.setError(error);
        form.setTitle("Saisie de Bateau");
        return form;
    }

    public static void main(String[] args) throws Exception {
        for (Bateau bateau : (Bateau[]) new Bateau().findAll(null)) {
            System.out.println(bateau.getId());
            System.out.println(bateau.getPavillon().getNom());
        }
    }

}