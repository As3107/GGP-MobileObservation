package com.ggp.ai_ggpmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Kasie implements Parcelable {

    private String id;
    private String KasieIndex;
    private String nama;

    public Kasie(String id, String kasieIndex, String nama) {
        this.id = id;
        KasieIndex = kasieIndex;
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public String getKasieIndex() {
        return KasieIndex;
    }

    public String getNama() {
        return nama;
    }

    protected Kasie(Parcel in) {
        id = in.readString();
        KasieIndex = in.readString();
        nama = in.readString();
    }

    public static final Creator<Kasie> CREATOR = new Creator<Kasie>() {
        @Override
        public Kasie createFromParcel(Parcel in) {
            return new Kasie(in);
        }

        @Override
        public Kasie[] newArray(int size) {
            return new Kasie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(KasieIndex);
        dest.writeString(nama);
    }
}
