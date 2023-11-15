package project;

import connection.BddObject;
import server.Configuration;

public class Project extends BddObject {
    
    String name;
    String url;
    String branch;
    String script;
    String target;
    Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setConfiguration(String configuration) throws Exception {
        Configuration config = new Configuration();
        config.setId(configuration);
        this.setConfiguration(config);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public String getBranch() {
        return branch;
    }

    public String getScript() {
        return script;
    }

    public String getTarget() {
        return target;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Project() throws Exception {
        super();
        this.setSerial(false);
        this.setTable("project");
        this.setPrimaryKeyName("id_project");
        this.setConnection("SQLite");
    }

    public Project(String name, String url, String branch, String script, String target, String methode) throws Exception {
        this();
        this.setName(name);
        this.setUrl(url);
        this.setBranch(branch);
        this.setScript(script);
        this.setTarget(target);
        this.setConfiguration(methode);
    }

    public static void addProject(String name, String url, String branch, String script, String target, String methode) throws Exception {
        Project project = new Project(name, url, branch, script, target, methode);
        project.insert(null);
    }

    public static void main(String[] args) throws Exception {
        Project.addProject("Gestion Stock", "https://github.com/tendrynyavo/gestion-stock.git", "main", "compile.bat", "target", "1");
    }

}