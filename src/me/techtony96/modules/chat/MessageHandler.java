package me.techtony96.modules.chat;

import sx.blah.discord.api.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;

public class MessageHandler {
	
	@EventSubscriber
	 public void OnMesageEvent(MessageReceivedEvent e) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		IMessage message = e.getMessage();
		if(message.getContent().startsWith("!modulemessage")){
			sendMessage("Message send! Module is working.", e);
		}
	}
	
	public void sendMessage(String message, MessageReceivedEvent event) throws HTTP429Exception, DiscordException, MissingPermissionsException{
		new MessageBuilder(ChatModule.client).appendContent(message).withChannel(event.getMessage().getChannel()).build();
	}


}
