package com.discordbolt.boltbot.modules.reddit;

import com.discordbolt.boltbot.modules.reddit.enums.PostType;
import com.discordbolt.boltbot.modules.reddit.enums.SortMethod;
import com.discordbolt.boltbot.utils.ChannelUtil;
import com.discordbolt.boltbot.utils.Logger;
import org.apache.http.client.HttpResponseException;
import sx.blah.discord.handle.obj.IChannel;

import java.io.IOException;

public class ScheduledSubreddit implements Runnable {

    private String subreddit;
    private IChannel channel;

    public ScheduledSubreddit(String subreddit, IChannel channel) {
        this.subreddit = subreddit;
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            ChannelUtil.sendMessage(channel, Subreddit.getPosts(subreddit, SortMethod.TOP_DAY).filter(false, PostType.IMAGE, PostType.SELF).getTopPost().toEmbed());
        } catch (IndexOutOfBoundsException e) {
            ChannelUtil.sendMessage(channel, "No posts were found for " + subreddit + ".");
            return;
        } catch (HttpResponseException e) {
            ChannelUtil.sendMessage(channel, subreddit + " subreddit doesn't exist!");
            return;
        } catch (IOException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
            ChannelUtil.sendMessage(channel, "An error occurred while trying to post " + subreddit + "'s post.");
            return;
        }
    }
}
