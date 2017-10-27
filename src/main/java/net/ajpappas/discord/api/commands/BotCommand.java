package net.ajpappas.discord.api.commands;

import sx.blah.discord.handle.obj.Permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Tony on 4/14/2017.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotCommand {
    String[] command();

    String description();

    String usage();

    String module();

    String[] aliases() default {};

    String[] allowedChannels() default {};

    Permissions[] permissions() default {};

    int args() default -1;

    int minArgs() default -1;

    int maxArgs() default -1;

    boolean secret() default false;

    boolean allowPM() default false;

    boolean deleteMessages() default false;
}