package com.anniemovil;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
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

import cc.smarnet.sdk.Observer;
import cc.smarnet.sdk.command.EscCommand;

public class Menu extends AppCompatActivity {
    private TableLayout tabla;
    private TableLayout tablah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*tabla = (TableLayout)findViewById(R.id.tabla);
        for(int i=0; i<5; i++){
            TableRow fila= new TableRow(this);
            fila.setId(100+i);
            TextView tv_col1 = new TextView(this);
            tv_col1.setId(200+i);
            tv_col1.setText("Celda " + num_celda);

            TextView tv_col2 = new TextView(this);
            tv_col2.setId(200+i);
            tv_col2.setText("Celda " + num_celda+1);

            TextView tv_col3 = new TextView(this);
            tv_col3.setId(200+i);
            tv_col3.setText("Celda " + num_celda+2);

            fila.addView(tv_col1);
            fila.addView(tv_col2);
            fila.addView(tv_col3);
            tabla.addView(fila);
            num_celda=num_celda+3;
        }*/

        this.getData();

    }

    public void printTest(View view) {
        final EscCommand escCommand = EscCommand.getInstance();
        try {
            escCommand.connect(this, new Observer() {
                @Override
                public void success() {

                    escCommand.addPrintAndFeedLines((byte) 3);
                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
                    escCommand.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
                    escCommand.addText("Sample\n");
                    escCommand.addPrintAndLineFeed();

                    escCommand.addText("Completed!\r\n");

                    escCommand.addPrintAndFeedLines((byte) 8);
                    escCommand.addCutPaper();
                }

                @Override
                public void fail() {
                    Log.e("TAG", "msj");
                }

                @Override
                public void error(Throwable throwable) {
                    Log.e("TAG", "msj - " + throwable.toString());
                }

                @Override
                public void other(int code) {
                    Log.e("TAG", "msj - " + code);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("ResourceType")
    public void getData(){


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        String token_auth = getIntent().getExtras().getString("Auth");
        String id_usr = getIntent().getExtras().getString("User");

        //String data = "[{\"ope_log\":\"\",\"campo\":\"fec_emi\",\"operador\":\">\",\"valor\":\"2020-08-05\"},{\"ope_log\":\"AND\",\"campo\":\"id_tipo_doc\",\"operador\":\"=\",\"valor\":\"99\"}]";
        String data = "[{\"textocampo\":\"Sucursal\",\"textoope\":\"igual\",\"campo\":\"id_suc\",\"operador\":\"=\",\"valor\":\"0\"}]";

        String urlbase = "https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/";
        String servicio = "SIcom_fac.php/filtro_com_fac";

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
                    JSONArray com_fac = new JSONArray(obj_json.getString("com_fac"));

                    int idH = 0;

                    tablah = (TableLayout)findViewById(R.id.tablah);

                    TableRow filah = new TableRow(this);
                    filah.setId(idH);

                    TextView tv_head1 = new TextView(this);
                    tv_head1.setId(idH);
                    tv_head1.setText("Número Oficial");
                    tv_head1.setTextColor(Color.rgb(0,0,0));
                    tv_head1.setWidth(getResources().getDisplayMetrics().widthPixels / 6);

                    idH++;
                    TextView tv_head2 = new TextView(this);
                    tv_head2.setId(idH);
                    tv_head2.setText("Cliente");
                    tv_head2.setTextColor(Color.rgb(0,0,0));
                    tv_head2.setWidth(getResources().getDisplayMetrics().widthPixels / 6);


                    idH++;
                    TextView tv_head3 = new TextView(this);
                    tv_head3.setId(idH);
                    tv_head3.setText("Razón social");
                    tv_head3.setTextColor(Color.rgb(0,0,0));
                    tv_head3.setWidth(getResources().getDisplayMetrics().widthPixels / 6);


                    idH++;
                    TextView tv_head4 = new TextView(this);
                    tv_head4.setId(idH);
                    tv_head4.setText("Fecha de Emisión");
                    tv_head4.setTextColor(Color.rgb(0,0,0));
                    tv_head4.setWidth(getResources().getDisplayMetrics().widthPixels / 6);


                    idH++;
                    TextView tv_head5 = new TextView(this);
                    tv_head5.setId(idH);
                    tv_head5.setText("Forma de pago");
                    tv_head5.setTextColor(Color.rgb(0,0,0));
                    tv_head5.setWidth(getResources().getDisplayMetrics().widthPixels / 6);


                    idH++;
                    TextView tv_head6 = new TextView(this);
                    tv_head6.setId(idH);
                    tv_head6.setText("Total");
                    tv_head6.setTextColor(Color.rgb(0,0,0));
                    tv_head6.setWidth(getResources().getDisplayMetrics().widthPixels / 6);

                    filah.addView(tv_head1);
                    filah.addView(tv_head2);
                    filah.addView(tv_head3);
                    filah.addView(tv_head4);
                    filah.addView(tv_head5);
                    filah.addView(tv_head6);

                    tablah.addView(filah);

                    tabla = (TableLayout)findViewById(R.id.tabla);

                    for(int i=0; i < com_fac.length(); i++) {
                        JSONObject objcom_fac = com_fac.getJSONObject(i);

                        TableRow fila= new TableRow(this);
                        fila.setId(i);
                        TextView tv_col1 = new TextView(this);
                        tv_col1.setId(i);
                        tv_col1.setText(objcom_fac.getString("num_ofi"));
                        tv_col1.setTextColor(Color.rgb(0,0,0));
                        tv_col1.setWidth(getResources().getDisplayMetrics().widthPixels / 6);


                        TextView tv_col2 = new TextView(this);
                        tv_col2.setId(i);
                        tv_col2.setText(objcom_fac.getString("id_adq"));
                        tv_col2.setTextColor(Color.rgb(0,0,0));
                        tv_col2.setWidth(getResources().getDisplayMetrics().widthPixels / 6);


                        TextView tv_col3 = new TextView(this);
                        tv_col3.setId(i);
                        tv_col3.setText(objcom_fac.getString("raz_soc_adq"));
                        tv_col3.setTextColor(Color.rgb(0,0,0));
                        tv_col3.setWidth(getResources().getDisplayMetrics().widthPixels / 6);



                        TextView tv_col4 = new TextView(this);
                        tv_col4.setId(i);
                        tv_col4.setText(objcom_fac.getString("fec_emi"));
                        tv_col4.setTextColor(Color.rgb(0,0,0));
                        tv_col4.setWidth(getResources().getDisplayMetrics().widthPixels / 6);


                        TextView tv_col5 = new TextView(this);
                        tv_col5.setId(i);
                        tv_col5.setText(objcom_fac.getString("des_forma_pago"));
                        tv_col5.setTextColor(Color.rgb(0,0,0));
                        tv_col5.setWidth(getResources().getDisplayMetrics().widthPixels / 6);


                        TextView tv_col6 = new TextView(this);
                        tv_col6.setId(i);
                        tv_col6.setText(objcom_fac.getString("mon_tot_pagar"));
                        tv_col6.setTextColor(Color.rgb(0,0,0));
                        tv_col6.setWidth(getResources().getDisplayMetrics().widthPixels / 6);



                        fila.addView(tv_col1);
                        fila.addView(tv_col2);
                        fila.addView(tv_col3);
                        fila.addView(tv_col4);
                        fila.addView(tv_col5);
                        fila.addView(tv_col6);

                        fila.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View rowselect)
                            {
                                rowselect.setBackgroundColor(Color.rgb(0,0,50));
                            }
                        });

                        //tabla.setBackgroundColor(Color.rgb(200,200,200));
                        tabla.addView(fila);
                    }

                    /*Intent menu = new Intent(this, Menu.class);
                    menu.putExtra("Auth", SAuth);
                    menu.putExtra("User", SUser);
                    startActivity(menu);*/

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}