package net.ajpappas.discord.modules.reddit;

import net.ajpappas.discord.utils.Logger;

public enum PostType {

    IMAGE("image"),
    LINK("link"),
    rich_video("rich:video"),
    SELF("self");

    private String value;

    PostType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PostType getEnum(String value) {
        for (PostType pt : PostType.values())
            if (pt.getValue().equalsIgnoreCase(value))
                return pt;
        Logger.warning("No Post Type found for \"" + value + "\"");
        return null;
    }
}
