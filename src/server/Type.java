package server;

import connection.BddObject;

public class Type extends BddObject {

    String name;
    String path;
    int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Type() throws Exception {
        super();
        this.setTable("type_server");
        this.setConnection("SQLite");
        this.setPrimaryKeyName("id_type");
    }
    
}
