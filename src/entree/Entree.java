package entree;

import java.sql.Date;

import connection.BddObject;
import connection.annotation.ColumnName;
import article.Article;
import magasin.Magasin;

public class Entree extends BddObject {

    @ColumnName("date_entree")
    Date date;
    double quantite;
    @ColumnName("prix_unitaire")
    double prixUnitaire;
    Magasin magasin;
    Article article;

    public Date getDate() {
        return date;
    }

    public double getQuantite() {
        return quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public Magasin getMagasin() {
        return magasin;
    }

    public Article getArticle() {
        return article;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public void setMagasin(Magasin magasin) {
        this.magasin = magasin;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Entree() throws Exception {
        super();
        this.setTable("entree");
        this.setPrimaryKeyName("id_entree");
        this.setConnection("PostgreSQL");
    }
    
}
