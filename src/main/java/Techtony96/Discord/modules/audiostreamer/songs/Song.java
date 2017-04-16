package Techtony96.Discord.modules.audiostreamer.songs;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tony on 4/15/2017.
 */
public class Song {

    private String title, id, path;

    Song(String id, String title, Path path) {
        this.id = id;
        this.title = title;
        this.path = path.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public Path getPath() {
        return Paths.get(path);
    }
}
