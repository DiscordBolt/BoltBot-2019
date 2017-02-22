package Techtony96.Discord.api.list;

import java.util.ArrayList;

/**
 * Created by Tony on 2/17/2017.
 */
public class ArgList extends ArrayList<String> {

    public ArgList(String[] args){
        for (String s : args)
            add(s);
    }

    public boolean containsIgnoreCase(String s) {
        for (String s1 : this)
            if (s.equalsIgnoreCase(s1))
                return true;
        return false;
    }
}
