package com.ggp.ai_ggpmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ggp.ai_ggpmobile.Adapter.ChopperAdapter;
import com.ggp.ai_ggpmobile.Model.Chopper;
import com.ggp.ai_ggpmobile.Model.TK;
import com.ggp.ai_ggpmobile.Model.User;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ObservationalInputActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView tvChoseTK, tvDate, tvTest;
    EditText etNoSPK, etLocation, etRegion;
    Spinner spChoseObservation;
    MaterialButton btSend;

    String observation, date, no_spk, location, region, tk, setDate;
    String index_mandor, index_kasie, kit_tk;
    String tk_01, tk_02;


    boolean[] selectTK;
    ArrayList<Integer> listTK = new ArrayList<>();
    private TK tk_data;
    List<TK> arrayTK = new ArrayList<>();
    String[] tk_nama;
    String[] tk_kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observational_input);

        Objects.requireNonNull(getSupportActionBar()).hide();

        tvChoseTK = findViewById(R.id.tv_chose_tk);
        tvDate = findViewById(R.id.tv_date);
        tvTest = findViewById(R.id.tv_test);
        etNoSPK = findViewById(R.id.et_no_spk);
        etLocation = findViewById(R.id.et_location);
        etRegion = findViewById(R.id.et_region);
        spChoseObservation = findViewById(R.id.sp_observation);
        btSend = findViewById(R.id.bt_send);

        index_mandor = SharedPrefManager.getInstance(this).getDataMandor().getMandorIndex();
        index_kasie = SharedPrefManager.getInstance(this).getDataMandor().getKasieIndex();

        getTK();

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.list_observation, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.list_observation, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChoseObservation.setAdapter(adapter);
        spChoseObservation.setSelection(adapter.getPosition(observation));
        spChoseObservation.setSelection(0);
        spChoseObservation.setBackgroundResource(R.drawable.bg_edit_text);
        spChoseObservation.setOnItemSelectedListener(this);

        btSend.setOnClickListener(v -> {
            validateInput();
            //Toast.makeText(this, tk_01 +"#"+tk_02, Toast.LENGTH_SHORT).show();
        });

        tvDate.setOnClickListener(v -> onDateSet(true) );
        onDateSet(false);

        tvChoseTK.setOnClickListener(v -> {
            //listTK.clear();
            selectTK();
        });

    }

    private void getTK(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_LIST_TK_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.length()>0){
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject data =jsonArray.getJSONObject(i);

                                tk_data = new TK(
                                        data.getString("kit"),
                                        data.getString("nama"),
                                        data.getString("MandorIndex")
                                );
                                arrayTK.add(tk_data);

                            }
                            tk_nama = new String[arrayTK.size()];
                            tk_kit = new String[arrayTK.size()];
                            for(int i=0 ; i< arrayTK.size();i++){
                                tk_nama[i] = arrayTK.get(i).getNama();
                                tk_kit[i] = arrayTK.get(i).getKit();
                            }
                            selectTK = new boolean[tk_nama.length];
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
                params.put("index", index_mandor);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ObservationalInputActivity.this);
        requestQueue.add(stringRequest);
    }

    private void selectTK(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_chose_tk);
        builder.setCancelable(false);
        builder.setMultiChoiceItems(tk_nama, selectTK, (dialog, which, isChecked) -> {
            if (isChecked){
                listTK.add(which);
                //Collections.sort(listTK);
            } else {
                listTK.clear();
            }
        });

        builder.setPositiveButton("Ok", (dialog, which) -> {
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder builderKIT = new StringBuilder();

            if(listTK.size()<2){
                for (int i=0; i<listTK.size(); i++){
                    stringBuilder.append(tk_nama[listTK.get(i)]);
                    builderKIT.append(tk_kit[listTK.get(i)]);
                    tk_01 = tk_kit[listTK.get(0)];
                    tk_02 = null;
                }
                tk = stringBuilder.toString();
            } else if(listTK.size()<3){
                for (int i=0; i<listTK.size(); i++){
                    stringBuilder.append(tk_nama[listTK.get(i)]);
                    builderKIT.append(tk_kit[listTK.get(i)]);
                    if (i != listTK.size()-1){
                        stringBuilder.append("\n");
                        builderKIT.append(",");
                        tk_01 = tk_kit[listTK.get(0)];
                        tk_02 = tk_kit[listTK.get(1)];
                    }
                }
                tk = stringBuilder.toString();
            } else {
                for (int i=0; i<selectTK.length; i++){
                    selectTK[i] = false;
                    listTK.clear();
                }
                tk = getString(R.string.title_chose_tk);
                Toast.makeText(this, "Tidak Booleh Lebih 3", Toast.LENGTH_SHORT).show();
                tk_01 = null;
                tk_02 = null;
            }
            //Toast.makeText(this, tk_01 +"#"+tk_02, Toast.LENGTH_SHORT).show();
            tvChoseTK.setText(tk);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Clear All", (dialog, which) -> {
            for (int i=0; i<selectTK.length; i++){
                selectTK[i] = false;
                listTK.clear();
                tvChoseTK.setText(R.string.title_chose_tk);
            }
            tk_01 = null;
            tk_02 = null;
        });
        builder.show();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spChoseObservation = (Spinner) parent;
        if(spChoseObservation.getId() == R.id.sp_observation) {
            if (parent.getItemAtPosition(position).equals("Pilih Pengamatan")) {
                observation = null;
            } else {
                observation = parent.getItemAtPosition(position).toString();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void validateInput(){
        no_spk = etNoSPK.getText().toString();
        location = etLocation.getText().toString();
        region = etRegion.getText().toString();

        if (!(no_spk==null || location==null || region==null || setDate==null || observation==null || tk==null)){
            if (tk_01!=null && tk_02!=null){
                sendNoSPK1();
                sendNoSPK2();
            } else if (tk_02==null){
                sendNoSPK1();
            }

        } else {
            Toast.makeText(this, "Semua Data Harus Diisi", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNoSPK1(){
        ProgressDialog progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.SEND_NO_SPK_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))) {
                            Toast.makeText(this, "NO SPK Berhasil Dibuat 01", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "NO SPK Gagal Dibuat", Toast.LENGTH_SHORT).show();
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
                params.put("no_spk", no_spk);
                params.put("tanggal", setDate);
                params.put("lokasi", location);
                params.put("wilayah", region);
                params.put("pengamatan", observation);
                params.put("tk", tk_01);
                params.put("mandor", index_mandor);
                params.put("kasie", index_kasie);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ObservationalInputActivity.this);
        requestQueue.add(stringRequest);
    }

    private void sendNoSPK2(){
        ProgressDialog progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.SEND_NO_SPK_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))) {
                            Toast.makeText(this, "NO SPK Berhasil Dibuat 02", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "NO SPK Gagal Dibuat", Toast.LENGTH_SHORT).show();
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
                params.put("no_spk", no_spk);
                params.put("tanggal", setDate);
                params.put("lokasi", location);
                params.put("wilayah", region);
                params.put("pengamatan", observation);
                params.put("tk", tk_02);
                params.put("mandor", index_mandor);
                params.put("kasie", index_kasie);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ObservationalInputActivity.this);
        requestQueue.add(stringRequest);
    }

    public void onDateSet(boolean dialog) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.YEAR, year);
            calendar1.set(Calendar.MONTH, monthOfYear);
            calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDate = year +"-"+ (monthOfYear+1) +"-"+ dayOfMonth;
            date = DateFormat.getDateInstance(DateFormat.LONG).format(calendar1.getTime());
            tvDate.setText(date);
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if (dialog){
            datePickerDialog.show();
        } else {
            datePickerDialog.dismiss();
        }
        setDate = calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH)+1) +"-"+ calendar.get(Calendar.DAY_OF_MONTH);
        date = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        tvDate.setText(date);
    }

}