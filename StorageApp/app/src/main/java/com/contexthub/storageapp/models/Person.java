package com.contexthub.storageapp.models;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A {@link java.io.Serializable} object saved in the ContextHub vault. Uses the Parceler library
 * (https://github.com/johncarl81/parceler) to auto-generate the Parcelable implementation needed to
 * most efficiently pass the object between fragments. Yes, we could pass it as a serializable
 * extra, but that's just lazy.
 */
@Parcel
public class Person implements Serializable {

    String name;
    String title;
    ArrayList<String> nicknames = new ArrayList<String>();
    int heightInInches;
    int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getNicknames() {
        if(nicknames == null) nicknames = new ArrayList<String>();
        return nicknames;
    }

    public void setNicknames(ArrayList<String> nicknames) {
        this.nicknames = nicknames;
    }

    public int getHeightInInches() {
        return heightInInches;
    }

    public void setHeightInInches(int heightInInches) {
        this.heightInInches = heightInInches;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("name: %s\ntitle: %s\nnicknames: %s\nheightInInches: %s\nage: %s",
                name, title, nicknames, heightInInches, age);
    }
}
