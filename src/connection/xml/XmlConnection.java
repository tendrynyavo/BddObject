package connection.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class XmlConnection {
    
    String host;
    String port;
    String database;
    String user;
    String password;

    public void setHost(String host) throws Exception {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(String port) throws Exception {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public void setDatabase(String database) throws Exception {
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public void setPassword(String password) throws Exception {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUser(String user) throws Exception {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public XmlConnection(String host, String port, String database, String user, String password) throws Exception {
        this.setHost(host);
        this.setPort(port);
        this.setDatabase(database);
        this.setUser(user);
        this.setPassword(password);
    }

    public static XmlConnection createConnection(String product) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        URL root = Thread.currentThread().getContextClassLoader().getResource("config.xml");
        Document document = db.parse(root.getFile()); // Fichier de configuration .xml
        document.getDocumentElement().normalize(); // Pour normaliser les textes des elements
        NodeList list = document.getElementsByTagName("connection"); // root du fichier .xml
        for (int temp = 0; temp < list.getLength(); temp++) {
            Node node = list.item(temp);
            Element element = (Element) node;
            String name = element.getAttribute("name"); // Nom de la base de donnee
            if (name.equals(product)) {
                String host = element.getElementsByTagName("host").item(0).getTextContent();
                String port = element.getElementsByTagName("port").item(0).getTextContent();
                String database = element.getElementsByTagName("database").item(0).getTextContent();
                String user = element.getElementsByTagName("user").item(0).getTextContent();
                String password = element.getElementsByTagName("password").item(0).getTextContent();
                return new XmlConnection(host, port, database, user, password);
            }
        }
        throw new Exception("Configuration de Base de donnee introuvable");
    }

}
