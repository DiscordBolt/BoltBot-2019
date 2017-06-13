package net.ajpappas.discord.modules.seen;

import net.ajpappas.discord.utils.Logger;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Tony on 5/15/2017.
 */
public class StatusFileIO {

    private static final String FILE_EXT = ".json";
    protected static final Path STATUS_DIRECTORY = Paths.get(System.getProperty("user.dir"), "statuses");
    protected static final Gson gson = new Gson();

    public static void saveStatus(UserStatus userStatus) {
        try {
            Path filePath = getStatusPath(userStatus);
            Files.createDirectories(filePath.getParent());
            FileWriter fw = new FileWriter(filePath.toFile());
            fw.write(gson.toJson(userStatus));
            fw.close();
        } catch (IOException e) {
            Logger.error("Unable to write user status \"" + userStatus.getUserID() + "\" to file.");
            Logger.debug(e);
        }
    }

    public static HashMap<Long, UserStatus> loadStatuses() {
        HashMap<Long, UserStatus> statuses = new HashMap<>();
        try {
            Files.walk(STATUS_DIRECTORY).forEach(p -> {
                try {
                    if (!Files.isDirectory(p)) {
                        UserStatus us = gson.fromJson(new FileReader(p.toFile()), UserStatus.class);
                        statuses.put(us.getUserID(), us);
                    }
                } catch (FileNotFoundException e) {
                    Logger.error("Unable to load user status \"" + p.getFileName().toString() + "\"");
                    Logger.debug(e);
                }
            });
        } catch (IOException e) {
            Logger.error("Unable to walk user status directory.");
            Logger.debug(e);
        }
        return statuses;
    }

    private static Path getStatusPath(UserStatus status) {
        return Paths.get(STATUS_DIRECTORY.toString(), status.getUserID().toString() + FILE_EXT);
    }
}
