package com.discordbolt.boltbot.modules.reddit.enums;

import com.discordbolt.boltbot.utils.Logger;

public enum PostType {

    IMAGE("image"),
    GIFV(null),
    LINK("link"),
    VIDEO("rich:video"),
    HOSTED_VIDEO("hosted:video"),
    SELF("self"),
    UNKNOWN(null);

    private String value;

    PostType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PostType getEnum(String value) {
        if (value == null || value.length() == 0)
            return null;
        for (PostType pt : PostType.values())
            if (pt.getValue() != null && pt.getValue().equalsIgnoreCase(value))
                return pt;
        Logger.warning("No Post Type found for post_hint: \"" + value + "\"");
        return null;
    }

    public static PostType convertDomain(String domain) {
        if (domain == null || domain.length() == 0)
            return null;
        if (domain.startsWith("self."))
            return SELF;
        if (domain.equalsIgnoreCase("i.redd.it")) {
            return IMAGE;
        }

        return null;
    }

    public static PostType convertURL(String url) {
        url = url.toLowerCase();
        if (url.contains("imgur.com")) {
            if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".gif")) {
                return IMAGE;
            } else if (url.endsWith(".gifv")) {
                return GIFV;
            }
        } else if (url.contains("youtube.com") || url.contains("youtu.be")) {
            return VIDEO;
        }
        return null;
    }
}
