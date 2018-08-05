package com.discordbolt.boltbot.web.models;

import com.discordbolt.boltbot.repository.entity.GuildData;
import java.util.List;

public class GuildModel {

    private long count;
    private List<GuildData> guilds;

    public GuildModel(List<GuildData> guilds) {
        this.count = guilds.size();
        this.guilds = guilds;
    }

    public long getCount() {
        return count;
    }

    public List<GuildData> getGuilds() {
        return guilds;
    }
}
