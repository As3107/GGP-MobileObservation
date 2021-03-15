package com.ggp.ai_ggpmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ggp.ai_ggpmobile.Adapter.ChopperAdapter;
import com.ggp.ai_ggpmobile.Model.Chopper;
import com.ggp.ai_ggpmobile.Model.Notes;
import com.ggp.ai_ggpmobile.Model.SPK;
import com.ggp.ai_ggpmobile.Model.TK;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DetailResultSPKActivity extends AppCompatActivity {

    TextView tvLocation, tvTotalBt, tvTotalTh, tvTotalAr, tvTotalMean, tvDataNull, tvPengamat, tvNotes, tvTotalSample, tvTitleNotes;
    EditText etNotes;
    RecyclerView rvDataChopper;
    MaterialButton btDisAgree, btAgree;
    MaterialCardView cvGetNotes;
    ProgressDialog progressDialog;

    private final List<Chopper> list = new ArrayList<>();
    String kit, no_spk, id, status, totalSample, get_index, catatan, set_index;
    double totalBT, totalTH, totalAR, totalMean, total_data;

    public static final String DATA_SPK = "data_spk";
    public static final String DATA_INTENT = "data_intent";
    public static final String DATA_NOTES = "data_notes";
    private SPK spk;
    private TK tk;
    private Notes notes;
    String status_activity, dataNotes, messageBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_result_s_p_k);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.detail_spk);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        tvLocation = findViewById(R.id.tv_location);
        tvDataNull = findViewById(R.id.tv_data_null);
        tvPengamat = findViewById(R.id.tv_pengamat);
        tvTotalBt = findViewById(R.id.tv_total_bt);
        tvTotalTh = findViewById(R.id.tv_total_th);
        tvTotalAr = findViewById(R.id.tv_total_ar);
        tvTotalMean = findViewById(R.id.tv_total_mean);
        tvNotes = findViewById(R.id.tv_notes);
        tvTotalSample = findViewById(R.id.tv_total_data);
        tvTitleNotes = findViewById(R.id.tv_title_note);
        cvGetNotes = findViewById(R.id.cv_get_note);
        etNotes = findViewById(R.id.et_notes);
        rvDataChopper = findViewById(R.id.rv_list_data);
        btDisAgree = findViewById(R.id.bt_disagree);
        btAgree = findViewById(R.id.bt_agree);

        spk = getIntent().getParcelableExtra(DATA_SPK);
        assert spk != null;
        kit = spk.getTk();
        id = spk.getId();
        status_activity = getIntent().getStringExtra(DATA_INTENT);

        dataNotes = getIntent().getStringExtra(DATA_NOTES);

        getProfileTK();
        getNotes();

        if (status_activity.equals("kasie")){
            set_index = spk.getKasie();
        } else if (status_activity.equals("mandor")) {
            set_index = spk.getMandor();
        }

        btDisAgree.setOnClickListener(v -> {
            catatan = etNotes.getText().toString();
            if (status_activity.equals("kasie")){
                status = "mandor";
                messageBuilder = getString(R.string.message_disagree_kasie);
                get_index = spk.getMandor();
            } else if (status_activity.equals("mandor")) {
                messageBuilder = getString(R.string.message_disagree_mandor);
                status = "tk";
                get_index = spk.getTk();
            }
            confirmDisAgree();

        });

        btAgree.setOnClickListener(v -> {
            catatan = etNotes.getText().toString();
            if (status_activity.equals("kasie")){
                status = "close";
                messageBuilder = getString(R.string.message_agree_kasie);
                get_index = "close";
            } else if (status_activity.equals("mandor")) {
                messageBuilder = getString(R.string.message_agree_mandor);
                status = spk.getKasie();
                get_index = spk.getKasie();
            }
            confirmAgree();
        });

    }

    private void confirmDisAgree(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailResultSPKActivity.this);
        builder.setTitle("Konfirmasi Data");
        builder.setMessage(messageBuilder);
        builder.setPositiveButton("Ya", (dialog, which) -> {
                if (!(catatan.matches(""))){
                    sendNotes();
                    updateStatus();
                } else {
                    updateStatus();
                }

        });
        builder.setNegativeButton("Tidak", (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void confirmAgree(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailResultSPKActivity.this);
        builder.setTitle("Konfirmasi Data");
        builder.setMessage(messageBuilder);
        builder.setPositiveButton("Ya", (dialog, which) -> {
                if (!(catatan.matches(""))){
                    sendNotes();
                    updateStatus();
                } else {
                    updateStatus();
                }

        });
        builder.setNegativeButton("Tidak", (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getDataChopper(){
        progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_CHOPPER_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.length()>0){
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject data =jsonArray.getJSONObject(i);

                                Chopper chopper = new Chopper(
                                        data.getInt("id"),
                                        data.getString("no_spk"),
                                        data.getString("tanggal"),
                                        data.getString("kit"),
                                        data.getString("pengamat"),
                                        data.getString("lokasi"),
                                        data.getString("plot"),
                                        data.getString("bt"),
                                        data.getString("th"),
                                        data.getString("ar"),
                                        data.getString("total"));
                                list.add(chopper);
                                totalBT += Double.parseDouble(chopper.getBt());
                                totalTH += Double.parseDouble(chopper.getTh());
                                totalAR += Double.parseDouble(chopper.getAr());
                                totalMean += Double.parseDouble(chopper.getTotal());
                                total_data = totalMean/jsonArray.length();
                                totalSample = String.valueOf(jsonArray.length());

                            }
                            DecimalFormat df = new DecimalFormat("##.##");
                            //totalMean = (totalBT+totalTH+totalAR);
                            tvTotalBt.setText(String.valueOf(totalBT));
                            tvTotalTh.setText(String.valueOf(totalTH));
                            tvTotalAr.setText(String.valueOf(totalAR));
                            tvTotalMean.setText(String.valueOf(df.format(total_data)));
                            tvTotalSample.setText(totalSample);

                            tvDataNull.setVisibility(View.GONE);
                            rvDataChopper.setVisibility(View.VISIBLE);
                            rvDataChopper.setLayoutManager(new LinearLayoutManager(DetailResultSPKActivity.this));
                            ChopperAdapter adapter = new ChopperAdapter(DetailResultSPKActivity.this, list);
                            rvDataChopper.setAdapter(adapter);
                            adapter.onItemClickCallback(data -> {
                                //Toast.makeText(this, data.getId()+" - Fitur masih dalam pengembangan", Toast.LENGTH_SHORT).show();
                            });

                        } else {
                            tvDataNull.setVisibility(View.VISIBLE);
                            rvDataChopper.setVisibility(View.GONE);
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
                params.put("kit", kit);
                params.put("no_spk", no_spk);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(DetailResultSPKActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getProfileTK(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_PROFILE_TK_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))) {
                            JSONObject data = jsonObject.getJSONObject("data");

                            tk = new TK(
                                    data.getString("kit"),
                                    data.getString("nama"),
                                    data.getString("mandor"));

                            no_spk = spk.getNo_spk();
                            tvLocation.setText(spk.getLokasi());
                            tvPengamat.setText(tk.getNama());
                            getDataChopper();
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
        RequestQueue requestQueue = Volley.newRequestQueue(DetailResultSPKActivity.this);
        requestQueue.add(stringRequest);
    }

    private void updateStatus(){
        ProgressDialog progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.UPDATE_STATUS_SPK_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))) {
                            Toast.makeText(this, "SPK Berhasil Diubah", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "SPK Gagal Diubah", Toast.LENGTH_SHORT).show();
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
                params.put("id", id);
                params.put("status", status);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(DetailResultSPKActivity.this);
        requestQueue.add(stringRequest);
    }

    private void sendNotes(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.SET_NOTES_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))) {
                            Toast.makeText(this, "Catatan Berhasil Dikirim", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Catatan Gagal Dikirim", Toast.LENGTH_SHORT).show();
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
                params.put("id", id);
                params.put("indeks", get_index);
                params.put("no_spk", no_spk);
                params.put("catatan", catatan);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(DetailResultSPKActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getNotes(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_NOTES_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        StringBuilder set_catatan = new StringBuilder();
                        if (jsonArray.length()>0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);

                                notes = new Notes(
                                        data.getString("id"),
                                        data.getString("indeks"),
                                        data.getString("no_spk"),
                                        data.getString("catatan")
                                );

                                set_catatan.append(notes.getCatatan());
                                set_catatan.append("\n");
                            }
                            if (dataNotes.equals("kasie_disagree")){
                                tvTitleNotes.setText(R.string.notes_kasie);
                                tvNotes.setText(notes.getCatatan());
                            } else if (dataNotes.equals("mandor")){
                                tvTitleNotes.setText(R.string.notes_mandor);
                                tvNotes.setText(set_catatan.toString());
                            } else {
                                tvTitleNotes.setText(R.string.notes_tk);
                                tvNotes.setText(set_catatan.toString());
                            }

                        } else {
                            tvNotes.setText(R.string.data_null);
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
                params.put("id", id);
                params.put("indeks", set_index);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(DetailResultSPKActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}