package sortie;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import connection.BddObject;
import connection.annotation.ColumnName;
import article.Article;
import entree.Entree;
import magasin.Magasin;

public class Sortie extends BddObject {

    @ColumnName("date_sortie")
    Date date;
    double quantite;
    Magasin magasin;
    Article article;

    public Date getDate() {
        return date;
    }

    public double getQuantite() {
        return quantite;
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

    public void setDate(String date) {
        this.setDate(Date.valueOf(date));
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public void setQuantite(String quantite) {
        this.setQuantite(Double.parseDouble(quantite));
    }

    public void setMagasin(Magasin magasin) {
        this.magasin = magasin;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
    
    public Sortie() throws Exception {
        super();
        this.setTable("sortie");
        this.setPrimaryKeyName("id_sortie");
        this.setPrefix("S");
        this.setCountPK(5);
        this.setFunctionPK("nextval('seq_sortie')");
        this.setConnection("PostgreSQL");
    }

    public Sortie(String date, Article article, String quantite, String magasin) throws Exception {
        this();
        this.setDate(date);
        this.setQuantite(quantite);
        this.setMagasin(new Magasin(magasin));
        this.setArticle(article);
    }

    public void sortir(String date, String article, String quantite, String magasin) throws Exception {
        Connection connection = null;
        try {
            connection = this.getConnection();
            Mouvement[] mouvements = sortir(date, article, quantite, magasin, connection);
            for (Mouvement mouvement : mouvements) {
                mouvement.insert(connection);
            }
            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public Mouvement[] sortir(String date, String article, String quantite, String magasin, Connection connection) throws Exception {
        Article[] articles = (Article[]) new Article().setCode(article).findAll(connection, null);
        if (articles.length == 0) throw new IllegalArgumentException(String.format("Article avec le code %s n'existe pas", article));
        Sortie sortie = new Sortie(date, articles[0], quantite, magasin);
        sortie.insert(connection);
        String type = (articles[0].getType() == 1) ? "DESC" : "ASC";
        Entree[] etats = (Entree[]) ((BddObject) new Entree().setTable(String.format("v_etat_stock WHERE date_entree <= '%s'", date))).findAll(connection, "date_entree " + type);
        double s = sortie.getQuantite();
        List<Mouvement> mouvements = new ArrayList<>();
        for (Entree entree : etats) {
            s -= entree.getQuantite();
            if (s <= 0) {
                mouvements.add(new Mouvement(sortie.getId(), entree, -s));
                return mouvements.toArray(new Mouvement[0]);
            }
            mouvements.add(new Mouvement(sortie.getId(), entree, entree.getQuantite()));
        }
        throw new Exception("Stock insuffisant");
    }

    public static void main(String[] args) throws Exception {
        new Sortie().sortir("2021-12-04", "RR", "400", "MG001");
    }

}