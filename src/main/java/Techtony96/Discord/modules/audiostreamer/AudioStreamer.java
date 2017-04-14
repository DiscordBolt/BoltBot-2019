package Techtony96.Discord.modules.audiostreamer;

import Techtony96.Discord.api.CustomModule;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;

/**
 * Created by Tony on 4/14/2017.
 */
public class AudioStreamer extends CustomModule implements IModule {

    private static IUser commander;

    public AudioStreamer() {
        super("Audio Streamer", "0.1");
    }
}
