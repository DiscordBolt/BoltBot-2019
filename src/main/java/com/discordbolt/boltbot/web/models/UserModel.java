package com.discordbolt.boltbot.web.models;

import com.discordbolt.boltbot.repository.entity.UserData;
import java.util.List;

public class UserModel {

    private long count;
    private List<UserData> users;

    public UserModel(List<UserData> users) {
        this.count = users.size();
        this.users = users;
    }

    public UserModel() {
    }

    public long getCount() {
        return count;
    }

    public List<UserData> getUsers() {
        return users;
    }
}
