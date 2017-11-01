package net.ajpappas.discord.api.commands.list;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tony on 2/17/2017.
 */
public class ArgList extends ArrayList<String> {

    public ArgList(String[] args) {
        addAll(Arrays.asList(args));
    }

    public boolean containsIgnoreCase(String s) {
        return this.stream().anyMatch(s1 -> s1.equalsIgnoreCase(s));
    }
}
