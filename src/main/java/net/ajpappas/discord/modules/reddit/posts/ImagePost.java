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
        // Embed Title
        embed.withAuthorName(EmbedUtil.limitString(EmbedUtil.AUTHOR_NAME_MAX_LENGTH, getTitle()));
        embed.withAuthorUrl("https://www.reddit.com/r/" + getSubreddit() + "/comments/" + getId());

        // Image
        embed.withImage(getUrl());

        // Post info
        embed.appendField(":pencil2: Author", EmbedUtil.limitString(EmbedUtil.FIELD_VALUE_MAX_LENGTH, getAuthor()), true);
        embed.appendField(":arrow_up: Upvotes", String.format("%,d", getScore()), true);
        if (getGilded() > 0)
            embed.appendField(":star: Gold", "x" + getGilded(), false);

        return embed.build();
    }
}
