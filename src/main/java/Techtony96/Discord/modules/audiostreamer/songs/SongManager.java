package Techtony96.Discord.modules.audiostreamer.songs;

import Techtony96.Discord.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;

/**
 * Created by Tony on 4/15/2017.
 */
public class SongManager {

    private static final Path SONG_DIRECTORY = Paths.get(System.getProperty("user.home"), "discord", "songs");
    private static final String DELIMITER = "$";

    private HashSet<Song> songs = new HashSet<>();

    public SongManager() {
        // Init song cache
        try {
            Files.walk(SONG_DIRECTORY).forEach(p -> songs.add(new Song(getSongID(p), getSongTitle(p), p)));
        } catch (IOException e) {
            Logger.warning(e.getMessage());
            Logger.debug(e);
        }
    }

    public Song createSong(String ID) {
        return songs.stream().filter(s -> s.getId().equals(ID)).findAny().orElseGet(() -> {
            Optional<Path> path = downloadSong(ID);
            if (path.isPresent()) {
                Song s = new Song(getSongID(path.get()), getSongTitle(path.get()), path.get());
                songs.add(s);
                return s;
            }
            return null;
        });
    }

    /**
     * Find the Path of a given song ID
     *
     * @param ID YouTube video ID
     * @return Optional<Song>
     */
    public Optional<Song> findSong(String ID) {
        return songs.stream().filter(s -> s.getId().equals(ID)).findAny();
    }

    public boolean checkExists(String ID) {
        return findSong(ID).isPresent();
    }

    /**
     * Retreive the song title from the downloaded file.
     *
     * @param ID YouTube video ID
     * @return Optional<String> Title of song, if it was found.
     */
    public Optional<String> getSongTitle(String ID) {
        return findSong(ID).map(s -> s.getTitle());
    }

    public Optional<Path> downloadSong(String ID) {
        try {
            Process p = Runtime.getRuntime().exec("youtube-dl -- " + ID);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[download] Destination: "))
                    return Optional.of(Paths.get(line.replace("[download] Destination: ", "")));
            }
            Logger.severe("youtube-dl has changed message formatting. Unable to return downloaded song \"" + ID + "\"");
        } catch (IOException | InterruptedException e) {
            Logger.warning(e.getMessage());
            Logger.debug(e);
        }
        return Optional.empty();
    }

    private String getSongID(Path path) {
        return path.getFileName().toString().substring(0, path.getFileName().toString().indexOf(DELIMITER));
    }

    private String getSongTitle(Path path) {
        return path.getFileName().toString().replaceAll("(.+\\\\" + DELIMITER + ")|(\\\\..+)", "");
    }
}
