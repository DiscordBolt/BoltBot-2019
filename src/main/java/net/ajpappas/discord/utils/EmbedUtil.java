package net.ajpappas.discord.utils;

public class EmbedUtil {

    public static final String ZERO_WIDTH_SPACE = "\u200B";
    public static final int TITLE_MAX_LENGTH = 256;
    public static final int DESCRIPTION_MAX_LENGTH = 2048;
    public static final int FIELD_NAME_MAX_LENGTH = 256;
    public static final int FIELD_VALUE_MAX_LENGTH = 1024;
    public static final int FOOTER_TEXT_MAX_LENGTH = 2048;
    public static final int AUTHOR_NAME_MAX_LENGTH = 256;

    public static String limitString(int charCount, String string) {
        return string.length() <= charCount ? string : string.substring(0, charCount - 1);
    }
}
