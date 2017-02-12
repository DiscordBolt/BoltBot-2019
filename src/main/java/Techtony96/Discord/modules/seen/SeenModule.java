package Techtony96.Discord.modules.seen;

import Techtony96.Discord.api.CustomModule;
import Techtony96.Discord.utils.ChannelUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.PresenceUpdateEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;
import sx.blah.discord.modules.IModule;


/**
 * Created by Tony on 12/25/2016.
 */
public class SeenModule extends CustomModule implements IModule {


    public SeenModule() {
        super("Seen Command", "1.0");
    }

    @EventSubscriber
    public void onUserPresenceChange(PresenceUpdateEvent e){
        if (e.getNewPresence() != Presences.OFFLINE)
            return;

       // UserData data = new UserData(e.getUser());
       // ofy().save().entity(data).now();
    }

    @EventSubscriber
    public void OnMesageEvent(MessageReceivedEvent e) {
        IMessage message = e.getMessage();
        IChannel channel = message.getChannel();
        String messageString = message.getContent();
        String[] args = messageString.split(" ");
        String command = args[0].toLowerCase();

        if (!command.equals("!seen")) {
            return;
        }
        if (message.getMentions().size() != 1){
            ChannelUtil.sendMessage(channel, e.getMessage().getAuthor().mention() + ", you may only @mention one user.");
            return;
        }

        IUser query = message.getMentions().get(0);

        if (query.getPresence() != Presences.OFFLINE){
            ChannelUtil.sendMessage(channel, e.getMessage().getAuthor().mention() + ", " + query.getDisplayName(message.getGuild()) + " is currently " + query.getPresence().name());
            return;
        }

        // UserData fetched = ofy().load().type(UserData.class).id(query.getID()).now();

       //  ChannelUtil.sendMessage(channel, e.getMessage().getAuthor().mention() + ", " + query.getDisplayName(message.getGuild()) + " was last seen " + new PrettyTime().format(fetched.lastOnline) + "." );
    }







}
