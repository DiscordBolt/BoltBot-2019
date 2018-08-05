package com.discordbolt.boltbot.web.models;

import com.discordbolt.boltbot.repository.entity.GuildData;
import java.util.List;

public class GuildModel {

    public long count;
    public List<GuildData> guilds;

    public GuildModel(List<GuildData> guilds) {
        this.count = guilds.size();
        this.guilds = guilds;
    }
}
