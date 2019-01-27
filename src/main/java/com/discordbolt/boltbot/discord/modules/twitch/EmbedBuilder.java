package com.discordbolt.boltbot.discord.modules.twitch;

import com.discordbolt.boltbot.discord.modules.twitch.responses.ChannelData;
import com.discordbolt.boltbot.discord.modules.twitch.responses.UserData;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;

import java.awt.*;
import java.util.function.Consumer;

public class EmbedBuilder {

    private static final Color EMBED_COLOR = new Color(75, 54, 124);

    static Consumer<EmbedCreateSpec> createEmbed(ChannelData channelData, UserData userData, Member guildMember) {
        return spec -> {
            spec.setAuthor(userData.getDisplayName() + " is now live!", "https://twitch.tv/" + channelData.getUsername(), userData.getProfileImageUrl());
            spec.setTitle(channelData.getTitle());
            spec.setDescription(String.format("Link: [twitch.tv/%s](https://twitch.tv/%s)", channelData.getUsername(), channelData.getUsername()));
            spec.addField("Live Viewers", Integer.toString(channelData.getViewerCount()), true);
            if (guildMember != null)
                spec.addField("Streamer", guildMember.getNicknameMention(), true);
            spec.setImage(channelData.getThumbnailUrl().replace("{width}", "1280").replace("{height}", "720"));
            spec.setColor(EMBED_COLOR);
        };
    }
}
