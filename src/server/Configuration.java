package server;

import connection.BddObject;

public class Configuration extends BddObject {

    String type;
    String name;
    String password;
    int port;
    Server server;

    public void setPort(int port) {
        this.port = port;
    }

    public void setPort(String port) {
        this.setPort(Integer.parseInt(port));
    }

    public int getPort() {
        return port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Server getServer() {
        return server;
    }
    
    public void setServer(Server server) {
        this.server = server;
    }

    public Configuration() throws Exception {
        super();
        this.setSerial(false);
        this.setTable("configuration");
        this.setConnection("SQLite");
        this.setPrimaryKeyName("id_configuration");
    }

    public Configuration(String id, String user, String password, String port, Server server) throws Exception {
        this();
        this.setId(id);
        this.setName(user);
        this.setPort(port);
        this.setPassword(password);
        this.setServer(server);
    }
    
}