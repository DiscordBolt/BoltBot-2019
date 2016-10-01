package Techtony96.Discord.modules.gametracker;

import Techtony96.Discord.utils.Logger;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.io.IOException;

import static Techtony96.Discord.modules.gametracker.GameTrackerModule.FILE_LOCATION;
import static Techtony96.Discord.modules.gametracker.GameTrackerModule.FILE_TYPE;

/**
 * Created by Techt on 10/1/2016.
 */
public class FileIO {

	protected static File getFile(IUser user) {
		File f = new File(FILE_LOCATION + user.getID() + FILE_TYPE);
		if (f.exists())
			return f;
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
		return f;
	}
}
