package com.ggp.ai_ggpmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Mandor implements Parcelable {

    private String MandorIndex;
    private String nama;
    private String KasieIndex;

    public Mandor(String mandorIndex, String nama, String kasieIndex) {
        MandorIndex = mandorIndex;
        this.nama = nama;
        KasieIndex = kasieIndex;
    }

    public String getMandorIndex() {
        return MandorIndex;
    }

    public String getNama() {
        return nama;
    }

    public String getKasieIndex() {
        return KasieIndex;
    }

    protected Mandor(Parcel in) {
        MandorIndex = in.readString();
        nama = in.readString();
        KasieIndex = in.readString();
    }

    public static final Creator<Mandor> CREATOR = new Creator<Mandor>() {
        @Override
        public Mandor createFromParcel(Parcel in) {
            return new Mandor(in);
        }

        @Override
        public Mandor[] newArray(int size) {
            return new Mandor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(MandorIndex);
        dest.writeString(nama);
        dest.writeString(KasieIndex);
    }
}
