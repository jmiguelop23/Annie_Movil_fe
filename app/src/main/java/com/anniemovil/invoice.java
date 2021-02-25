package com.anniemovil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cc.smarnet.sdk.Observer;
import cc.smarnet.sdk.command.EscCommand;

public class invoice extends AppCompatActivity {
    JSONArray inv_art_impto;
    JSONArray arr_com_fac_det = new JSONArray();


    JSONObject obj_com_fac_print = new JSONObject();
    JSONObject obj_com_fac_impto_print = new JSONObject();
    JSONObject obj_com_fac_det_impto_print = new JSONObject();
    JSONObject obj_app_emp_print = new JSONObject();
    JSONObject obj_dian_reso_print = new JSONObject();
    JSONObject obj_com_adq_print = new JSONObject();

    JSONArray com_fac_det_print = new JSONArray();
    JSONArray com_fac_det_impto = new JSONArray();

    AlertDialog d = null;
    AlertDialog e = null;

    private EditText ET_cant_item;
    private EditText ET_mon_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        this.get_prm_tipo_id();
        this.get_com_lis_pre();
        this.get_medio_pago();
        this.get_vendedor();
        this.favoritos();

        EditText find_adq = (EditText) findViewById(R.id.find_adq);
        find_adq.setText("222222222222");

        this.getadq(null);
        String id_usr = getIntent().getExtras().getString("User");

        TextView user = (TextView) findViewById(R.id.user);
        user.setText("Usuario: " + id_usr);

        Button show_menu = (Button) findViewById(R.id.show_menu);
        show_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder nBuilder = new AlertDialog.Builder(invoice.this);
                View nView = getLayoutInflater().inflate(R.layout.menu,null);

