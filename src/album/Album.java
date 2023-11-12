package album;

import artist.Artist;
import connection.BddObject;

public class Album extends BddObject {

    String title;
    Artist artist;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    
    public Album() throws Exception {
        super();
        this.setConnection("SQLite");
        this.setTable("albums");
        this.setPrimaryKeyName("AlbumId");
    }
    
}
