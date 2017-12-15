package com.discordbolt.boltbot.api.mysql.data;

public interface Savable {

    /**
     * Save the object in the database
     *
     * @return boolean success
     */
    boolean save();

    /**
     * delete the object from the database
     *
     * @return boolean success
     */
    boolean delete();

    /**
     * Get the object unique ID
     *
     * @return Long ID
     */
    long getId();
}
