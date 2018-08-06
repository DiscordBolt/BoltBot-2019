package com.discordbolt.boltbot.web.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import com.discordbolt.boltbot.repository.UserRepository;
import com.discordbolt.boltbot.repository.entity.UserData;
import com.discordbolt.boltbot.web.models.UserModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIT {

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    private UserData user1 = new UserData(12345L, "User One", "0000");
    private UserData user2 = new UserData(99999L, "User Two", "1234");
    private UserData user3 = new UserData(84586376L, "User Three", "7777");
    private final long INVALID_ID = 100L;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/users");
        userRepository.saveAll(Arrays.asList(user1, user2, user3));

        objectMapper = new ObjectMapper();
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
        UserModel userModel = objectMapper.readValue(response.getBody(), new TypeReference<UserModel>() {
        });
        assertThat(Arrays.asList(user1, user2, user3), equalTo(userModel.getUsers()));
    }

    @Test
    public void testGetUser1() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "/" + user1.getId(), String.class);
        UserData responseUser = objectMapper.readValue(response.getBody(), UserData.class);
        assertThat(user1, equalTo(responseUser));
        assertThat(user2, not(equalTo(responseUser)));
        assertThat(user3, not(equalTo(responseUser)));
    }

    @Test
    public void testGetUser2() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "/" + user2.getId(), String.class);
        UserData responseUser = objectMapper.readValue(response.getBody(), UserData.class);
        assertThat(user2, equalTo(responseUser));
        assertThat(user1, not(equalTo(responseUser)));
        assertThat(user3, not(equalTo(responseUser)));
    }

    @Test
    public void testGetUser3() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "/" + user3.getId(), String.class);
        UserData responseUser = objectMapper.readValue(response.getBody(), UserData.class);
        assertThat(user3, equalTo(responseUser));
        assertThat(user1, not(equalTo(responseUser)));
        assertThat(user2, not(equalTo(responseUser)));
    }

    @Test
    public void testGetInvalidUser() {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "/" + INVALID_ID, String.class);
        assertThat(HttpStatus.NOT_FOUND, equalTo(response.getStatusCode()));
    }
}
