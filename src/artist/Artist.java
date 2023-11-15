package artist;

import java.io.IOException;
import java.net.Socket;

import connection.BddObject;

public class Artist extends BddObject {

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist() throws Exception {
        super();
        this.setSerial(false);
        this.setTable("artists");
        this.setPrimaryKeyName("ArtistId");
        this.setConnection("SQLite");
    }

    public static void main(String[] args) throws Exception {
        Artist artist = new Artist();
        artist.setName("Tendry Ny Avo");
        artist.insert(null);
    }
    
}