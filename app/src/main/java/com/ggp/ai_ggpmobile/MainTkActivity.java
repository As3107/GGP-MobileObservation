package com.ggp.ai_ggpmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ggp.ai_ggpmobile.Adapter.SPKAdapter;
import com.ggp.ai_ggpmobile.Model.SPK;
import com.ggp.ai_ggpmobile.Model.TK;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainTkActivity extends AppCompatActivity {

    RecyclerView rvListSPK, rvListSPK01;
    TextView tvName, tvDataNull, tvDataNull01;
    ImageView ivLogout;
    SwipeRefreshLayout swipeRefreshLayout;

    private final List<SPK> list = new ArrayList<>();
    private final List<SPK> list1 = new ArrayList<>();
    ProgressDialog progressDialog;
    String kit, nama, date;
    private TK tk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tk);

        Objects.requireNonNull(getSupportActionBar()).hide();

        rvListSPK = findViewById(R.id.rv_list_data);
        tvDataNull = findViewById(R.id.tv_data_null);
        rvListSPK01 = findViewById(R.id.rv_list_data01);
        tvDataNull01 = findViewById(R.id.tv_data_null01);
        ivLogout = findViewById(R.id.iv_logout);
        tvName = findViewById(R.id.tv_name);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        kit = SharedPrefManager.getInstance(this).getUserLogin().getkit();
        nama = SharedPrefManager.getInstance(this).getDataTK().getNama();

        tvName.setText(nama);

        setDate();

        swipeRefreshLayout.setProgressViewEndTarget(false,0);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            rvListSPK.setVisibility(View.GONE);
            rvListSPK01.setVisibility(View.GONE);
            list.clear();
            getSPK();
            getSPKDisagree();
            new Handler().postDelayed(() ->
                    swipeRefreshLayout.setRefreshing(false), 2000);
        });

        ivLogout.setOnClickListener(v -> {
            confirmLogout();
        });


    }

    private void getSPK(){
        list.clear();

        progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_SPK_TK_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.length()>0){
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject data =jsonArray.getJSONObject(i);

                                SPK spk = new SPK(
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
                            rvListSPK.setLayoutManager(new LinearLayoutManager(MainTkActivity.this));
                            SPKAdapter adapter = new SPKAdapter(MainTkActivity.this, list);
                            rvListSPK.setAdapter(adapter);
                            adapter.onItemClickCallback(data -> {
                                if (data.getPengamatan().equals("Chopper")){
                                    Intent intent = new Intent(MainTkActivity.this, InputChopperActivity.class);
                                    intent.putExtra(InputChopperActivity.DATA_SPK, data);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(this, "Fitur Input Belum Selesai", Toast.LENGTH_SHORT).show();
                                }

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
                params.put("tk", kit);
                params.put("tanggal", date);
                params.put("status", "ok");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainTkActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getSPKDisagree(){
        list1.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_SPK_TK_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.length()>0){
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject data =jsonArray.getJSONObject(i);

                                SPK spk01 = new SPK(
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
                                list1.add(spk01);
                            }
                            tvDataNull01.setVisibility(View.GONE);
                            rvListSPK01.setVisibility(View.VISIBLE);
                            rvListSPK01.setLayoutManager(new LinearLayoutManager(MainTkActivity.this));
                            SPKAdapter adapter = new SPKAdapter(MainTkActivity.this, list1);
                            rvListSPK01.setAdapter(adapter);
                            adapter.onItemClickCallback(data -> {
                                if (data.getPengamatan().equals("Chopper")){
                                    Intent intent = new Intent(MainTkActivity.this, InputChopperActivity.class);
                                    intent.putExtra(InputChopperActivity.DATA_SPK, data);
                                    intent.putExtra(InputChopperActivity.DATA_NOTES, "true");
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(this, "Fitur Input Belum Selesai", Toast.LENGTH_SHORT).show();
                                }

                            });

                        } else {
                            tvDataNull01.setVisibility(View.VISIBLE);
                            rvListSPK01.setVisibility(View.GONE);
                        }

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
                params.put("tk", kit);
                params.put("tanggal", date);
                params.put("status", "tk");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainTkActivity.this);
        requestQueue.add(stringRequest);
    }

    private void setDate(){
        Calendar calendar = Calendar.getInstance();
        date = calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH)+1) +"-"+ calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void confirmLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainTkActivity.this);
        builder.setTitle(R.string.logout);
        builder.setMessage("Apakah Anda Yakin Ingin Keluar Dari Aplikasi");

        builder.setPositiveButton("Ya", (dialog, which) -> {
            SharedPrefManager.getInstance(this).logout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("Tidak", (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSPK();
        getSPKDisagree();
    }
}