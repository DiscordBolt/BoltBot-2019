package com.discordbolt.boltbot.modules.reddit.enums;

public enum SortMethod {

    HOT(""),
    NEW("/new"),
    RISING("/rising"),
    CONTROVERSAL("/controversial"),
    GILDED("/gilded"),
    TOP_HOUR("/top/?sort=top&t=hour"),
    TOP_DAY("/top/?sort=top&t=day"),
    TOP_WEEK("/top/?sort=top&t=week"),
    TOP_MONTH("/top/?sort=top&t=month"),
    TOP_YEAR("/top/?sort=top&t=year"),
    TOP_ALL("/top/?sort=top&t=all");

    private String urlTag;

    SortMethod(String urlTag) {
        this.urlTag = urlTag;
    }

    public String getUrlTag() {
        return urlTag;
    }

    /**
     * Override the default toString() method in order to get the URL to append
     *
     * @return String to append to URL
     */
    @Override
    public String toString() {
        return this.getUrlTag();
    }
}
