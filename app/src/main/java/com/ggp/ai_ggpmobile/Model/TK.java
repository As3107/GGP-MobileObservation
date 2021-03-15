package com.ggp.ai_ggpmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class TK implements Parcelable {
    private String kit;
    private String nama;
    private String mandor;

    public TK(String kit, String nama, String mandor) {
        this.kit = kit;
        this.nama = nama;
        this.mandor = mandor;
    }

    protected TK(Parcel in) {
        kit = in.readString();
        nama = in.readString();
        mandor = in.readString();
    }

    public static final Creator<TK> CREATOR = new Creator<TK>() {
        @Override
        public TK createFromParcel(Parcel in) {
            return new TK(in);
        }

        @Override
        public TK[] newArray(int size) {
            return new TK[size];
        }
    };

    public String getKit() {
        return kit;
    }

    public String getNama() {
        return nama;
    }

    public String getMandor() {
        return mandor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kit);
        dest.writeString(nama);
        dest.writeString(mandor);
    }
}
