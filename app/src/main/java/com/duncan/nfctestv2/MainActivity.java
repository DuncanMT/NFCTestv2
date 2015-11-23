package com.duncan.nfctestv2;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends NFCActivity {
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";

    private TextView mTextView;
    private Spinner moduleView;
    private ListView studentListView;
    private ArrayList<String> listItems=new ArrayList<>();
    private ArrayAdapter<String> listAdapter;
    private RequestQueue mVolleyQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        moduleView = (Spinner) findViewById(R.id.module);
        studentListView = (ListView) findViewById(R.id.listView);

        ArrayList<String> modules = new ArrayList<String>();
        modules.add("SOC10101");
        modules.add("SET10108");
        modules.add("SET10109");

        mVolleyQueue = Volley.newRequestQueue(this);

        listAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        studentListView.setAdapter(listAdapter);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, modules);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moduleView.setAdapter(adapter);

        moduleView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String module = parent.getItemAtPosition(position).toString();
                String url = "http://napierattendance-duncanmt.rhcloud.com/CardID.php?class="+module;

                JsonArrayRequest classRequest = new JsonArrayRequest( url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                listAdapter.clear();
                                Log.v("onResponse", response.toString());
                                for(int i=0;i<response.length();i++){
                                    try {
                                        JSONObject jb = (JSONObject) response.get(i);
                                        String name = jb.getString("SPR_FNM1");
                                        listAdapter.add(name);
                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("Volley Error", error.toString());
                    }
                });
                mVolleyQueue.add(classRequest);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(LoginState.getUserName(MainActivity.this).length() == 0)
        {
            Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(mainIntent);
            MainActivity.this.finish();
        }


        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            LoginState.clearUserName(this);
            Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(mainIntent);
            MainActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            byte [] idInBinary = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String CardID = readID(idInBinary);
            //Toast.makeText(this, CardID, Toast.LENGTH_LONG).show();
            String url = "http://napierattendance-duncanmt.rhcloud.com/CardID.php?card="+CardID;

            // Request a string response from the provided URL.
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest( url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.v("onResponsemain", response.toString());
                            for(int i=0;i<response.length();i++){
                                try {
                                    JSONObject jb = (JSONObject) response.get(i);
                                    String name = jb.getString("SPR_FNM1");
                                    mTextView.setText(name);
                                }catch(JSONException e){
                                    e.printStackTrace();
                                }

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("Volley Error", error.toString());
                }
            });

            mVolleyQueue.add(jsonArrayRequest);
        }
    }

    private String readID(byte [] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

}

