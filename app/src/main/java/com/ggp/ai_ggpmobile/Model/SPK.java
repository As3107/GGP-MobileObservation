package com.ggp.ai_ggpmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class SPK implements Parcelable {

    private String id;
    private String no_spk;
    private String tanggal;
    private String lokasi;
    private String wilayah;
    private String pengamatan;
    private String tk;
    private String mandor;
    private String kasie;
    private String status;

    public SPK(String id, String no_spk, String tanggal, String lokasi, String wilayah, String pengamatan, String tk, String mandor, String kasie, String status) {
        this.id = id;
        this.no_spk = no_spk;
        this.tanggal = tanggal;
        this.lokasi = lokasi;
        this.wilayah = wilayah;
        this.pengamatan = pengamatan;
        this.tk = tk;
        this.mandor = mandor;
        this.kasie = kasie;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getNo_spk() {
        return no_spk;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getWilayah() {
        return wilayah;
    }

    public String getPengamatan() {
        return pengamatan;
    }

    public String getTk() {
        return tk;
    }

    public String getMandor() {
        return mandor;
    }

    public String getKasie() {
        return kasie;
    }

    public String getStatus() {
        return status;
    }

    protected SPK(Parcel in) {
        id = in.readString();
        no_spk = in.readString();
        tanggal = in.readString();
        lokasi = in.readString();
        wilayah = in.readString();
        pengamatan = in.readString();
        tk = in.readString();
        mandor = in.readString();
        kasie = in.readString();
        status = in.readString();
    }

    public static final Creator<SPK> CREATOR = new Creator<SPK>() {
        @Override
        public SPK createFromParcel(Parcel in) {
            return new SPK(in);
        }

        @Override
        public SPK[] newArray(int size) {
            return new SPK[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(no_spk);
        dest.writeString(tanggal);
        dest.writeString(lokasi);
        dest.writeString(wilayah);
        dest.writeString(pengamatan);
        dest.writeString(tk);
        dest.writeString(mandor);
        dest.writeString(kasie);
        dest.writeString(status);
    }
}
