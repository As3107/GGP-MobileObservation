package com.ggp.ai_ggpmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private final String kit;
    private final String password;
    private final String level;

    public User(String kit, String password, String level) {
        this.kit = kit;
        this.password = password;
        this.level = level;
    }

    protected User(Parcel in) {
        kit = in.readString();
        password = in.readString();
        level = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getkit() {
        return kit;
    }

    public String getPassword() {
        return password;
    }

    public String getLevel() {
        return level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kit);
        dest.writeString(password);
        dest.writeString(level);
    }
}
