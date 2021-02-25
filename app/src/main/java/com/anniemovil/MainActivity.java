package com.anniemovil;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cc.smarnet.sdk.Observer;
import cc.smarnet.sdk.command.Command;
import cc.smarnet.sdk.command.EscCommand;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private EditText et_user, et_pass;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String a = "";


        et_user = (EditText) findViewById(R.id.et_user);
        et_pass = (EditText) findViewById(R.id.et_pass);

        /*String data = "{ \"usr_login\": [{\"id_usr\": \"movil@annie\",\"psw_usr\":\"qwer\"}]}";
        getData(data);*/

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "{ \"usr_login\": [{\"id_usr\": \"" + et_user.getText().toString() + "\",\"psw_usr\":\"" + et_pass.getText().toString() + "\"}]}";

                try {
                    getData(data);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void testprint(View view){



        final EscCommand escCommand = EscCommand.getInstance();
        try {
            escCommand.connect(this, new Observer() {
                @Override
                public void success(){


                    //escCommand.addPrintAndFeedLines((byte) 3);
                    escCommand.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                    escCommand.addText("Prueba\n");
                    escCommand.addPrintAndLineFeed();
                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);

                    escCommand.addSelectPrintModes (EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

                    escCommand.addText("Id");
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
                    //escCommand.addSetCharacterSize(EscCommand.WIDTH_ZOOM.MUL_2, EscCommand.HEIGHT_ZOOM.MUL_2);
                    for(int j=0; j < 2; j++) {
                        escCommand.addText("9");


                        String desc = "Descricion larga para articulo probando salto de linea";
                        String[] palabras = desc.split(" ");
                        String stracu = "";
                        String strtemp = "";
                        Integer flag = 0;

                        if (palabras.length > 0) {
                            for (int i = 0; i < palabras.length; i++) {
                                if (stracu == "") {
                                    stracu = palabras[i];
                                } else {
                                    strtemp = stracu + " " + palabras[i];
                                    if (strtemp.length() > 18) {
                                        //Log.i("print",stracu);


                                        if (flag == 0) {
                                            escCommand.addSetAbsolutePrintPosition((short) 2);
                                            escCommand.addText(stracu);
                                            stracu = palabras[i];
                                            escCommand.addSetAbsolutePrintPosition((short) 10);
                                            escCommand.addText("999999");
                                            escCommand.addSetAbsolutePrintPosition((short) 14);
                                            escCommand.addText("9999.999");
                                            escCommand.addSetAbsolutePrintPosition((short) 18);
                                            escCommand.addText("9999999" + "\n");
                                            flag = 1;
                                        } else {
                                            escCommand.addSetAbsolutePrintPosition((short) 2);
                                            escCommand.addText(stracu);
                                            stracu = palabras[i];
                                            escCommand.addText("\n");
                                        }
                                    } else {
                                        stracu = stracu + " " + palabras[i];
                                    }
                                }
                            }
                            escCommand.addSetAbsolutePrintPosition((short) 2);
                            escCommand.addText(stracu);
                            escCommand.addText("\n");
                        } else {
                            escCommand.addSetAbsolutePrintPosition((short) 2);
                            escCommand.addText(desc);
                            escCommand.addSetAbsolutePrintPosition((short) 10);
                            escCommand.addText("999999.99");
                            escCommand.addSetAbsolutePrintPosition((short) 14);
                            escCommand.addText("9999.99");
                            escCommand.addSetAbsolutePrintPosition((short) 18);
                            escCommand.addText("9999999.99" + "\n");
                        }
                    }

                    escCommand.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
                    escCommand.addText("GRACIAS POR SU COMPRA");
                    escCommand.addPrintAndLineFeed();
                    escCommand.addText("Facturacion P.O.S desarrollada por: IDSWEB");
                    escCommand.addPrintAndLineFeed();
                    escCommand.addText("S.A.S - NIT 901177455");
                    escCommand.addPrintAndLineFeed();
                    escCommand.addText("www.idsweb.com.co");
                    escCommand.addPrintAndLineFeed();

                    escCommand.addPrintAndFeedLines((byte) 8);
                    escCommand.addCutPaper();


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

    }


    public void getData(String data) throws IOException, JSONException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL("https://annie-fe.com/annie_lab/v2/backend/ServiceInterface/SIapp_usr.php/usr_login");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
            outputStream.write(data.getBytes());
            outputStream.flush();
        }
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.i("response", line);
                    JSONObject obj_json = new JSONObject(line);
                    JSONArray usr_login = new JSONArray(obj_json.getString("usr_login"));
                    String SAuth = "";
                    String SUser = "";

                    for(int i=0; i < usr_login.length(); i++) {
                        JSONObject objusr_login = usr_login.getJSONObject(i);
                        SAuth = objusr_login.getString("token_auth");
                        SUser = objusr_login.getString("id_usr");
                    }

                    Intent invoice = new Intent(this, invoice.class);
                    invoice.putExtra("Auth", SAuth);
                    invoice.putExtra("User", SUser);
                    startActivity(invoice);
                }
            }
        } else {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.i("response", line);
                    JSONObject obj_json = new JSONObject(line);
                    JSONArray message = new JSONArray(obj_json.getString("message"));

                    for(int i=0; i < message.length(); i++) {
                        Log.i("pru",message.getString(i));

                        Toast.makeText(getApplicationContext(),"Error: " + message.getString(i),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}