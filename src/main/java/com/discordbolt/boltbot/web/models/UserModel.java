package com.discordbolt.boltbot.web.models;

import com.discordbolt.boltbot.repository.entity.UserData;
import java.util.List;

public class UserModel {

    public long count;
    public List<UserData> users;

    public UserModel(List<UserData> users) {
        this.count = users.size();
        this.users = users;
    }
}
