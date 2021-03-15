package com.ggp.ai_ggpmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ggp.ai_ggpmobile.Adapter.SPKAdapter;
import com.ggp.ai_ggpmobile.Model.SPK;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeKasieActivity extends AppCompatActivity {

    TextView tvName, tvDataNull, tvTotalAgree, tvTotalDisagree;
    RecyclerView rvListSPK;
    ImageView ivLogout;

    String nama_kasie, index_mandor, date, index_kasie, agree, disagree;
    ProgressDialog progressDialog;
    private final List<SPK> list = new ArrayList<>();
    private SPK spk;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_kasie);

        Objects.requireNonNull(getSupportActionBar()).hide();

        ivLogout = findViewById(R.id.iv_logout);
        tvName = findViewById(R.id.tv_name);
        tvDataNull = findViewById(R.id.tv_data_null);
        tvTotalAgree = findViewById(R.id.tv_total_agree);
        tvTotalDisagree = findViewById(R.id.tv_total_disagree);
        rvListSPK = findViewById(R.id.rv_list_data);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        nama_kasie = SharedPrefManager.getInstance(this).getDataKasie().getNama();
        index_kasie = SharedPrefManager.getInstance(this).getDataKasie().getKasieIndex();
        tvName.setText(nama_kasie);

        swipeRefreshLayout.setProgressViewEndTarget(false,0);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            rvListSPK.setVisibility(View.GONE);
            list.clear();
            getResultSPK();
            getAgreeSPK();
            getDisgreeSPK();
            new Handler().postDelayed(() ->
                    swipeRefreshLayout.setRefreshing(false), 2000);
        });

        ivLogout.setOnClickListener(v -> {
            SharedPrefManager.getInstance(this).logout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void getResultSPK(){
        progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_RESULT_SPK,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.length()>0){
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject data =jsonArray.getJSONObject(i);

                                spk = new SPK(
                                        data.getString("id"),
                                        data.getString("no_spk"),
                                        data.getString("tanggal"),
                                        data.getString("lokasi"),
                                        data.getString("wilayah"),
                                        data.getString("pengamatan"),
                                        data.getString("tk"),
                                        data.getString("mandor"),
                                        data.getString("kasie"),
                                        data.getString("status")
                                );
                                list.add(spk);
                            }
                            tvDataNull.setVisibility(View.GONE);
                            rvListSPK.setVisibility(View.VISIBLE);
                            rvListSPK.setLayoutManager(new LinearLayoutManager(HomeKasieActivity.this));
                            SPKAdapter adapter = new SPKAdapter(HomeKasieActivity.this, list);
                            rvListSPK.setAdapter(adapter);
                            adapter.onItemClickCallback(data -> {
                                Intent intent = new Intent(HomeKasieActivity.this, DetailResultSPKActivity.class);
                                intent.putExtra(DetailResultSPKActivity.DATA_SPK, data);
                                intent.putExtra(DetailResultSPKActivity.DATA_INTENT, "kasie");
                                intent.putExtra(DetailResultSPKActivity.DATA_NOTES, "mandor");
                                startActivity(intent);
                            });

                        } else {
                            tvDataNull.setVisibility(View.VISIBLE);
                            rvListSPK.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("status", index_kasie);
                params.put("tanggal", date);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeKasieActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getAgreeSPK(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_AGREE_SPK_KASIE,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.isNull(0)){
                            agree = "0";
                        } else {
                            for (int i = 0; i<jsonArray.length(); i++){
                                agree = String.valueOf(i+1);
                            }
                        }
                        tvTotalAgree.setText(agree);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("status", "close");
                params.put("tanggal", date);
                params.put("kasie", index_kasie);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeKasieActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getDisgreeSPK(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_AGREE_SPK_KASIE,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");


                        if (jsonArray.isNull(0)){
                            disagree = "0";
                        } else {
                            for (int i = 0; i<jsonArray.length(); i++){
                                disagree = String.valueOf(i+1);

                            }
                        }
                        tvTotalDisagree.setText(disagree);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("status", "mandor");
                params.put("tanggal", date);
                params.put("kasie", index_kasie);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeKasieActivity.this);
        requestQueue.add(stringRequest);
    }

    private void setDate(){
        Calendar calendar = Calendar.getInstance();
        date = calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH)+1) +"-"+ calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected void onResume() {
        list.clear();
        setDate();
        getResultSPK();
        getAgreeSPK();
        getDisgreeSPK();
        super.onResume();
    }
}