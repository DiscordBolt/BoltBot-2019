package net.ajpappas.discord.modules.reddit.posts;

import net.ajpappas.discord.modules.reddit.internal.RawRedditPost;
import net.ajpappas.discord.utils.EmbedUtil;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class ImagePost extends RedditPost {

    public ImagePost(RawRedditPost rawRedditPost) {
        super(rawRedditPost);
    }

    @Override
    public EmbedObject toEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(EMBED_COLOR);
        embed.withAuthorName(getTitle());
        embed.withAuthorUrl("https://www.reddit.com/r/" + getSubreddit() + "/comments/" + getId());
        embed.withDesc("Posted by: " + getAuthor());
        embed.withImage(getUrl());
        embed.appendField(":arrow_up: " + getScore(), EmbedUtil.ZERO_WIDTH_SPACE, true);
        embed.appendField(":star: x" + getGilded(), EmbedUtil.ZERO_WIDTH_SPACE, true);
        return embed.build();
    }
}
