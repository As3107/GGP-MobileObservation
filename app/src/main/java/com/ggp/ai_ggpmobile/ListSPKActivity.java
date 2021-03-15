package com.ggp.ai_ggpmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ggp.ai_ggpmobile.Adapter.SPKAdapter;
import com.ggp.ai_ggpmobile.Model.SPK;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListSPKActivity extends AppCompatActivity {

    TextView tvDataNull;
    RecyclerView rvListSPK;

    ProgressDialog progressDialog;
    private final List<SPK> list = new ArrayList<>();
    String index_mandor, date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_s_p_k);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.list_spk);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        tvDataNull = findViewById(R.id.tv_data_null);
        rvListSPK = findViewById(R.id.rv_list_data);

        index_mandor = SharedPrefManager.getInstance(this).getDataMandor().getMandorIndex();

        setDate();
        getSPK();


    }

    private void getSPK(){

        progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_SPK_MANDOR_URL,
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
                            rvListSPK.setLayoutManager(new LinearLayoutManager(ListSPKActivity.this));
                            SPKAdapter adapter = new SPKAdapter(ListSPKActivity.this, list);
                            rvListSPK.setAdapter(adapter);
                            adapter.onItemClickCallback(data -> {

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
                params.put("mandor", index_mandor);
                params.put("tanggal", date);
                params.put("status", "ok");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ListSPKActivity.this);
        requestQueue.add(stringRequest);
    }

    private void setDate(){
        Calendar calendar = Calendar.getInstance();
        date = calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH)+1) +"-"+ calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}