package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import connection.BddObject;
import connection.annotation.ColumnName;
import cluster.Cluster;

public class Server extends BddObject {

    String name;
    String ip;
    String path;
    Type type;
    @ColumnName("id_project")
    String project;
    @ColumnName("id_cluster")
    Cluster cluster;
    int port;
    Configuration[] configurations;

    public void setPort(String port) {
        this.setPort(Integer.parseInt(port));
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setType(String type) throws Exception {
        this.setType((Type) new Type().setId(type));
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public void setIp(String ip) throws IllegalArgumentException {
        if (ip.isEmpty()) throw new IllegalArgumentException("IP est vide");
        this.ip = ip;
    }

    public void setName(String name) throws IllegalArgumentException {
        if (name.isEmpty()) throw new IllegalArgumentException("Name est vide");
        this.name = name;
    }

    public Configuration[] getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Configuration[] configurations) {
        this.configurations = configurations;
    }

    public Server() throws Exception {
        super();
        this.setSerial(false);
        this.setTable("server");
        this.setConnection("SQLite");
        this.setPrimaryKeyName("id_server");
    }

    public Server(String name, String ip) throws Exception {
        this();
        this.setName(name);
        this.setIp(ip);
    }

    public Server(String id, String type, String path) throws Exception {
        this();
        this.setId(id);
        this.setType(type);
        this.setPath(path);
    }

    public boolean hostAvailabilityCheck(int port) { 
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(this.getIp(), port), 50);
            return true;
        } catch (IOException ex) {

        }
        return false;
    }

    public String getStatusSSH() {
        return (this.hostAvailabilityCheck(this.getConfigurations()[0].getPort())) ? "active" : "disconnected";
    }

    public String getStatusSSHColor() {
        return (this.hostAvailabilityCheck(this.getConfigurations()[0].getPort())) ? "success" : "danger";
    }

    public String getStatusFTP() {
        return (this.hostAvailabilityCheck(this.getConfigurations()[1].getPort())) ? "active" : "disconnected";
    }

    public String getStatusFTPColor() {
        return (this.hostAvailabilityCheck(this.getConfigurations()[1].getPort())) ? "success" : "danger";
    }

    public static void updateServer(String id, String name, String ip) throws Exception {
        Server server = new Server(name, ip);
        server.setId(id);
        server.update(null);
    }

    public Server[] getServersConfiguration() throws Exception {
        try (Connection connection = this.getConnection()) {
            Server[] servers = (Server[]) new Server().findAll(connection, null);
            for (Server server : servers) {
                Configuration config = (Configuration) new Configuration().setTable("v_configuration");
                config.setServer(server);
                server.setConfigurations((Configuration[]) config.findAll(connection, null));
            }
            return servers;
        }
    }

}