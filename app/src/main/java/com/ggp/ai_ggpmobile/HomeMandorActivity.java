package com.ggp.ai_ggpmobile;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeMandorActivity extends AppCompatActivity {

    TextView tvName, tvDataNull, tvDataNull01, tvTotalAgree, tvTotalDisagree;
    MaterialCardView cvCreateSPK, cvListSPK;
    RecyclerView rvListSPK, rvListSPK01;
    ImageView ivLogout;

    String nama_mandor, index_mandor, index_kasi, kit_tk, date, agree, disagree, newToken;
    ProgressDialog progressDialog;
    private final List<SPK> list1 = new ArrayList<>();
    private final List<SPK> list = new ArrayList<>();
    private SPK spk;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_mandor);

        Objects.requireNonNull(getSupportActionBar()).hide();

        ivLogout = findViewById(R.id.iv_logout);
        tvName = findViewById(R.id.tv_name);
        tvDataNull = findViewById(R.id.tv_data_null);
        rvListSPK01 = findViewById(R.id.rv_list_data01);
        tvDataNull01 = findViewById(R.id.tv_data_null01);
        tvTotalAgree = findViewById(R.id.tv_total_agree);
        tvTotalDisagree = findViewById(R.id.tv_total_disagree);
        rvListSPK = findViewById(R.id.rv_list_data);
        cvCreateSPK = findViewById(R.id.cv_create_spk);
        cvListSPK = findViewById(R.id.cv_list_spk);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        nama_mandor = SharedPrefManager.getInstance(this).getDataMandor().getNama();
        index_mandor = SharedPrefManager.getInstance(this).getDataMandor().getMandorIndex();
        index_kasi = SharedPrefManager.getInstance(this).getDataMandor().getKasieIndex();
        tvName.setText(nama_mandor);

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
            confirmLogout();
        });

        cvCreateSPK.setOnClickListener(v -> {
            Intent intent = new Intent(this, ObservationalInputActivity.class);
            startActivity(intent);
        });

        setDate();
        cvListSPK.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListSPKActivity.class);
            startActivity(intent);
        });

    }

    private void setDate(){
        Calendar calendar = Calendar.getInstance();
        date = calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH)+1) +"-"+ calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void getResultSPK(){
        list.clear();
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
                            rvListSPK.setLayoutManager(new LinearLayoutManager(HomeMandorActivity.this));
                            SPKAdapter adapter = new SPKAdapter(HomeMandorActivity.this, list);
                            rvListSPK.setAdapter(adapter);
                            adapter.onItemClickCallback(data -> {
                                Intent intent = new Intent(HomeMandorActivity.this, DetailResultSPKActivity.class);
                                intent.putExtra(DetailResultSPKActivity.DATA_SPK, data);
                                intent.putExtra(DetailResultSPKActivity.DATA_INTENT, "mandor");
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
                params.put("status", index_mandor);
                params.put("tanggal", date);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeMandorActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getSPKDisagree(){
        list1.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_SPK_MANDOR_URL,
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
                            rvListSPK01.setLayoutManager(new LinearLayoutManager(HomeMandorActivity.this));
                            SPKAdapter adapter = new SPKAdapter(HomeMandorActivity.this, list1);
                            rvListSPK01.setAdapter(adapter);
                            adapter.onItemClickCallback(data -> {
                                if (data.getPengamatan().equals("Chopper")){
                                    Intent intent = new Intent(HomeMandorActivity.this, DetailResultSPKActivity.class);
                                    intent.putExtra(DetailResultSPKActivity.DATA_SPK, data);
                                    intent.putExtra(DetailResultSPKActivity.DATA_INTENT, "mandor");
                                    intent.putExtra(DetailResultSPKActivity.DATA_NOTES, "kasie_disagree");
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
                params.put("mandor", index_mandor);
                params.put("tanggal", date);
                params.put("status", "mandor");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeMandorActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getAgreeSPK(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_AGREE_SPK,
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
                params.put("status", index_kasi);
                params.put("tanggal", date);
                params.put("mandor", index_mandor);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeMandorActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getDisgreeSPK(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_AGREE_SPK,
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
                params.put("status", "tk");
                params.put("tanggal", date);
                params.put("mandor", index_mandor);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeMandorActivity.this);
        requestQueue.add(stringRequest);
    }

    private void confirmLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeMandorActivity.this);
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
        list.clear();
        getResultSPK();
        getAgreeSPK();
        getDisgreeSPK();
        getSPKDisagree();
    }

}