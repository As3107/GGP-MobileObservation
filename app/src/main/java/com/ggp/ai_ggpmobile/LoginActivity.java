package com.ggp.ai_ggpmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ggp.ai_ggpmobile.Model.Kasie;
import com.ggp.ai_ggpmobile.Model.Mandor;
import com.ggp.ai_ggpmobile.Model.TK;
import com.ggp.ai_ggpmobile.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etUsername, etPassword;
    MaterialButton btLogin;
    TextView tvPassword;

    private String kit, password, newToken;
    private User user;
    private Mandor mandor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).hide();
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvPassword = findViewById(R.id.tv_forgot_password);
        btLogin = findViewById(R.id.bt_login);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> newToken = instanceIdResult.getToken());

        btLogin.setOnClickListener(v -> getUser());

    }

    private void getUser() {

        ProgressDialog progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        kit = Objects.requireNonNull(etUsername.getText()).toString().trim();
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.LOGIN_URL,
                response -> {
                progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))) {
                            JSONObject objUser = jsonObject.getJSONObject("data");

                            user = new User(objUser.getString("kit"),
                                    objUser.getString("password"),
                                    objUser.getString("level"));

                            SharedPrefManager.getInstance(LoginActivity.this).setUserLogin(user);

                            if (SharedPrefManager.getInstance(this).login()) {
                                switch (user.getLevel()) {
                                    case "tk": {
                                        getProfileTK();
                                        break;
                                    }
                                    case "mandor": {
                                        getProfileMandor();
                                        break;
                                    }
                                    case "kasie": {
                                        getProfileKasie();
                                        break;
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(this, "Username atau Password Salah", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kit", kit);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getProfileTK(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_PROFILE_TK_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))) {
                            JSONObject data = jsonObject.getJSONObject("data");

                            TK tk = new TK(
                                    data.getString("kit"),
                                    data.getString("nama"),
                                    data.getString("mandor")
                            );
                            SharedPrefManager.getInstance(LoginActivity.this).setDataTK(tk);
                            SharedPrefManager.getInstance(LoginActivity.this).login();
                            Intent intent = new Intent(LoginActivity.this, MainTkActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("kit", kit);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getProfileMandor(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_PROFILE_MANDOR_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))) {
                            JSONObject data = jsonObject.getJSONObject("data");

                            mandor = new Mandor(
                                    data.getString("index"),
                                    data.getString("nama"),
                                    data.getString("kasie")
                            );
                            updateTokenMandor();
                            /*SharedPrefManager.getInstance(LoginActivity.this).setDataMandor(mandor);
                            SharedPrefManager.getInstance(LoginActivity.this).login();
                            Intent intent = new Intent(LoginActivity.this, HomeMandorActivity.class);
                            startActivity(intent);
                            finish();*/
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("index", kit);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getProfileKasie(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_PROFILE_KASIE_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))) {
                            JSONObject data = jsonObject.getJSONObject("data");

                            Kasie kasie = new Kasie(
                                    data.getString("id"),
                                    data.getString("nama"),
                                    data.getString("index")
                            );
                            SharedPrefManager.getInstance(LoginActivity.this).setDataKasie(kasie);
                            SharedPrefManager.getInstance(LoginActivity.this).login();
                            Intent intent = new Intent(LoginActivity.this, HomeKasieActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("index", kit);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }

    private void updateTokenMandor(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.UPDATE_TOKEN_MANDOR,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.getString("message").equalsIgnoreCase(String.valueOf(true))){
                            SharedPrefManager.getInstance(LoginActivity.this).setDataMandor(mandor);
                            SharedPrefManager.getInstance(LoginActivity.this).login();
                            Intent intent = new Intent(LoginActivity.this, HomeMandorActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                error -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("MandorIndex", mandor.getMandorIndex());
                params.put("token", newToken);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }

}