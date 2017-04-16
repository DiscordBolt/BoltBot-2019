package Techtony96.Discord.modules.audiostreamer.songs;

import java.nio.file.Path;

/**
 * Created by Tony on 4/15/2017.
 */
public class Song {

    private String title, id;
    private Path path;

    Song(String id, String title, Path path) {
        this.id = id;
        this.title = title;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public Path getPath() {
        return path;
    }
}
