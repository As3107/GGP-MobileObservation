package com.ggp.ai_ggpmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Notes implements Parcelable {

    private String id;
    private String indeks;
    private String no_spk;
    private String catatan;

    public Notes(String id, String indeks, String no_spk, String catatan) {
        this.id = id;
        this.indeks = indeks;
        this.no_spk = no_spk;
        this.catatan = catatan;
    }

    protected Notes(Parcel in) {
        id = in.readString();
        indeks = in.readString();
        no_spk = in.readString();
        catatan = in.readString();
    }

    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getIndeks() {
        return indeks;
    }

    public String getNo_spk() {
        return no_spk;
    }

    public String getCatatan() {
        return catatan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(indeks);
        dest.writeString(no_spk);
        dest.writeString(catatan);
    }
}
