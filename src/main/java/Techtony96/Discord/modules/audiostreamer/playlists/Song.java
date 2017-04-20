package Techtony96.Discord.modules.audiostreamer.playlists;

/**
 * Created by Tony on 4/15/2017.
 */
public class Song {

    private String id, title = "";

    /**
     * Create a new song with a given ID
     *
     * @param id the unique identifier of a song. For example, a YouTube URL
     */
    Song(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Song) {
            Song that = (Song) obj;
            return this.getId() == that.getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
