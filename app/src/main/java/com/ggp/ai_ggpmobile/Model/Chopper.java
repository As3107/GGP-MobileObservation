package com.ggp.ai_ggpmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Chopper implements Parcelable {

    private final int id;
    private final String no_spk;
    private final String tanggal;
    private final String kit;
    private final String pengamat;
    private final String lokasi;
    private final String plot;
    private final String bt;
    private final String th;
    private final String ar;
    private final String total;

    public Chopper(int id, String no_spk, String tanggal, String kit, String pengamat, String lokasi, String plot, String bt, String th, String ar, String total) {
        this.id = id;
        this.no_spk = no_spk;
        this.tanggal = tanggal;
        this.kit = kit;
        this.pengamat = pengamat;
        this.lokasi = lokasi;
        this.plot = plot;
        this.bt = bt;
        this.th = th;
        this.ar = ar;
        this.total = total;
    }

    protected Chopper(Parcel in) {
        id = in.readInt();
        no_spk = in.readString();
        tanggal = in.readString();
        kit = in.readString();
        pengamat = in.readString();
        lokasi = in.readString();
        plot = in.readString();
        bt = in.readString();
        th = in.readString();
        ar = in.readString();
        total = in.readString();
    }

    public static final Creator<Chopper> CREATOR = new Creator<Chopper>() {
        @Override
        public Chopper createFromParcel(Parcel in) {
            return new Chopper(in);
        }

        @Override
        public Chopper[] newArray(int size) {
            return new Chopper[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getNo_spk() {
        return no_spk;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getKit() {
        return kit;
    }

    public String getPengamat() {
        return pengamat;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getPlot() {
        return plot;
    }

    public String getBt() {
        return bt;
    }

    public String getTh() {
        return th;
    }

    public String getAr() {
        return ar;
    }

    public String getTotal() {
        return total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(no_spk);
        dest.writeString(tanggal);
        dest.writeString(kit);
        dest.writeString(pengamat);
        dest.writeString(lokasi);
        dest.writeString(plot);
        dest.writeString(bt);
        dest.writeString(th);
        dest.writeString(ar);
    }
}
