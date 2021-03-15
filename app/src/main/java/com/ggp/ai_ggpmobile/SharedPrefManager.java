package com.ggp.ai_ggpmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.ggp.ai_ggpmobile.Model.Kasie;
import com.ggp.ai_ggpmobile.Model.Mandor;
import com.ggp.ai_ggpmobile.Model.TK;
import com.ggp.ai_ggpmobile.Model.User;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String USER_USERNAME = "username";
    private static final String USER_PASSWORD = "password";
    private static final String USER_LEVEL = "level";
    public static final String TK_KIT = "tk_kit";
    public static final String TK_NAMA = "tk_nama";
    public static final String TK_MANDOR = "tk_mandor";
    public static final String MANDOR_INDEX = "mandor_index";
    public static final String MANDOR_NAMA = "mandor_nama";
    public static final String KASIE_MANDOR = "kasie_mandor";
    public static final String KASIE_ID = "kasie_id";
    public static final String KASIE_NAMA = "kasie_nama";
    public static final String KASIE_INDEX = "kasie_index";

    @SuppressLint("StaticFieldLeak")
    private static SharedPrefManager mInstance;
    @SuppressLint("StaticFieldLeak")
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean login() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_LEVEL, null) != null;
    }

    public void logout(){
        mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public void setUserLogin(User user){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_USERNAME, user.getkit());
        editor.putString(USER_PASSWORD, user.getPassword());
        editor.putString(USER_LEVEL, user.getLevel());
        editor.apply();
    }

    public User getUserLogin(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(USER_USERNAME, null),
                sharedPreferences.getString(USER_PASSWORD, null),
                sharedPreferences.getString(USER_LEVEL, null)
        );
    }

    public void setDataTK(TK tk){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TK_KIT, tk.getKit());
        editor.putString(TK_NAMA, tk.getNama());
        editor.putString(TK_MANDOR, tk.getMandor());
        editor.apply();
    }

    public TK getDataTK(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new TK(
                sharedPreferences.getString(TK_KIT, null),
                sharedPreferences.getString(TK_NAMA, null),
                sharedPreferences.getString(TK_MANDOR, null)
        );
    }

    public void setDataMandor(Mandor mandor){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MANDOR_INDEX, mandor.getMandorIndex());
        editor.putString(MANDOR_NAMA, mandor.getNama());
        editor.putString(KASIE_MANDOR, mandor.getKasieIndex());
        editor.apply();
    }

    public Mandor getDataMandor(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Mandor(
                sharedPreferences.getString(MANDOR_INDEX, null),
                sharedPreferences.getString(MANDOR_NAMA, null),
                sharedPreferences.getString(KASIE_MANDOR, null)
        );
    }

    public void setDataKasie(Kasie kasie){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KASIE_ID, kasie.getId());
        editor.putString(KASIE_NAMA, kasie.getNama());
        editor.putString(KASIE_INDEX, kasie.getKasieIndex());
        editor.apply();
    }

    public Kasie getDataKasie(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Kasie(
                sharedPreferences.getString(KASIE_ID, null),
                sharedPreferences.getString(KASIE_NAMA, null),
                sharedPreferences.getString(KASIE_INDEX, null)
        );
    }

}
