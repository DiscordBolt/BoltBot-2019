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
        embed.withAuthorName(getTitle());
        embed.withAuthorUrl("https://www.reddit.com/r/" + getSubreddit() + "/comments/" + getId());
        embed.withDesc("Posted by: " + getAuthor());
        embed.withDesc(getSelfText().substring(0, getSelfText().length() > 2048 ? 2047 : getSelfText().length() - 1));
        embed.appendField(":arrow_up: " + getScore(), EmbedUtil.ZERO_WIDTH_SPACE, true);
        embed.appendField(":star: x" + getGilded(), EmbedUtil.ZERO_WIDTH_SPACE, true);
        ;
        return embed.build();
    }

    public String getSelfText() {
        return selfText;
    }
}
