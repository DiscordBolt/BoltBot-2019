package com.discordbolt.boltbot.persistent;

public abstract class PersistentObject {

    public abstract void save();

    public abstract void delete();

    public abstract long getId();
}
