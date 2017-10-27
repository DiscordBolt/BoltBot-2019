package net.ajpappas.discord.modules.reddit.posts;

import net.ajpappas.discord.modules.reddit.internal.RawRedditPost;
import net.ajpappas.discord.utils.EmbedUtil;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class SelfPost extends RedditPost {

    private String selfText;

    public SelfPost(RawRedditPost rawRedditPost) {
        super(rawRedditPost);
        this.selfText = rawRedditPost.selftext;
    }

    @Override
    public EmbedObject toEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.withColor(EMBED_COLOR);

        // Embed Title
        embed.withAuthorName(EmbedUtil.limitString(EmbedUtil.AUTHOR_NAME_MAX_LENGTH, getTitle()));
        embed.withAuthorUrl("https://www.reddit.com/r/" + getSubreddit() + "/comments/" + getId());

        // Embed Text
        embed.withDesc(EmbedUtil.limitString(EmbedUtil.DESCRIPTION_MAX_LENGTH, getSelfText()));

        // Post info
        embed.appendField(":pencil2: Author", EmbedUtil.limitString(EmbedUtil.FIELD_VALUE_MAX_LENGTH, getAuthor()), true);
        embed.appendField(":arrow_up: Upvotes", String.format("%,d", getScore()), true);
        if (getGilded() > 0)
            embed.appendField(":star: Gold", "x" + getGilded(), false);

        return embed.build();
    }

    public String getSelfText() {
        return selfText;
    }
}