                Button salir = (Button) nView.findViewById(R.id.salir);
                salir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        invoice.this.cerrar(null);
                    }
                });

                Button volver = (Button) nView.findViewById(R.id.volver);
                volver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });

                nBuilder.setView(nView);
                e = nBuilder.create();
                e.show();
                e.getWindow().setLayout(300, 300);
                e.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


            }
        });

        Button show_add_detail = (Button) findViewById(R.id.add_detail);
        show_add_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(invoice.this);
                View mView = getLayoutInflater().inflate(R.layout.ppadd_detail,null);

                final EditText find_art = (EditText) mView.findViewById(R.id.find_art);
                final Spinner des_art = (Spinner) mView.findViewById(R.id.des_art);
                final TextView id_art = (TextView) mView.findViewById(R.id.id_art);
                final TextView val_id_lis_pre = (TextView) findViewById(R.id.val_id_lis_pre);
                final TextView porc_dcto = (TextView) mView.findViewById(R.id.porc_dcto);
                final TextView precio_unidad = (TextView) mView.findViewById(R.id.precio_unidad);
                final TextView precio_uni = (TextView) mView.findViewById(R.id.precio_uni);
                final EditText cant_item = (EditText) mView.findViewById(R.id.cant_item);
                final TextView mon_impto = (TextView) mView.findViewById(R.id.mon_impto);
                final EditText mon_item = (EditText) mView.findViewById(R.id.mon_item);

                Button ppadd_cerrar = (Button) mView.findViewById(R.id.ppadd_cerrar);
                ppadd_cerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });

                /*mon_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                      @Override
                      public void onFocusChange(View view, boolean hasFocus) {
                          if(hasFocus) {
                              Log.i("hasFocus","1");
                              mon_item.setTextIsSelectable(true);
                          }
                      }
                    }
                );*/

                Button btn_add_det = (Button) mView.findViewById(R.id.btn_add_det);
                btn_add_det.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if( TextUtils.isEmpty(id_art.getText())){
                            Toast.makeText(getApplicationContext(),"Favor seleccione el artículo",Toast.LENGTH_LONG).show();
                        }else{

                            if(!(Float.parseFloat(mon_item.getText().toString()) > 0)){

                                Log.i("pru","1");

                                DecimalFormat format = new DecimalFormat("#.000");

                                Float totalitem = Float.parseFloat(precio_unidad.getText().toString()) * Float.parseFloat(cant_item.getText().toString()) ;
                                Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                mon_item.setText(format.format(totalitem));
                                mon_impto.setText(totalimpto.toString());
                            }else{
                                if((Float.parseFloat(cant_item.getText().toString()) > 1)){

                                    Log.i("pru","2");
                                    DecimalFormat format = new DecimalFormat("#.000");

                                    Float totalitem = Float.parseFloat(precio_unidad.getText().toString()) * Float.parseFloat(cant_item.getText().toString()) ;
                                    Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                    mon_item.setText(format.format(totalitem));
                                    mon_impto.setText(totalimpto.toString());
                                }else{

                                    DecimalFormat format = new DecimalFormat("#.000");

                                    Float totalcant = Float.parseFloat(mon_item.getText().toString()) / Float.parseFloat(precio_unidad.getText().toString());

                                    cant_item.setText(format.format(totalcant));

                                    Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                    mon_impto.setText(totalimpto.toString());
                                }
                            }

                            try {
                                JSONObject  obj_com_fac_det = new JSONObject();

                                obj_com_fac_det.put("id_art", id_art.getText().toString());
                                obj_com_fac_det.put("cant_item", cant_item.getText().toString());
                                obj_com_fac_det.put("mon_impto", mon_impto.getText().toString());
                                obj_com_fac_det.put("des_art", des_art.getSelectedItem().toString());
                                obj_com_fac_det.put("mon_item", mon_item.getText().toString());
                                obj_com_fac_det.put("precio_uni", precio_uni.getText().toString());
                                obj_com_fac_det.put("porc_dcto", porc_dcto.getText().toString());

                                arr_com_fac_det.put(obj_com_fac_det);

                                invoice.this.re_tabla();

                                cant_item.setText("1");
                                precio_uni.setText("0");
                                mon_impto.setText("0");
                                mon_item.setText("0");
                                porc_dcto.setText("0");

                                invoice.this.calculartotales();

                                d.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                cant_item.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                            DecimalFormat format = new DecimalFormat("#.000");

                            Float totalitem = Float.parseFloat(precio_unidad.getText().toString()) * Float.parseFloat(cant_item.getText().toString()) ;
                            Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                            mon_item.setText(format.format(totalitem));
                            mon_impto.setText(totalimpto.toString());
                        }
                        return false;
                    }
                });

                mon_item.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            if(Float.parseFloat(cant_item.getText().toString()) > 1){
                                DecimalFormat format = new DecimalFormat("#.000");

                                Float totalcant = Float.parseFloat(mon_item.getText().toString()) / Float.parseFloat(precio_unidad.getText().toString());

                                cant_item.setText(format.format(totalcant));

                                Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                mon_impto.setText(totalimpto.toString());
                            }
                            else{
                                DecimalFormat format = new DecimalFormat("#.000");

                                Float totalcant = Float.parseFloat(cant_item.getText().toString());

                                Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                mon_impto.setText(totalimpto.toString());
                            }
                        }
                        return false;
                    }
                });

                Button btn_get_art = (Button) mView.findViewById(R.id.btn_get_art);
                btn_get_art.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        String token_auth = getIntent().getExtras().getString("Auth");
                        String id_usr = getIntent().getExtras().getString("User");

                        String data = "[{\"ope_log\":\"OR\",\"campo\":\"id_art\",\"operador\":\"LIKE\",\"valor\":\"%" + find_art.getText().toString() + "%\"},{\"ope_log\":\"OR\",\"campo\":\"des_art\",\"operador\":\"LIKE\",\"valor\":\"%" + find_art.getText().toString() + "%\"}, {\"ope_log\":\"AND\",\"campo\":\"estado\",\"operador\":\"=\",\"valor\":\"1\"}]";

                        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
                        String servicio = "SIinv_art.php/filtro_inv_art";

                        try {
                            URL url = new URL (urlbase + servicio);
                            HttpURLConnection con = (HttpURLConnection)url.openConnection();
                            con.setRequestProperty("Content-Type", "application/json; utf-8");
                            con.setRequestProperty("Accept", "application/json");
                            con.setRequestProperty("Auth", token_auth);
                            con.setRequestProperty("User", id_usr);
                            con.setDoOutput(true);

                            String jsonInputString = data;

                            try(OutputStream os = con.getOutputStream()) {
                                byte[] input = jsonInputString.getBytes("utf-8");
                                os.write(input, 0, input.length);
                            }

                            try(BufferedReader br = new BufferedReader(
                                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                                StringBuilder response = new StringBuilder();
                                String responseLine = null;
                                while ((responseLine = br.readLine()) != null) {
                                    response.append(responseLine.trim());
                                }
                                Log.i("Servicio", response.toString());

                                try {

                                    ArrayList<SpinnerData> id_art_List = new ArrayList<SpinnerData>();

                                    JSONObject obj_json = new JSONObject(response.toString());
                                    JSONArray inv_art = new JSONArray(obj_json.getString("inv_art"));

                                    for(int i=0; i < inv_art.length(); i++) {
                                        JSONObject objinv_art = inv_art.getJSONObject(i);

                                        id_art_List.add(new SpinnerData(objinv_art.getString("id_art"),objinv_art.getString("des_art")));

                                    }

                                    //fill data in spinner
                                    ArrayAdapter<SpinnerData> spinnerAdapter = new ArrayAdapter<SpinnerData>(invoice.this, R.layout.spinner_list,id_art_List);

                                    spinnerAdapter.setDropDownViewResource(R.layout.spinner_list);

                                    des_art.setAdapter(spinnerAdapter);


                                    des_art.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                                        @Override            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            SpinnerData spn = (SpinnerData) parent.getItemAtPosition(position);

                                            id_art.setText(spn.value);

                                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                            StrictMode.setThreadPolicy(policy);
                                            String token_auth = getIntent().getExtras().getString("Auth");
                                            String id_usr = getIntent().getExtras().getString("User");

                                            String data = "{\"inv_art\":[{\"id_art\": \"" + id_art.getText().toString() + "\",\"id_lis_pre\": \"" + val_id_lis_pre.getText().toString() + "\"}]}";
                                            Log.i("data", data);
                                            String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
                                            String servicio = "SIinv_art.php/get_art_pre";

                                            try {
                                                URL url = new URL (urlbase + servicio);
                                                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                                                con.setRequestProperty("Content-Type", "application/json; utf-8");
                                                con.setRequestProperty("Accept", "application/json");
                                                con.setRequestProperty("Auth", token_auth);
                                                con.setRequestProperty("User", id_usr);
                                                con.setDoOutput(true);

                                                String jsonInputString = data;

                                                try(OutputStream os = con.getOutputStream()) {
                                                    byte[] input = jsonInputString.getBytes("utf-8");
                                                    os.write(input, 0, input.length);
                                                }

                                                try(BufferedReader br = new BufferedReader(
                                                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                                                    StringBuilder response = new StringBuilder();
                                                    String responseLine = null;
                                                    while ((responseLine = br.readLine()) != null) {
                                                        response.append(responseLine.trim());
                                                    }
                                                    Log.i("Servicio", response.toString());

                                                    try {
                                                        JSONObject obj_json = new JSONObject(response.toString());
                                                        JSONArray inv_art = new JSONArray(obj_json.getString("inv_art"));

                                                        for(int i=0; i < inv_art.length(); i++) {
                                                            JSONObject objinv_art = inv_art.getJSONObject(i);

                                                            inv_art_impto = new JSONArray(objinv_art.getString("inv_art_impto"));


                                                            porc_dcto.setText(objinv_art.getString("porc_dcto"));

                                                            Float precio_uni_st = Float.valueOf(0);
                                                            Float st_acu = Float.valueOf(0);
                                                            Float porc_acu = Float.valueOf(0);

                                                            for(int j=0; j < inv_art_impto.length(); j++) {
                                                                JSONObject objinv_art_impto = null;

                                                                objinv_art_impto = inv_art_impto.getJSONObject(j);

                                                                if (Boolean.parseBoolean(objinv_art_impto.getString("retencion")) == false) {
                                                                    if (Integer.parseInt(objinv_art_impto.getString("id_tipo_impto")) == 25) {
                                                                        st_acu = st_acu + Float.parseFloat(objinv_art_impto.getString("base_impto"));
                                                                    }else{
                                                                        porc_acu = porc_acu + Float.parseFloat(objinv_art_impto.getString("porc_impto"));
                                                                    }
                                                                }
                                                            }

                                                            Float otr_imp = Float.parseFloat(objinv_art.getString("precio_uni")) * porc_acu / 100;

                                                            precio_uni_st = Float.parseFloat(objinv_art.getString("precio_uni")) + st_acu + otr_imp;

                                                            Float sum_impto = st_acu + otr_imp;

                                                            Log.i("calculos", porc_acu.toString() + " " + st_acu.toString() + " " + otr_imp.toString());


                                                            precio_unidad.setText(precio_uni_st.toString());


                                                            precio_uni.setText(objinv_art.getString("precio_uni"));


                                                            cant_item.setText("1");


                                                            mon_impto.setText(sum_impto.toString());


                                                            mon_item.setText("0");

                                                            //this.calc();
                                                        }

                                                    } catch (JSONException e) {
                                                        Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override            public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                                }


                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                mBuilder.setView(mView);
                /*AlertDialog dialog = mBuilder.create();
                dialog.show();
                dialog.getWindow().setLayout(1850, 480);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);*/

                d = mBuilder.create();
                d.show();
                d.getWindow().setLayout(1890, 500);
                d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        });
    }

   @SuppressLint("ResourceType")
    public void get_prm_tipo_id(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String token_auth = getIntent().getExtras().getString("Auth");
        String id_usr = getIntent().getExtras().getString("User");

        String data = "{\"prm_tipo_id\":[{\"estado\": \"1\"}]}";

        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
        String servicio = "SIprm_tipo_id.php/read_prm_tipo_id";

        try {
            URL url = new URL (urlbase + servicio);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Auth", token_auth);
            con.setRequestProperty("User", id_usr);
            con.setDoOutput(true);

            String jsonInputString = data;

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.i("Servicio", response.toString());

                try {
                    Spinner id_tipo_id = (Spinner) findViewById(R.id.id_tipo_id);

                    ArrayList<SpinnerData> id_tipo_id_List = new ArrayList<SpinnerData>();

                    JSONObject obj_json = new JSONObject(response.toString());
                    JSONArray prm_tipo_id = new JSONArray(obj_json.getString("prm_tipo_id"));

                    for(int i=0; i < prm_tipo_id.length(); i++) {
                        JSONObject objprm_tipo_id = prm_tipo_id.getJSONObject(i);

                        id_tipo_id_List.add(new SpinnerData(objprm_tipo_id.getString("id_tipo_id"),objprm_tipo_id.getString("des_tipo_id")));

                    }

                    //fill data in spinner
                    ArrayAdapter<SpinnerData> spinnerAdapter = new ArrayAdapter<SpinnerData>(invoice.this,
                            R.layout.spinner_list,id_tipo_id_List);

                    spinnerAdapter.setDropDownViewResource(R.layout.spinner_list);


                    id_tipo_id.setAdapter(spinnerAdapter);


                    id_tipo_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            SpinnerData spn = (SpinnerData) parent.getItemAtPosition(position);

                        }

                        @Override            public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ResourceType")
    public void get_medio_pago(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String token_auth = getIntent().getExtras().getString("Auth");
        String id_usr = getIntent().getExtras().getString("User");

        String data = "{\"prm_medio_pago\":[{\"estado\": \"1\"}]}";

        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
        String servicio = "SIprm_medio_pago.php/read_prm_medio_pago";

        try {
            URL url = new URL (urlbase + servicio);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Auth", token_auth);
            con.setRequestProperty("User", id_usr);
            con.setDoOutput(true);

            String jsonInputString = data;

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.i("Servicio", response.toString());

                try {
                    Spinner id_medio_pago = (Spinner) findViewById(R.id.id_medio_pago);

                    ArrayList<SpinnerData> id_medio_pago_List = new ArrayList<SpinnerData>();

                    JSONObject obj_json = new JSONObject(response.toString());
                    JSONArray prm_medio_pago = new JSONArray(obj_json.getString("prm_medio_pago"));

                    for(int i=0; i < prm_medio_pago.length(); i++) {
                        JSONObject objprm_medio_pago = prm_medio_pago.getJSONObject(i);

                        id_medio_pago_List.add(new SpinnerData(objprm_medio_pago.getString("id_medio_pago"),objprm_medio_pago.getString("des_medio_pago")));

                    }

                    //fill data in spinner
                    ArrayAdapter<SpinnerData> spinnerAdapter = new ArrayAdapter<SpinnerData>(invoice.this,
                            R.layout.spinner_list,id_medio_pago_List);

                    spinnerAdapter.setDropDownViewResource(R.layout.spinner_list);
                    id_medio_pago.setAdapter(spinnerAdapter);


                    id_medio_pago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            SpinnerData spn = (SpinnerData) parent.getItemAtPosition(position);

                            //Toast.makeText(invoice.this,spn.value,Toast.LENGTH_LONG).show();

                        }

                        @Override            public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void get_com_lis_pre(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String token_auth = getIntent().getExtras().getString("Auth");
        String id_usr = getIntent().getExtras().getString("User");

        String data = "{\"com_lis_pre\":[{\"estado\": \"1\", \"id_estado\":\"2\"}]}";

        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
        String servicio = "SIcom_lis_pre.php/read_com_lis_pre";

        try {
            URL url = new URL (urlbase + servicio);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Auth", token_auth);
            con.setRequestProperty("User", id_usr);
            con.setDoOutput(true);

            String jsonInputString = data;

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.i("Servicio", response.toString());

                try {
                    Spinner id_lis_pre = (Spinner) findViewById(R.id.id_lis_pre);

                    ArrayList<SpinnerData> id_lis_pre_List = new ArrayList<SpinnerData>();

                    JSONObject obj_json = new JSONObject(response.toString());
                    JSONArray com_lis_pre = new JSONArray(obj_json.getString("com_lis_pre"));

                    for(int i=0; i < com_lis_pre.length(); i++) {
                        JSONObject objcom_lis_pre = com_lis_pre.getJSONObject(i);

                        id_lis_pre_List.add(new SpinnerData(objcom_lis_pre.getString("id_lis_pre"),objcom_lis_pre.getString("des_lis_pre")));

                    }

                    //fill data in spinner
                    ArrayAdapter<SpinnerData> spinnerAdapter = new ArrayAdapter<SpinnerData>(invoice.this,
                            R.layout.spinner_list,id_lis_pre_List);

                    spinnerAdapter.setDropDownViewResource(R.layout.spinner_list);

                    id_lis_pre.setAdapter(spinnerAdapter);



                    id_lis_pre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            SpinnerData spn = (SpinnerData) parent.getItemAtPosition(position);

                            TextView val_id_lis_pre = (TextView) findViewById(R.id.val_id_lis_pre);
                            val_id_lis_pre.setText(spn.value);

                            //Toast.makeText(invoice.this,spn.value,Toast.LENGTH_LONG).show();

                        }

                        @Override            public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void get_vendedor(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String token_auth = getIntent().getExtras().getString("Auth");
        String id_usr = getIntent().getExtras().getString("User");

        String data = "{\"com_terc\":[{\"estado\": \"1\"}]}";

        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
        String servicio = "SIcom_terc.php/read_com_terc";

        try {
            URL url = new URL (urlbase + servicio);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Auth", token_auth);
            con.setRequestProperty("User", id_usr);
            con.setDoOutput(true);

            String jsonInputString = data;

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.i("Servicio", response.toString());

                try {
                    Spinner id_lis_pre = (Spinner) findViewById(R.id.id_vend);

                    ArrayList<SpinnerData> id_terc = new ArrayList<SpinnerData>();

                    JSONObject obj_json = new JSONObject(response.toString());
                    JSONArray com_terc = new JSONArray(obj_json.getString("com_terc"));

                    id_terc.add(new SpinnerData("",""));

                    for(int i=0; i < com_terc.length(); i++) {
                        JSONObject objcom_terc = com_terc.getJSONObject(i);

                        id_terc.add(new SpinnerData(objcom_terc.getString("id_terc"),objcom_terc.getString("raz_soc_terc")));

                    }

                    //fill data in spinner
                    ArrayAdapter<SpinnerData> spinnerAdapter = new ArrayAdapter<SpinnerData>(invoice.this,
                            R.layout.spinner_list,id_terc);

                    spinnerAdapter.setDropDownViewResource(R.layout.spinner_list);
                    id_lis_pre.setAdapter(spinnerAdapter);


                    id_lis_pre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            SpinnerData spn = (SpinnerData) parent.getItemAtPosition(position);

                            TextView val_id_vend = (TextView) findViewById(R.id.val_id_vend);
                            val_id_vend.setText(spn.value);

                            //Toast.makeText(invoice.this,spn.value,Toast.LENGTH_LONG).show();

                        }

                        @Override            public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getadq(View view){

        EditText find_adq = (EditText) findViewById(R.id.find_adq);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String token_auth = getIntent().getExtras().getString("Auth");
        String id_usr = getIntent().getExtras().getString("User");

        String data = "{\"com_adq\":[{\"estado\":\"1\",\"id_adq\": \"" + find_adq.getText().toString() + "\",\"id_tipo_id\":\"13\"}]}";

        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
        String servicio = "SIcom_adq.php/read_com_adq";

        try {
            URL url = new URL (urlbase + servicio);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Auth", token_auth);
            con.setRequestProperty("User", id_usr);
            con.setDoOutput(true);

            String jsonInputString = data;

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.i("Servicio", response.toString());

                try {

                    JSONObject obj_json = new JSONObject(response.toString());
                    JSONArray prm_tipo_id = new JSONArray(obj_json.getString("com_adq"));

                    JSONObject objprm_tipo_id = prm_tipo_id.getJSONObject(0);

                    TextView raz_soc_adq = (TextView) findViewById(R.id.raz_soc_adq);
                    raz_soc_adq.setText(objprm_tipo_id.getString("raz_soc_adq"));

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void cerrar(View view){
        Intent i = new Intent(invoice.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void re_tabla(){

        TableLayout tabla = (TableLayout)findViewById(R.id.tabla);
        int count = tabla.getChildCount();

        /*TextView ancho = (TextView) findViewById(R.id.textView23);
        int width = ancho.getMeasuredWidth();*/

        int width = 120;

        for (int i = 0; i < count; i++) {
            View child = tabla.getChildAt(i); if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }

        try{

            for(int i=0; i < arr_com_fac_det.length(); i++) {
                JSONObject obj_com_fac_det = arr_com_fac_det.getJSONObject(i);

                TableRow fila = new TableRow(this);

                TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
                fila.setLayoutParams(params);

                TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);


                TextView tv_col1 = new TextView(this);
                tv_col1.setText(obj_com_fac_det.getString("cant_item"));
                tv_col1.setTextColor(Color.rgb(0,0,0));
                tv_col1.setWidth(120);
                tv_col1.setGravity(Gravity.RIGHT);
                tv_col1.setTextSize(16);


                TextView tv_col3 = new TextView(this);
                tv_col3.setText("   " + obj_com_fac_det.getString("des_art"));
                tv_col3.setTextColor(Color.rgb(0,0,0));
                tv_col3.setTextSize(16);
                tv_col3.setWidth(280);
                tv_col3.setGravity(Gravity.LEFT);

                TextView tv_col5 = new TextView(this);
                tv_col5.setText(obj_com_fac_det.getString("precio_uni"));
                tv_col5.setTextColor(Color.rgb(0,0,0));
                tv_col5.setTextSize(16);
                tv_col5.setWidth(200);
                //tv_col5.setBackgroundColor(Color.GREEN);
                tv_col5.setGravity(Gravity.RIGHT);

                TextView tv_col7 = new TextView(this);
                tv_col7.setText(obj_com_fac_det.getString("mon_impto"));
                tv_col7.setTextColor(Color.rgb(0,0,0));
                tv_col7.setWidth(180);
                tv_col7.setTextSize(16);
                tv_col7.setGravity(Gravity.RIGHT);

                TextView tv_col8 = new TextView(this);
                tv_col8.setText(obj_com_fac_det.getString("mon_item") + "  ");
                tv_col8.setTextColor(Color.rgb(0,0,0));
                tv_col8.setWidth(170);
                tv_col8.setTextSize(16);
                tv_col8.setGravity(Gravity.RIGHT);


                Button btnborrar = new Button(this);

                btnborrar.setText("");
                btnborrar.setBackgroundResource(R.mipmap.btn_eliminar);
                //btnborrar.setWidth(2);
                //btnborrar.setHeight(5);
                btnborrar.setLayoutParams(buttonParams);
                btnborrar.setId(i);

                btnborrar.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        Log.i("botones", String.valueOf(v.getId()));

                        arr_com_fac_det.remove(v.getId());

                        invoice.this.re_tabla();
                        invoice.this.calculartotales();
                    }
                });

                fila.addView(tv_col1);
                //fila.addView(tv_col2);
                fila.addView(tv_col3);
                //fila.addView(tv_col4);
                fila.addView(tv_col5);
                //fila.addView(tv_col6);
                fila.addView(tv_col7);
                fila.addView(tv_col8);
                fila.addView(btnborrar);
                fila.setBackgroundColor(Color.TRANSPARENT);
                tabla.addView(fila);
                tabla.setBackgroundColor(Color.TRANSPARENT);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void calculartotales(){
        Float totimp = (float) 0;
        Float totdcto = (float) 0;
        Float totbase = (float) 0;
        Float tot = (float) 0;

        DecimalFormat formatea = new DecimalFormat("###,###.##");

        try {
            Log.i("arr_com_fac_det", arr_com_fac_det.toString() );
            for(int i=0; i < arr_com_fac_det.length(); i++) {
                JSONObject obj = null;

                    obj = arr_com_fac_det.getJSONObject(i);

                totimp = totimp + Float.parseFloat(obj.getString("mon_impto"));
                totdcto = totdcto + Float.parseFloat(obj.getString("precio_uni")) * Float.parseFloat(obj.getString("porc_dcto")) / 100 * Float.parseFloat(obj.getString("cant_item"));
                //totbase = totbase + Float.parseFloat(obj.getString("precio_uni")) * Float.parseFloat(obj.getString("cant_item"));
                totbase = totbase + Float.parseFloat(obj.getString("mon_item")) - totimp + totdcto;
                tot = tot + Float.parseFloat(obj.getString("mon_item"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView mon_tot_base_imponible = (TextView) findViewById(R.id.mon_tot_base_imponible);
        mon_tot_base_imponible.setText(String.valueOf(Math.round(Float.parseFloat(totbase.toString()))));

        TextView mon_tot_dctos = (TextView) findViewById(R.id.mon_tot_dctos);
        mon_tot_dctos.setText(totdcto.toString());

        TextView mon_tot_tributos = (TextView) findViewById(R.id.mon_tot_tributos);
        mon_tot_tributos.setText(totimp.toString());

        TextView mon_tot_pagar = (TextView) findViewById(R.id.mon_tot_pagar);
        mon_tot_pagar.setText(String.valueOf(Math.round(Float.parseFloat(tot.toString()))));
    }

    public void facturar(View view){
        JSONObject factura = new JSONObject();

        if(arr_com_fac_det.length() > 0){
            EditText idadq = (EditText) findViewById(R.id.find_adq);
            EditText placa = (EditText) findViewById(R.id.placa);
            TextView val_id_vend = (TextView) findViewById(R.id.val_id_vend);
            TextView val_id_pven = (TextView) findViewById(R.id.val_id_pven);

            TextView mon_tot_base_imponible = (TextView) findViewById(R.id.mon_tot_base_imponible);
            TextView mon_tot_tributos = (TextView) findViewById(R.id.mon_tot_tributos);
            TextView mon_tot_dctos = (TextView) findViewById(R.id.mon_tot_dctos);
            TextView mon_tot_pagar = (TextView) findViewById(R.id.mon_tot_pagar);

            Float mon_tot_con_tributos = Float.parseFloat(mon_tot_base_imponible.getText().toString()) + Float.parseFloat(mon_tot_tributos.getText().toString());
            if( TextUtils.isEmpty(val_id_vend.getText())){
                Toast.makeText(getApplicationContext(),"Favor seleccione el facturador",Toast.LENGTH_LONG).show();
            }else{
                try {
                    JSONObject  obj_com_fac = new JSONObject();
                    JSONArray  arr_com_fac = new JSONArray();

                    String fecemi = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    String puntoventa = getIntent().getExtras().getString("puntoventa");

                    obj_com_fac.put("id_doc", "4");
                    obj_com_fac.put("id_pven", val_id_pven.getText().toString());
                    obj_com_fac.put("id_forma_pago", "1");
                    obj_com_fac.put("id_medio_pago", "10");
                    obj_com_fac.put("fec_emi", fecemi);
                    obj_com_fac.put("id_suc", "0");
                    obj_com_fac.put("id_mnd", "COP");
                    obj_com_fac.put("id_adq", idadq.getText().toString());
                    obj_com_fac.put("placa", placa.getText().toString());
                    obj_com_fac.put("id_vend", val_id_vend.getText().toString());
                    obj_com_fac.put("id_estado", "4");


                    obj_com_fac.put("mon_tot_bruto", mon_tot_base_imponible.getText().toString());
                    obj_com_fac.put("mon_tot_base_imponible", mon_tot_base_imponible.getText().toString());
                    obj_com_fac.put("mon_tot_tributos", mon_tot_tributos.getText().toString());
                    obj_com_fac.put("mon_tot_dctos", mon_tot_dctos.getText().toString());
                    obj_com_fac.put("mon_tot_con_tributos", mon_tot_con_tributos.toString());
                    obj_com_fac.put("mon_tot_pagar", mon_tot_pagar.getText().toString());

                    arr_com_fac.put(obj_com_fac);


                    factura.put("com_fac", arr_com_fac);
                    factura.put("com_fac_det", arr_com_fac_det);

                    Log.i("factura", factura.toString() );

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                String token_auth = getIntent().getExtras().getString("Auth");
                String id_usr = getIntent().getExtras().getString("User");

                String data = factura.toString();

                String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
                String servicio = "SIcom_fac.php/create_datosPOS";

                try {
                    URL url = new URL (urlbase + servicio);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("Auth", token_auth);
                    con.setRequestProperty("User", id_usr);
                    con.setDoOutput(true);
                    Log.i("param", token_auth);
                    Log.i("param", id_usr);

                    String jsonInputString = data;

                    try(OutputStream os = con.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.i("Servicio", response.toString());

                        try {

                            JSONObject obj_json = new JSONObject(response.toString());
                            JSONArray com_fac = new JSONArray(obj_json.getString("com_fac"));
                            JSONObject obj_com_fac = com_fac.getJSONObject(0);

                            this.printinvoice(obj_com_fac.getString("id_fac"));

                            //this.reload();

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Debe seleccionar productos a facturar",Toast.LENGTH_LONG).show();
        }
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    /*public void pventa(View view){
        Intent puntodeventa = new Intent(invoice.this, puntodeventa.class);
        puntodeventa.putExtra("Auth", getIntent().getExtras().getString("Auth"));
        puntodeventa.putExtra("User", getIntent().getExtras().getString("User"));
        startActivity(puntodeventa);
    }*/
    public void printinvoice(String id_fac){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String token_auth = getIntent().getExtras().getString("Auth");
        String id_usr = getIntent().getExtras().getString("User");

        String data = "{\"com_fac\":[{\"id_fac\": \"" + id_fac + "\"}]}";

        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
        String servicio = "SIcom_fac.php/datosPOS";

        try {
            URL url = new URL (urlbase + servicio);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Auth", token_auth);
            con.setRequestProperty("User", id_usr);
            con.setDoOutput(true);

            String jsonInputString = data;

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("ISO-8859-1");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "ISO-8859-1"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.i("Servicio", response.toString());

                try {

                    JSONObject obj_json = new JSONObject(response.toString());

                    JSONArray com_fac = new JSONArray(obj_json.getString("com_fac"));
                    com_fac_det_print = new JSONArray(obj_json.getString("com_fac_det"));
                    JSONArray com_fac_impto = new JSONArray(obj_json.getString("com_fac_impto"));
                    com_fac_det_impto = new JSONArray(obj_json.getString("com_fac_det_impto"));
                    JSONArray app_emp = new JSONArray(obj_json.getString("app_emp"));
                    JSONArray dian_reso = new JSONArray(obj_json.getString("dian_reso"));
                    JSONArray com_adq = new JSONArray(obj_json.getString("com_adq"));

                    obj_com_fac_print = com_fac.getJSONObject(0);
                    obj_com_fac_impto_print = com_fac_impto.getJSONObject(0);

                    obj_app_emp_print = app_emp.getJSONObject(0);
                    obj_dian_reso_print = dian_reso.getJSONObject(0);
                    obj_com_adq_print = com_adq.getJSONObject(0);

                    final EscCommand escCommand = EscCommand.getInstance();
                    try {
                        escCommand.connect(this, new Observer() {
                            @Override
                            public void success() {

                                try {
                                    DecimalFormat df = new DecimalFormat("#.00");
                                    DecimalFormat formatea = new DecimalFormat("###,###.##");
                                    //escCommand.addSelectCodePage()

                                    escCommand.addPrintAndFeedLines((byte) 3);
                                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                                    escCommand.addText(obj_app_emp_print.getString("raz_soc_emp"), "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText(obj_app_emp_print.getString("nom_com"), "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText(obj_app_emp_print.getString("id_emp") + "-" + obj_app_emp_print.getString("dv_emp") , "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText(obj_app_emp_print.getString("des_tipo_regimen") , "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText("Res #" + obj_com_fac_print.getString("num_reso") + " de fecha " + obj_dian_reso_print.getString("fec_ini_reso") + "\nhasta " + obj_dian_reso_print.getString("fec_fin_reso") , "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText("Rango autorizado " + obj_dian_reso_print.getString("rango_ini_reso") + " al " +  obj_dian_reso_print.getString("rango_fin_reso") , "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                                    escCommand.addText(obj_com_fac_print.getString("num_ofi"), "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                                    escCommand.addText(obj_com_fac_print.getString("fec_emi") + " " + obj_com_fac_print.getString("hora_emi"), "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText(obj_app_emp_print.getString("dir_emp"), "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText(obj_app_emp_print.getString("tel_emp"), "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText(obj_app_emp_print.getString("des_muni"), "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();

                                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
                                    escCommand.addText(obj_com_adq_print.getString("des_tipo_id") + ": " , "ISO-8859-1");
                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                                    escCommand.addText(obj_com_adq_print.getString("id_adq"), "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                                    escCommand.addText("Cliente: " , "ISO-8859-1");
                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                                    escCommand.addText(obj_com_adq_print.getString("raz_soc_adq") , "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();

                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                                    escCommand.addText("Placa: " , "ISO-8859-1");
                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                                    escCommand.addText(obj_com_fac_print.getString("placa") , "ISO-8859-1");
                                    escCommand.addText("\n\n");
                                    escCommand.addPrintAndLineFeed();

                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
                                    escCommand.addText("Id");
                                    escCommand.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
                                    escCommand.addSetAbsolutePrintPosition((short) 2);
                                    escCommand.addText("Producto");
                                    escCommand.addSetAbsolutePrintPosition((short) 10);
                                    escCommand.addText("PPU");
                                    escCommand.addSetAbsolutePrintPosition((short) 14);
                                    escCommand.addText("Cant.");
                                    escCommand.addSetAbsolutePrintPosition((short) 18);
                                    escCommand.addText("Valor");
                                    escCommand.addPrintAndLineFeed();

                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                                    for(int i=0; i < com_fac_det_print.length(); i++) {
                                        JSONObject obj_com_fac_det_print = com_fac_det_print.getJSONObject(i);

                                        /*escCommand.addText(obj_com_fac_det_print.getString("sec_item"));
                                        escCommand.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
                                        escCommand.addSetAbsolutePrintPosition((short) 2);
                                        escCommand.addText(obj_com_fac_det_print.getString("des_art"));
                                        escCommand.addSetAbsolutePrintPosition((short) 10);
                                        escCommand.addText(obj_com_fac_det_print.getString("precio_uni_st"));
                                        escCommand.addSetAbsolutePrintPosition((short) 14);
                                        escCommand.addText(obj_com_fac_det_print.getString("cant_item"));
                                        escCommand.addSetAbsolutePrintPosition((short) 18);
                                        escCommand.addText(obj_com_fac_det_print.getString("mon_item"));
                                        escCommand.addPrintAndLineFeed();*/

                                        escCommand.addText(obj_com_fac_det_print.getString("sec_item"));

                                        String desc = obj_com_fac_det_print.getString("des_art");
                                        String[] palabras = desc.split(" ");
                                        String stracu = "";
                                        String strtemp = "";
                                        Integer flag_a = 0;

                                        if (palabras.length > 0) {
                                            for (int j = 0; j < palabras.length; j++) {
                                                if (stracu == "") { //gasolina movil
                                                    stracu = palabras[j];
                                                } else {
                                                    strtemp = stracu + " " + palabras[j];
                                                    if (strtemp.length() > 14) {
                                                        if (flag_a == 0) {
                                                            escCommand.addSetAbsolutePrintPosition((short) 2);
                                                            escCommand.addText(stracu, "ISO-8859-1");
                                                            stracu = palabras[j];
                                                            escCommand.addSetAbsolutePrintPosition((short) 10);
                                                            escCommand.addText(String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_print.getString("precio_uni_st")))));
                                                            escCommand.addSetAbsolutePrintPosition((short) 14);

                                                            escCommand.addText(df.format(Float.parseFloat(obj_com_fac_det_print.getString("cant_item"))));
                                                            escCommand.addSetAbsolutePrintPosition((short) 18);
                                                            escCommand.addText(String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_print.getString("mon_item")))) + "\n");
                                                            flag_a = 1;
                                                        } else {
                                                            escCommand.addSetAbsolutePrintPosition((short) 2);
                                                            escCommand.addText(stracu, "ISO-8859-1");
                                                            stracu = palabras[j];
                                                            escCommand.addText("\n");
                                                        }
                                                    } else {
                                                        stracu = stracu + " " + palabras[j];
                                                    }
                                                }
                                            }
                                            if (flag_a == 0) {
                                                escCommand.addSetAbsolutePrintPosition((short) 2);
                                                escCommand.addText(stracu, "ISO-8859-1");
                                                escCommand.addSetAbsolutePrintPosition((short) 10);
                                                escCommand.addText(String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_print.getString("precio_uni_st")))));
                                                escCommand.addSetAbsolutePrintPosition((short) 14);
                                                escCommand.addText(df.format(Float.parseFloat(obj_com_fac_det_print.getString("cant_item"))));
                                                escCommand.addSetAbsolutePrintPosition((short) 18);
                                                escCommand.addText(String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_print.getString("mon_item")))) + "\n");
                                            }else{
                                                escCommand.addSetAbsolutePrintPosition((short) 2);
                                                escCommand.addText(stracu, "ISO-8859-1");
                                                escCommand.addText("\n");
                                            }
                                        } else {
                                            escCommand.addSetAbsolutePrintPosition((short) 2);
                                            escCommand.addText(desc, "ISO-8859-1");
                                            escCommand.addSetAbsolutePrintPosition((short) 10);
                                            escCommand.addText(String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_print.getString("precio_uni_st")))));
                                            escCommand.addSetAbsolutePrintPosition((short) 14);
                                            escCommand.addText(df.format(Float.parseFloat(obj_com_fac_det_print.getString("cant_item"))));
                                            escCommand.addSetAbsolutePrintPosition((short) 18);
                                            escCommand.addText(String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_print.getString("mon_item")))) + "\n");
                                        }

                                    }

                                    escCommand.addText("\n");

                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);

                                    escCommand.addText("Total: $" + formatea.format(Float.parseFloat(obj_com_fac_print.getString("mon_tot_pagar"))) + "\n");
                                    escCommand.addPrintAndLineFeed();

                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
                                    escCommand.addText("IMPUESTOS INCLUIDOS\n");
                                    escCommand.addPrintAndLineFeed();

                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
                                    escCommand.addText("Id");
                                    escCommand.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
                                    escCommand.addSetAbsolutePrintPosition((short) 4);
                                    escCommand.addText("IMP");
                                    escCommand.addSetAbsolutePrintPosition((short) 10);
                                    escCommand.addText("%");
                                    escCommand.addSetAbsolutePrintPosition((short) 12);
                                    escCommand.addText("Base");
                                    escCommand.addSetAbsolutePrintPosition((short) 18);
                                    escCommand.addText("Valor");
                                    escCommand.addPrintAndLineFeed();

                                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                                    Integer flag = 0;
                                    for(int i=0; i < com_fac_det_impto.length(); i++) {

                                        JSONObject obj_com_fac_det_impto_print = com_fac_det_impto.getJSONObject(i);

                                        if (Integer.parseInt(obj_com_fac_det_impto_print.getString("id_tipo_impto")) != 25) {

                                            escCommand.addText(obj_com_fac_det_impto_print.getString("sec_item"));
                                            escCommand.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
                                            escCommand.addSetAbsolutePrintPosition((short) 4);
                                            escCommand.addText(obj_com_fac_det_impto_print.getString("des_tipo_impto"));
                                            escCommand.addSetAbsolutePrintPosition((short) 10);
                                            escCommand.addText(obj_com_fac_det_impto_print.getString("porc_impto") + "%");
                                            escCommand.addSetAbsolutePrintPosition((short) 12);
                                            escCommand.addText("$" + String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_impto_print.getString("base_imponible")))));
                                            escCommand.addSetAbsolutePrintPosition((short) 18);
                                            escCommand.addText("$" + String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_impto_print.getString("mon_impto")))));
                                            escCommand.addPrintAndLineFeed();
                                        }
                                        else{
                                            flag = 1;
                                        }
                                    }

                                    escCommand.addText("\n");

                                    if(flag == 1){

                                        escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                                        escCommand.addText("Id");
                                        escCommand.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
                                        escCommand.addSetAbsolutePrintPosition((short) 4);
                                        escCommand.addText("IMP");
                                        escCommand.addSetAbsolutePrintPosition((short) 10);
                                        escCommand.addText("Tarifa");
                                        escCommand.addSetAbsolutePrintPosition((short) 18);
                                        escCommand.addText("Valor");
                                        escCommand.addPrintAndLineFeed();

                                        escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                                        for(int i=0; i < com_fac_det_impto.length(); i++) {
                                            JSONObject obj_com_fac_det_impto_print = com_fac_det_impto.getJSONObject(i);

                                            if (Integer.parseInt(obj_com_fac_det_impto_print.getString("id_tipo_impto")) == 25) {
                                                escCommand.addText(obj_com_fac_det_impto_print.getString("sec_item"));
                                                escCommand.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
                                                escCommand.addSetAbsolutePrintPosition((short) 4);
                                                escCommand.addText(obj_com_fac_det_impto_print.getString("des_tipo_impto"));
                                                escCommand.addSetAbsolutePrintPosition((short) 10);
                                                escCommand.addText("$" + String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_impto_print.getString("impto_base")))));
                                                escCommand.addSetAbsolutePrintPosition((short) 18);
                                                escCommand.addText("$" + String.valueOf(Math.round(Float.parseFloat(obj_com_fac_det_impto_print.getString("mon_impto")))));
                                                escCommand.addPrintAndLineFeed();
                                            }
                                        }
                                    }

                                    escCommand.addText("\n");
                                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
                                    escCommand.addText("GRACIAS POR SU COMPRA");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText("Facturación P.O.S desarrollada por: IDSWEB", "ISO-8859-1");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText("S.A.S - NIT 901177455");
                                    escCommand.addPrintAndLineFeed();
                                    escCommand.addText("www.idsweb.com.co");
                                    escCommand.addPrintAndLineFeed();

                                    escCommand.addPrintAndFeedLines((byte) 8);
                                    escCommand.addCutPaper();

                                    /*Intent intent = getIntent();
                                    overridePendingTransition(0, 0);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);*/

                                    Intent intent = getIntent();
                                    overridePendingTransition(0, 0);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(intent);


                                    /*Intent puntodeventa = new Intent(invoice.this, puntodeventa.class);
                                    puntodeventa.putExtra("Auth", getIntent().getExtras().getString("Auth"));
                                    puntodeventa.putExtra("User", getIntent().getExtras().getString("User"));
                                    startActivity(puntodeventa);*/

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void fail() {

                                Toast.makeText(getApplicationContext(),"FAIL",Toast.LENGTH_LONG).show();
                                Log.e("TAG", "msj");
                            }

                            @Override
                            public void error(Throwable throwable) {
                                Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_LONG).show();
                                Log.e("TAG", "msj - " + throwable.toString());
                            }

                            @Override
                            public void other(int code) {
                                Toast.makeText(getApplicationContext(),"other",Toast.LENGTH_LONG).show();
                                Log.e("TAG", "msj - " + code);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void favoritos(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String token_auth = getIntent().getExtras().getString("Auth");
        String id_usr = getIntent().getExtras().getString("User");

        String data = "{\"app_pven\":[{\"estado\": \"1\"}]}";

        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
        String servicio = "SIcom_fac_det.php/favoritos";


        try {
            URL url = new URL (urlbase + servicio);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Auth", token_auth);
            con.setRequestProperty("User", id_usr);
            con.setDoOutput(true);

            String jsonInputString = data;

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.i("Servicio", response.toString());

                TableLayout tabla = (TableLayout)findViewById(R.id.tabla_favoritos);

                int width = 50;

                JSONObject obj_json = new JSONObject(response.toString());
                final JSONArray com_fac_det = new JSONArray(obj_json.getString("com_fac_det"));

                TableRow fila = new TableRow(this);

                int intbtn = 0;

                if(com_fac_det.length() > 5){
                    intbtn = 5;
                }else{
                    intbtn = com_fac_det.length();
                }

                for(int i=0; i < intbtn; i++) {
                    JSONObject objcom_fac_det = com_fac_det.getJSONObject(i);

                    Button btn1 = new Button(this);

                    btn1.setText(objcom_fac_det.getString("des_art"));
                    btn1.setTextColor(Color.rgb(0,0,0));
                    btn1.setTextSize((float) 10);
                    btn1.setGravity(Gravity.BOTTOM | Gravity.CENTER);
                    btn1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    btn1.setId(i);
                    btn1.setTypeface(Typeface.create("@font/comfortaa", Typeface.NORMAL));

                    TableRow.LayoutParams lp = new TableRow.LayoutParams(40, 40, 1f);
                    lp.setMargins(10,10,10,10);

                    fila.addView(btn1, lp);

                    btn1.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {

                            JSONObject objcom_fac_det = null;
                            try {

                                objcom_fac_det = com_fac_det.getJSONObject(v.getId());
                                Log.i("pru", objcom_fac_det.getString("id_art"));

                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(invoice.this);
                                View mView = getLayoutInflater().inflate(R.layout.ppadd_detail,null);

                                final EditText find_art = (EditText) mView.findViewById(R.id.find_art);
                                final Spinner des_art = (Spinner) mView.findViewById(R.id.des_art);
                                final TextView id_art = (TextView) mView.findViewById(R.id.id_art);
                                final TextView val_id_lis_pre = (TextView) findViewById(R.id.val_id_lis_pre);
                                final TextView porc_dcto = (TextView) mView.findViewById(R.id.porc_dcto);
                                final TextView precio_unidad = (TextView) mView.findViewById(R.id.precio_unidad);
                                final TextView precio_uni = (TextView) mView.findViewById(R.id.precio_uni);
                                final EditText cant_item = (EditText) mView.findViewById(R.id.cant_item);
                                final TextView mon_impto = (TextView) mView.findViewById(R.id.mon_impto);
                                final EditText mon_item = (EditText) mView.findViewById(R.id.mon_item);

                                find_art.setText(objcom_fac_det.getString("id_art"));

                                Button ppadd_cerrar = (Button) mView.findViewById(R.id.ppadd_cerrar);
                                ppadd_cerrar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        d.dismiss();
                                    }
                                });

                                Button btn_add_det = (Button) mView.findViewById(R.id.btn_add_det);
                                btn_add_det.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if( TextUtils.isEmpty(id_art.getText())){
                                            Toast.makeText(getApplicationContext(),"Favor seleccione el artículo",Toast.LENGTH_LONG).show();
                                        }else{

                                            if(!(Float.parseFloat(mon_item.getText().toString()) > 0)){

                                                Log.i("pru","1");

                                                DecimalFormat format = new DecimalFormat("#.000");

                                                Float totalitem = Float.parseFloat(precio_unidad.getText().toString()) * Float.parseFloat(cant_item.getText().toString()) ;
                                                Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                                mon_item.setText(format.format(totalitem));
                                                mon_impto.setText(totalimpto.toString());
                                            }else{
                                                if((Float.parseFloat(cant_item.getText().toString()) > 1)){

                                                    Log.i("pru","2");
                                                    DecimalFormat format = new DecimalFormat("#.000");

                                                    Float totalitem = Float.parseFloat(precio_unidad.getText().toString()) * Float.parseFloat(cant_item.getText().toString()) ;
                                                    Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                                    mon_item.setText(format.format(totalitem));
                                                    mon_impto.setText(totalimpto.toString());
                                                }else{

                                                    DecimalFormat format = new DecimalFormat("#.000");

                                                    Float totalcant = Float.parseFloat(mon_item.getText().toString()) / Float.parseFloat(precio_unidad.getText().toString());

                                                    cant_item.setText(format.format(totalcant));

                                                    Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                                    mon_impto.setText(totalimpto.toString());
                                                }
                                            }

                                            try {
                                                JSONObject  obj_com_fac_det = new JSONObject();

                                                obj_com_fac_det.put("id_art", id_art.getText().toString());
                                                obj_com_fac_det.put("cant_item", cant_item.getText().toString());
                                                obj_com_fac_det.put("mon_impto", mon_impto.getText().toString());
                                                obj_com_fac_det.put("des_art", des_art.getSelectedItem().toString());
                                                obj_com_fac_det.put("mon_item", mon_item.getText().toString());
                                                obj_com_fac_det.put("precio_uni", precio_uni.getText().toString());
                                                obj_com_fac_det.put("porc_dcto", porc_dcto.getText().toString());

                                                arr_com_fac_det.put(obj_com_fac_det);

                                                invoice.this.re_tabla();

                                                cant_item.setText("1");
                                                precio_uni.setText("0");
                                                mon_impto.setText("0");
                                                mon_item.setText("0");
                                                porc_dcto.setText("0");

                                                invoice.this.calculartotales();

                                                d.dismiss();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                                cant_item.setOnKeyListener(new View.OnKeyListener() {
                                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                                            DecimalFormat format = new DecimalFormat("#.000");

                                            Float totalitem = Float.parseFloat(precio_unidad.getText().toString()) * Float.parseFloat(cant_item.getText().toString()) ;
                                            Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                            mon_item.setText(format.format(totalitem));
                                            mon_impto.setText(totalimpto.toString());
                                        }
                                        return false;
                                    }
                                });

                                mon_item.setOnKeyListener(new View.OnKeyListener() {
                                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                            DecimalFormat format = new DecimalFormat("#.000");

                                            Float totalcant = Float.parseFloat(mon_item.getText().toString()) / Float.parseFloat(precio_unidad.getText().toString());

                                            cant_item.setText(format.format(totalcant));

                                            Float totalimpto = Float.parseFloat(mon_impto.getText().toString()) * Float.parseFloat(cant_item.getText().toString());

                                            mon_impto.setText(totalimpto.toString());
                                        }
                                        return false;
                                    }
                                });

                                Button btn_get_art = (Button) mView.findViewById(R.id.btn_get_art);
                                btn_get_art.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);

                                        String token_auth = getIntent().getExtras().getString("Auth");
                                        String id_usr = getIntent().getExtras().getString("User");

                                        String data = "[{\"ope_log\":\"OR\",\"campo\":\"id_art\",\"operador\":\"LIKE\",\"valor\":\"%" + find_art.getText().toString() + "%\"},{\"ope_log\":\"OR\",\"campo\":\"des_art\",\"operador\":\"LIKE\",\"valor\":\"%" + find_art.getText().toString() + "%\"}, {\"ope_log\":\"AND\",\"campo\":\"estado\",\"operador\":\"=\",\"valor\":\"1\"}]";
                                        Log.i("data1", data);

                                        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
                                        String servicio = "SIinv_art.php/filtro_inv_art";

                                        try {
                                            URL url = new URL (urlbase + servicio);
                                            HttpURLConnection con = (HttpURLConnection)url.openConnection();
                                            con.setRequestProperty("Content-Type", "application/json; utf-8");
                                            con.setRequestProperty("Accept", "application/json");
                                            con.setRequestProperty("Auth", token_auth);
                                            con.setRequestProperty("User", id_usr);
                                            con.setDoOutput(true);



                                            String jsonInputString = data;

                                            try(OutputStream os = con.getOutputStream()) {
                                                byte[] input = jsonInputString.getBytes("utf-8");
                                                os.write(input, 0, input.length);
                                            }

                                            try(BufferedReader br = new BufferedReader(
                                                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                                                StringBuilder response = new StringBuilder();
                                                String responseLine = null;
                                                while ((responseLine = br.readLine()) != null) {
                                                    response.append(responseLine.trim());
                                                }
                                                Log.i("Servicio", response.toString());

                                                try {

                                                    ArrayList<SpinnerData> id_art_List = new ArrayList<SpinnerData>();

                                                    JSONObject obj_json = new JSONObject(response.toString());
                                                    JSONArray inv_art = new JSONArray(obj_json.getString("inv_art"));

                                                    for(int i=0; i < inv_art.length(); i++) {
                                                        JSONObject objinv_art = inv_art.getJSONObject(i);

                                                        id_art_List.add(new SpinnerData(objinv_art.getString("id_art"),objinv_art.getString("des_art")));

                                                    }

                                                    //fill data in spinner
                                                    ArrayAdapter<SpinnerData> spinnerAdapter = new ArrayAdapter<SpinnerData>(invoice.this, R.layout.spinner_list,id_art_List);

                                                    spinnerAdapter.setDropDownViewResource(R.layout.spinner_list);

                                                    des_art.setAdapter(spinnerAdapter);


                                                    des_art.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                                                        @Override            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                            SpinnerData spn = (SpinnerData) parent.getItemAtPosition(position);

                                                            id_art.setText(spn.value);



                                                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                                            StrictMode.setThreadPolicy(policy);
                                                            String token_auth = getIntent().getExtras().getString("Auth");
                                                            String id_usr = getIntent().getExtras().getString("User");

                                                            String data = "{\"inv_art\":[{\"id_art\": \"" + id_art.getText().toString() + "\",\"id_lis_pre\": \"" + val_id_lis_pre.getText().toString() + "\"}]}";
                                                            Log.i("data", data);
                                                            String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
                                                            String servicio = "SIinv_art.php/get_art_pre";

                                                            try {
                                                                URL url = new URL (urlbase + servicio);
                                                                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                                                                con.setRequestProperty("Content-Type", "application/json; utf-8");
                                                                con.setRequestProperty("Accept", "application/json");
                                                                con.setRequestProperty("Auth", token_auth);
                                                                con.setRequestProperty("User", id_usr);
                                                                con.setDoOutput(true);

                                                                String jsonInputString = data;

                                                                try(OutputStream os = con.getOutputStream()) {
                                                                    byte[] input = jsonInputString.getBytes("utf-8");
                                                                    os.write(input, 0, input.length);
                                                                }

                                                                try(BufferedReader br = new BufferedReader(
                                                                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                                                                    StringBuilder response = new StringBuilder();
                                                                    String responseLine = null;
                                                                    while ((responseLine = br.readLine()) != null) {
                                                                        response.append(responseLine.trim());
                                                                    }
                                                                    Log.i("Servicio", response.toString());

                                                                    try {
                                                                        JSONObject obj_json = new JSONObject(response.toString());
                                                                        JSONArray inv_art = new JSONArray(obj_json.getString("inv_art"));

                                                                        for(int i=0; i < inv_art.length(); i++) {
                                                                            JSONObject objinv_art = inv_art.getJSONObject(i);

                                                                            inv_art_impto = new JSONArray(objinv_art.getString("inv_art_impto"));


                                                                            porc_dcto.setText(objinv_art.getString("porc_dcto"));

                                                                            Float precio_uni_st = Float.valueOf(0);
                                                                            Float st_acu = Float.valueOf(0);
                                                                            Float porc_acu = Float.valueOf(0);

                                                                            for(int j=0; j < inv_art_impto.length(); j++) {
                                                                                JSONObject objinv_art_impto = null;

                                                                                objinv_art_impto = inv_art_impto.getJSONObject(j);

                                                                                if (Boolean.parseBoolean(objinv_art_impto.getString("retencion")) == false) {
                                                                                    if (Integer.parseInt(objinv_art_impto.getString("id_tipo_impto")) == 25) {
                                                                                        st_acu = st_acu + Float.parseFloat(objinv_art_impto.getString("base_impto"));
                                                                                    }else{
                                                                                        porc_acu = porc_acu + Float.parseFloat(objinv_art_impto.getString("porc_impto"));
                                                                                    }
                                                                                }
                                                                            }

                                                                            Float otr_imp = Float.parseFloat(objinv_art.getString("precio_uni")) * porc_acu / 100;

                                                                            precio_uni_st = Float.parseFloat(objinv_art.getString("precio_uni")) + st_acu + otr_imp;

                                                                            Float sum_impto = st_acu + otr_imp;

                                                                            Log.i("calculos", porc_acu.toString() + " " + st_acu.toString() + " " + otr_imp.toString());


                                                                            precio_unidad.setText(precio_uni_st.toString());


                                                                            precio_uni.setText(objinv_art.getString("precio_uni"));


                                                                            cant_item.setText("1");


                                                                            mon_impto.setText(sum_impto.toString());


                                                                            mon_item.setText("0");

                                                                            //this.calc();
                                                                        }

                                                                    } catch (JSONException e) {
                                                                        Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                                                                    }
                                                                }

                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override            public void onNothingSelected(AdapterView<?> adapterView) {

                                                        }
                                                    });

                                                } catch (JSONException e) {
                                                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                                                }


                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                mBuilder.setView(mView);
                /*AlertDialog dialog = mBuilder.create();
                dialog.show();
                dialog.getWindow().setLayout(1850, 480);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);*/

                                d = mBuilder.create();
                                d.show();
                                d.getWindow().setLayout(1890, 500);
                                d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                btn_get_art.callOnClick();

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                }
                tabla.addView(fila);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}