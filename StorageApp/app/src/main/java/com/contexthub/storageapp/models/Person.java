package com.contexthub.storageapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A {@link java.io.Serializable} object saved in the ContextHub vault.
 */
public class Person implements Serializable, Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.title);
        dest.writeSerializable(this.nicknames);
        dest.writeInt(this.heightInInches);
        dest.writeInt(this.age);
    }

    public Person() {
    }

    private Person(Parcel in) {
        this.name = in.readString();
        this.title = in.readString();
        this.nicknames = (ArrayList<String>) in.readSerializable();
        this.heightInInches = in.readInt();
        this.age = in.readInt();
    }

    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
