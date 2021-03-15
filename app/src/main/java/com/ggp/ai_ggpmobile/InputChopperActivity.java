package com.ggp.ai_ggpmobile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ggp.ai_ggpmobile.Adapter.ChopperAdapter;
import com.ggp.ai_ggpmobile.Model.Chopper;
import com.ggp.ai_ggpmobile.Model.Notes;
import com.ggp.ai_ggpmobile.Model.SPK;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.makeramen.roundedimageview.RoundedImageView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InputChopperActivity extends AppCompatActivity {

    EditText etPlot, etBt, etTh, etAr, etNotes;
    RoundedImageView ivUpload1, ivUpload2, ivUpload3;
    MaterialButton btSave, btSend;
    MaterialCardView cvGetNotes;
    TextView tvLocation, tvTotalBt, tvTotalTh, tvTotalAr, tvTotalMean, tvDataNull, tvPengamat, tvNotes, tvTotalSample;
    RecyclerView rvDataChopper;
    private Bitmap image1, image2, image3;

    private final List<Chopper> list = new ArrayList<>();
    ProgressDialog progressDialog;
    Long tsLong;
    String kit, ts, plot, bt, th, ar, pengamat, date, no_spk, id, mandor, dataNotes, totalSample;
    String  updatePlot, updateBt, updateTh, updateAr, catatan;
    int id_chopper;
    double totalBT, totalTH, totalAR, totalMean;
    float btReal, thReal, arReal;

    public static final String DATA_SPK = "data_spk";
    public static final String DATA_NOTES = "data_notes";

    private SPK spk;
    private Notes notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_chopper);

        Objects.requireNonNull(getSupportActionBar()).hide();

        etPlot = findViewById(R.id.et_plot);
        etBt = findViewById(R.id.et_bt);
        etTh = findViewById(R.id.et_th);
        etAr = findViewById(R.id.et_ar);
        etNotes = findViewById(R.id.et_notes);
        ivUpload1 = findViewById(R.id.iv_upload1);
        ivUpload2 = findViewById(R.id.iv_upload2);
        ivUpload3 = findViewById(R.id.iv_upload3);
        btSave = findViewById(R.id.bt_save);
        btSend = findViewById(R.id.bt_send);
        tvLocation = findViewById(R.id.tv_location);
        tvDataNull = findViewById(R.id.tv_data_null);
        tvPengamat = findViewById(R.id.tv_pengamat);
        tvTotalBt = findViewById(R.id.tv_total_bt);
        tvTotalTh = findViewById(R.id.tv_total_th);
        tvTotalAr = findViewById(R.id.tv_total_ar);
        tvTotalMean = findViewById(R.id.tv_total_mean);
        tvNotes = findViewById(R.id.tv_notes);
        tvTotalSample = findViewById(R.id.tv_total_data);
        cvGetNotes = findViewById(R.id.cv_get_note);
        rvDataChopper = findViewById(R.id.rv_list_data);

        spk = getIntent().getParcelableExtra(DATA_SPK);
        pengamat = SharedPrefManager.getInstance(this).getDataTK().getNama();
        no_spk = spk.getNo_spk();
        id = spk.getId();
        mandor = spk.getMandor();

        dataNotes = getIntent().getStringExtra(DATA_NOTES);
        if (dataNotes!=null && dataNotes.equals("true")){
            cvGetNotes.setVisibility(View.VISIBLE);
        } else {
            cvGetNotes.setVisibility(View.GONE);
        }

        tvLocation.setText(spk.getLokasi());
        tvPengamat.setText(pengamat);

        btSave.setOnClickListener(v -> validateInput());

        ivUpload1.setOnClickListener(v -> selectImage(1));
        ivUpload2.setOnClickListener(v -> selectImage(2));
        ivUpload3.setOnClickListener(v -> selectImage(3));

        tsLong = System.currentTimeMillis()/1000;
        ts = tsLong.toString();

        kit = SharedPrefManager.getInstance(InputChopperActivity.this).getUserLogin().getkit();
        getDataChopper();
        getNotes();

        btSend.setOnClickListener(v -> {
            catatan = etNotes.getText().toString();
            if (totalBT!=0){
                confirmSend();
            } else {
                Toast.makeText(this, "Data Tidak Ada", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void confirmSend(){
        AlertDialog.Builder builder = new AlertDialog.Builder(InputChopperActivity.this);
        builder.setTitle("Konfirmasi Data");
        builder.setMessage("Apakah Anda yakin semua data pengamatan sudah LENGKAP dan akan mengimkan ke MANDOR ?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            if (!(catatan.matches(""))){
                sendNotes();
                sendDataChopper();
            } else {
                sendDataChopper();
            }

        });
        builder.setNegativeButton("Tidak", (dialog, which) -> {
           dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void validateInput(){
        plot = etPlot.getText().toString();
        bt = etBt.getText().toString();
        th = etTh.getText().toString();
        ar = etAr.getText().toString();

        if(!(plot.matches("") || bt.matches("") || th.matches("") || ar.matches("")
                || (Integer.parseInt(bt)>100) || (Integer.parseInt(bt)>100) || (Integer.parseInt(bt)>100))){
            saveDataChopper();
        } else {
            Toast.makeText(this, "Semua Data Harus Diisi atau Data Lebih dari 100", Toast.LENGTH_LONG).show();
        }
    }

    private void saveDataChopper(){

        progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();

        double dBt = (Double.parseDouble(bt)*40)/100;
        double dTh = (Double.parseDouble(th)*40)/100;
        double dAr = (Double.parseDouble(ar)*20)/100;
        double dTotal = dBt+dTh+dAr;
        date = calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH)+1) +"-"+ calendar.get(Calendar.DAY_OF_MONTH);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.SEND_CHOPPER_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))){
                            Toast.makeText(this, "Berhasil Terkirim", Toast.LENGTH_SHORT).show();
                            //clearInput();
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                            //getDataChopper("false");
                        } else {
                            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("no_spk", spk.getNo_spk());
                params.put("tanggal", date);
                params.put("kit", SharedPrefManager.getInstance(InputChopperActivity.this).getDataTK().getKit());
                params.put("pengamat", SharedPrefManager.getInstance(InputChopperActivity.this).getDataTK().getNama());
                params.put("lokasi", spk.getLokasi());
                params.put("plot", plot);
                params.put("bt", String.valueOf(dBt));
                params.put("th", String.valueOf(dTh));
                params.put("ar", String.valueOf(dAr));
                params.put("total", String.valueOf(dTotal));
                return params;
            }
        };
            RequestQueue requestQueue = Volley.newRequestQueue(InputChopperActivity.this);
            requestQueue.add(stringRequest);
    }

    private void sendDataChopper(){

        progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.SEND_DATA_MANDOR_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))){
                            Toast.makeText(this, "Data Berhasil Terkirim Kemandor", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("status", mandor);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(InputChopperActivity.this);
        requestQueue.add(stringRequest);
    }

    private void updateDatAChopper(){
        progressDialog = ProgressDialog.show(this, null, null, true);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.item_progres_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();

        double dBt = (Double.parseDouble(updateBt)*40)/100;
        double dTh = (Double.parseDouble(updateTh)*40)/100;
        double dAr = (Double.parseDouble(updateAr)*20)/100;
        double dTotal = dBt+dTh+dAr;
        date = calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH)+1) +"-"+ calendar.get(Calendar.DAY_OF_MONTH);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.UPDATE_CHOPPER_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("message").equalsIgnoreCase(String.valueOf(true))){
                            Toast.makeText(this, "Data Berhasil Diubah", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        } else {
                            Toast.makeText(this, "Data Gagal Diubah", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id_chopper));
                params.put("plot", updatePlot);
                params.put("bt", String.valueOf(dBt));
                params.put("th", String.valueOf(dTh));
                params.put("ar", String.valueOf(dAr));
                params.put("total", String.valueOf(dTotal));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(InputChopperActivity.this);
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
                params.put("indeks", mandor);
                params.put("no_spk", no_spk);
                params.put("catatan", catatan);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(InputChopperActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getNotes(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_NOTES_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (jsonArray.length()>0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);

                                notes = new Notes(
                                        data.getString("id"),
                                        data.getString("indeks"),
                                        data.getString("no_spk"),
                                        data.getString("catatan")
                                );
                            }
                            tvNotes.setText(notes.getCatatan());
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
                params.put("indeks", kit);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(InputChopperActivity.this);
        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void getDataChopper(){
        list.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.GET_CHOPPER_URL,
                response -> {
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
                                        data.getString("total")
                                );
                                list.add(chopper);
                                totalBT += Double.parseDouble(chopper.getBt())/jsonArray.length();
                                totalTH += Double.parseDouble(chopper.getTh())/jsonArray.length();
                                totalAR += Double.parseDouble(chopper.getAr())/jsonArray.length();
                                totalMean += Double.parseDouble(chopper.getTotal())/jsonArray.length();
                                totalSample = String.valueOf(jsonArray.length());
                            }
                            DecimalFormat df = new DecimalFormat("##.##");
                            //totalMean = (totalBT+totalTH+totalAR);
                            tvTotalBt.setText(String.valueOf(df.format(totalBT))+"%");
                            tvTotalTh.setText(String.valueOf(df.format(totalTH))+"%");
                            tvTotalAr.setText(String.valueOf(df.format(totalAR))+"%");
                            tvTotalMean.setText(String.valueOf(df.format(totalMean))+"%");
                            tvTotalSample.setText(totalSample);

                            tvDataNull.setVisibility(View.GONE);
                            rvDataChopper.setVisibility(View.VISIBLE);
                            rvDataChopper.setLayoutManager(new LinearLayoutManager(InputChopperActivity.this));
                            ChopperAdapter adapter = new ChopperAdapter(InputChopperActivity.this, list);
                            rvDataChopper.setAdapter(adapter);
                            adapter.onItemClickCallback(this::editDataChopper);
                        } else {
                            tvDataNull.setVisibility(View.VISIBLE);
                            rvDataChopper.setVisibility(View.GONE);
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
                params.put("kit", kit);
                params.put("no_spk", no_spk);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(InputChopperActivity.this);
        requestQueue.add(stringRequest);
    }

    private void editDataChopper(Chopper choppers){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.item_edit_chopper, null);

        EditText etPlots, etBts, etThs, etArs;
        MaterialButton btSaves;

        DecimalFormat df = new DecimalFormat("##.##");
        btReal = Float.parseFloat(choppers.getBt())*100/40;
        thReal = Float.parseFloat(choppers.getTh())*100/40;
        arReal = Float.parseFloat(choppers.getAr())*100/20;

        etPlots = dialogView.findViewById(R.id.et_plot);
        etBts = dialogView.findViewById(R.id.et_bt);
        etThs = dialogView.findViewById(R.id.et_th);
        etArs = dialogView.findViewById(R.id.et_ar);
        btSaves = dialogView.findViewById(R.id.bt_save);

        etPlots.setText(choppers.getPlot(), TextView.BufferType.EDITABLE);
        etBts.setText(df.format(btReal), TextView.BufferType.EDITABLE);
        etThs.setText(df.format(thReal), TextView.BufferType.EDITABLE);
        etArs.setText(df.format(arReal), TextView.BufferType.EDITABLE);

        btSaves.setOnClickListener(v -> {
            id_chopper = choppers.getId();
            updatePlot = etPlots.getText().toString();
            updateBt = etBts.getText().toString();
            updateTh = etThs.getText().toString();
            updateAr = etArs.getText().toString();
            updateDatAChopper();
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void selectImage(int intent) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, (dialog, item) -> {

            if (intent==1){
                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 10);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 100);
                }else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            } else if (intent==2){
                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 20);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 200);
                }else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            } else if (intent==3){
                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 30);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 300);
                }else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }

        });
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                /*case 1:
                    if ( resultCode == RESULT_OK && data != null) {
                        latitude = data.getDoubleExtra("lat", latitude);
                        longitude = data.getDoubleExtra("lng", longitude);
                        tvLat.setText(new DecimalFormat("##.####").format(latitude));
                        tvLon.setText(new DecimalFormat("##.####").format(longitude));
                    }
                    break;*/

                case 10:
                    if ( resultCode == RESULT_OK && data != null) {
                        image1 = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        ivUpload1.setImageBitmap(image1);
                    }
                    break;

                case 20:
                    if (resultCode == RESULT_OK && data!=null){
                        image2 = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        ivUpload2.setImageBitmap(image2);
                    }
                    break;

                case 30:
                    if (resultCode == RESULT_OK && data!=null){
                        image3 = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        ivUpload3.setImageBitmap(image3);
                    }
                    break;

                case 100:
                    if ( resultCode == RESULT_OK && data != null) {
                        Uri imageUri = data.getData();
                        try {
                            image1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            ivUpload1.setImageBitmap(image1);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 200:
                    if ( resultCode == RESULT_OK && data != null) {
                        Uri imageUri = data.getData();
                        try {
                            image2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            ivUpload2.setImageBitmap(image2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 300:
                    if ( resultCode == RESULT_OK && data != null) {
                        Uri imageUri = data.getData();
                        try {
                            image3 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            ivUpload3.setImageBitmap(image3);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }


}