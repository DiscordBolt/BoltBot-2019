package com.discordbolt.boltbot.repository;

import com.discordbolt.boltbot.repository.entity.GuildData;
import discord4j.core.object.util.Snowflake;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {GuildRepository.class})
@EnableMongoRepositories()
@Import(EmbeddedMongoAutoConfiguration.class) // In example was EmbeddedMongoConfiguration.class
public class GuildRepositoryTest {

    @Autowired
    private GuildRepository guildRepository;

    @Before
    public void setUp() {
        // Make sure the db is empty
        guildRepository.deleteAll().subscribe();

        // Make 10 default test guilds
        for (int i = 1; i <= 10; i++)
            guildRepository.save(new GuildData(i, "Test Guild " + i)).subscribe();
    }

    @Test
    public void testGetById_Snowflake() {
        Snowflake guild1 = Snowflake.of(1L);

        assertThat(guildRepository.findById(guild1).map(GuildData::getId).block(), equalTo(1L));
    }
}
