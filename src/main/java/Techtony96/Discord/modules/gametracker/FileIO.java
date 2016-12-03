package Techtony96.Discord.modules.gametracker;

import Techtony96.Discord.modules.gametracker.exceptions.FileOperationException;
import Techtony96.Discord.modules.gametracker.exceptions.GameIndexDoesNotExistException;
import Techtony96.Discord.utils.Logger;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Scanner;

import static Techtony96.Discord.modules.gametracker.GameTrackerModule.*;

/**
 * Created by Tony Pappas on 10/1/2016.
 */
public class FileIO {

	private static final String DELIMITER = "|";
	private static final char NEWLINE = '\n';

	protected static Path getFilePath(IUser user) {
		return Paths.get(FILE_LOCATION, user.getID() + FILE_TYPE);
	}

	protected static boolean addGameTime(IUser user, String game, long startTime, long endTime) {
		game = game.replace("|", "");
		try {
			File f = new File(getFilePath(user).toString());
			if (!f.exists())
				f.createNewFile();
			Files.write(getFilePath(user), (getGameIndex(game) + DELIMITER + startTime + DELIMITER + endTime + NEWLINE).getBytes(), StandardOpenOption.APPEND);
			return true;
		} catch (FileOperationException | IOException e) {
			Logger.warning("Failed to save data for " + user.getName());
			Logger.debug(e);
			return false;
		}
	}

	private static int getGameIndex(String game) throws FileOperationException {
		int lastIndex = -1;
		try {
			Scanner fileScanner = new Scanner(new File(GAME_INDEX_LOCATION));
			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine();
				String fileGame = line.substring(line.indexOf(DELIMITER) + 1);
				lastIndex = Integer.parseInt(line.substring(0, line.indexOf(DELIMITER)));
				if (fileGame.equalsIgnoreCase(game)) {
					return lastIndex;
				}
				Logger.debug(fileGame + " != " + game);
			}
		} catch (IOException e) {
			Logger.error(e.getMessage());
			Logger.debug(e);
			throw new FileOperationException();
		}
		return makeNewGameIndex(lastIndex + 1, game);
	}

	private static String getGame(int index) throws GameIndexDoesNotExistException, FileOperationException {
		try {
			Iterator<String> lines = Files.lines(Paths.get(GAME_INDEX_LOCATION)).iterator();
			while (lines.hasNext()) {
				String[] array = lines.next().split(DELIMITER);
				if (Integer.parseInt(array[0]) == index) {
					return array[1];
				}
			}
		} catch (IOException e) {
			Logger.error(e.getMessage());
			Logger.debug(e);
			throw new FileOperationException();
		}
		throw new GameIndexDoesNotExistException(index + " does not exist!");
	}

	private static int makeNewGameIndex(int index, String game) throws FileOperationException {
		try {
			Files.write(Paths.get(GAME_INDEX_LOCATION), (index + DELIMITER + game + NEWLINE).getBytes(), StandardOpenOption.APPEND);
			return index;
		} catch (IOException e) {
			Logger.error(e.getMessage());
			Logger.debug(e);
		}
		throw new FileOperationException();
	}
}
