package net.ajpappas.discord.modules.tags;

import com.google.gson.Gson;
import net.ajpappas.discord.api.commands.exceptions.CommandException;
import net.ajpappas.discord.utils.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tony on 5/14/2017.
 */
public class TagFileIO {

    private static final String FILE_EXT = ".json";
    protected static final Path TAG_DIRECTORY = Paths.get(System.getProperty("user.dir"), "tags");
    protected static final Gson gson = new Gson();

    private static Path getTagPath(Tag tag) {
        return Paths.get(TAG_DIRECTORY.toString(), tag.getGuildID().toString() + "-" + tag.getTag() + FILE_EXT);
    }

    protected static void saveToFile(Tag tag) throws CommandException {
        try {
            Path filePath = getTagPath(tag);
            Files.createDirectories(filePath.getParent());
            FileWriter fw = new FileWriter(filePath.toFile());
            fw.write(gson.toJson(tag));
            fw.close();
        } catch (IOException e) {
            Logger.error("Unable to write tag \"" + tag.getTag() + "\" to file.");
            Logger.debug(e);
            throw new CommandException("Unable to update your tag.");
        }
    }

    protected static void deleteFile(Tag tag) throws CommandException {
        try {
            Files.delete(getTagPath(tag));
        } catch (IOException e) {
            Logger.error("Unable to delete tag \"" + tag.getTag() + "." + FILE_EXT + "\"  file.");
            Logger.debug(e);
            throw new CommandException("Unable to delete your tag.");
        }
    }

    protected static void loadTags() {
        try {
            if (!TAG_DIRECTORY.toFile().exists())
                Files.createDirectories(TAG_DIRECTORY);
            Files.walk(TAG_DIRECTORY).forEach(p -> {
                try {
                    if (!Files.isDirectory(p))
                        TagModule.tags.add(gson.fromJson(new FileReader(p.toFile()), Tag.class));
                } catch (FileNotFoundException e) {
                    Logger.error("Unable to load tag \"" + p.getFileName().toString() + "\"");
                    Logger.debug(e);
                }
            });
        } catch (IOException e) {
            Logger.error("Unable to walk tag directory.");
            Logger.debug(e);
        }
    }
}
