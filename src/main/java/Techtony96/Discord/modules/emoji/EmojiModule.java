package Techtony96.Discord.modules.emoji;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.api.commands.BotCommand;
import Techtony96.Discord.api.commands.CommandContext;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.Image;

import java.io.File;

/**
 * Created by Tony on 4/5/2017.
 */
public class EmojiModule extends CustomModule implements IModule {

    public EmojiModule() {
        super("Emoji", "1.0");
    }

    @EventSubscriber
    public void onReady(ReadyEvent e) {
        new BotCommand(client, "emoji") {

            @Override
            public void execute(CommandContext cc) {
                cc.replyWith("Starting processing of adding an emoji. Using \"" + cc.getArgument(1) + "\n as the name.");

                cc.getGuild().addEmoji(cc.getArgument(1), Image.forFile(new File("picture.png")));
            }
        }.setPermissions(Permissions.MANAGE_EMOJIS).setArguments(2).setUsage("!Emoji [Name]");
    }
}
