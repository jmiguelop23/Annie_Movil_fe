package com.anniemovil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class puntodeventa extends AppCompatActivity {
    AlertDialog e = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntodeventa);

        Button show_menu = (Button) findViewById(R.id.show_menu);
        show_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder nBuilder = new AlertDialog.Builder(puntodeventa.this);
                View nView = getLayoutInflater().inflate(R.layout.menu,null);

                Button salir = (Button) nView.findViewById(R.id.salir);
                salir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        puntodeventa.this.cerrar(null);
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

        this.puntos();
    }

    public void puntos(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String token_auth = getIntent().getExtras().getString("Auth");
        String id_usr = getIntent().getExtras().getString("User");

        String data = "{\"app_pven\":[{\"estado\": \"1\"}]}";

        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
        String servicio = "SIapp_pven.php/read_app_pven";

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

                    TableLayout tabla = (TableLayout)findViewById(R.id.puntosdeventa);

                    int width = 50;

                    JSONObject obj_json = new JSONObject(response.toString());
                    JSONArray app_pven = new JSONArray(obj_json.getString("app_pven"));

                    TableRow fila = new TableRow(this);

                    for(int i=0; i < app_pven.length(); i++) {
                        JSONObject objapp_pven = app_pven.getJSONObject(i);

                        Button btn1 = new Button(this);

                        btn1.setText(objapp_pven.getString("des_pven"));
                        btn1.setTextColor(Color.rgb(0,0,0));
                        btn1.setTextSize((float) 25);
                        btn1.setGravity(Gravity.BOTTOM | Gravity.CENTER);
                        btn1.setBackgroundResource(R.mipmap.btn_venta_1);
                        btn1.setId(Integer.parseInt(objapp_pven.getString("id_pven")));
                        btn1.setTypeface(Typeface.create("@font/comfortaa", Typeface.NORMAL));

                        TableRow.LayoutParams lp = new TableRow.LayoutParams(200, 200, 1f);
                        lp.setMargins(10,10,10,10);

                        fila.addView(btn1, lp);

                        btn1.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
                                Log.i("botones", String.valueOf(v.getId()));

                                String token_auth = getIntent().getExtras().getString("Auth");
                                String id_usr = getIntent().getExtras().getString("User");
                                Intent invoice = new Intent(puntodeventa.this, invoice.class);
                                invoice.putExtra("Auth", token_auth);
                                invoice.putExtra("User", id_usr);
                                invoice.putExtra("puntoventa", String.valueOf(v.getId()));
                                startActivity(invoice);
                            }
                        });

                    }
                    tabla.addView(fila);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void cerrar(View view){
        Intent i = new Intent(puntodeventa.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}