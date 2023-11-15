package cluster;

import server.Server;

public class Cluster extends Server {

    String mode;
    String balance;
    Server[] servers;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setServers(Server[] servers) {
        this.servers = servers;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalance() {
        return balance;
    }

    public Server[] getServers() {
        return servers;
    }

    public Cluster() throws Exception {
        super();
        this.setTable("cluster");
        this.setSerial(false);
        this.setPrimaryKeyName("id_cluster");
        this.setConnection("SQLite");
    }

    public Cluster(String name, String server, String port, String mode, String balance) throws Exception {
        this();
        this.setId(server);
        this.setName(name);
        this.setMode(mode);
        this.setPort(port);
        this.setBalance(balance);
    }

    public static void createCluster(String name, String server, String port, String mode, String balance) throws Exception {
        Cluster cluster = new Cluster(name, server, port, mode, balance);
        cluster.setPrimaryKeyName("id_server");
        cluster.insert(null);
    }

    public static Cluster[] getClusters() throws Exception {
        Cluster cluster = new Cluster();
        cluster.setTable("v_cluster");
        return (Cluster[]) cluster.findAll(null);
    }

    public String getStatusHaproxy() {
        return (this.hostAvailabilityCheck(this.getPort())) ? "active" : "disconnected";
    }

    public String getStatusHaproxyColor() {
        return (this.hostAvailabilityCheck(this.getPort())) ? "success" : "danger";
    }

    public static void addServer(String id, String server, String port) throws Exception {
        Cluster cluster = new Cluster();
        cluster.setId(id);
        Server s = new Server();
        s.setId(server);
        s.setPort(port);
        cluster.addServer(s);
    }

    public void addServer(Server server) throws Exception {
        server.setTable("cluster_server");
        server.setCluster(this);
        server.insert(null);
    }

    public static void main(String[] args) throws Exception {
        Server.updateServer("6", "Ubuntu-server", "192.168.88.13");
    }
    
}