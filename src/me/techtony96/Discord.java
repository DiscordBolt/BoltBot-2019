package me.techtony96;

import me.techtony96.modules.tempchannels.TempChannelModule;
import me.techtony96.modules.userstatus.UserStatusModule;
import me.techtony96.utils.Config;
import me.techtony96.utils.Logger;
import sx.blah.discord.modules.ModuleLoader;
import sx.blah.discord.util.DiscordException;

public class Discord {

	public static void main(String[] args) {
		Logger.info("Discord.java starting up!");
		
		loadModules();

		Instance bot = null;
		bot = new Instance(Config.get("token"));
		try {
			bot.login();
		} catch (DiscordException e) {
			Logger.error("Bot could not start");
			Logger.debug(e);
		}
		

	}

	private static void loadModules() {
		ModuleLoader.addModuleClass(TempChannelModule.class);
		ModuleLoader.addModuleClass(UserStatusModule.class);
	}

}
