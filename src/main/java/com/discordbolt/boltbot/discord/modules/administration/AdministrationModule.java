package com.discordbolt.boltbot.discord.modules.administration;

import com.discordbolt.boltbot.discord.api.BotModule;
import com.discordbolt.boltbot.discord.api.CommandBean;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import discord4j.core.DiscordClient;

public class AdministrationModule implements BotModule {

    @Override
    public void initialize(DiscordClient client) {
        BeanUtil.getBean(CommandBean.class).registerCommand(new DisconnectCommand(client));
        BeanUtil.getBean(CommandBean.class).registerCommand(new ListRolesCommand());
        BeanUtil.getBean(CommandBean.class).registerCommand(new CommandPrefixCommand());
        BeanUtil.getBean(CommandBean.class).registerCommand(new MoveCommand());
    }
}
