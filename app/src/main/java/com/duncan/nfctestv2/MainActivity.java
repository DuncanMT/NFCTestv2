package com.duncan.nfctestv2;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity {
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";

    private TextView mTextView;
    private Spinner moduleView;
    private ListView studentListView;
    private NfcAdapter mNfcAdapter;
    private ArrayList<String> listItems=new ArrayList<>();
    private ArrayAdapter<String> listAdapter;


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

                String uri = String.format("http://socweb8.napier.ac.uk/~40164965/CardID.php?class=%1$s",
                        module);
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                CustomRequest stringRequest = new CustomRequest(uri,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                    listAdapter.clear();
                                    listAdapter.add(response.toString());

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText("That didn't work!1");
                    }
                });
                queue.add(stringRequest);
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


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText(R.string.nfc_disabled);
        } else {
            mTextView.setText("");
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
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    public static void setupForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][] { new String[] { NfcA.class.getName() } };

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            byte [] idInBinary = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String CardID = readID(idInBinary);
            Toast.makeText(this, CardID, Toast.LENGTH_LONG).show();
            String uri = String.format("http://socweb8.napier.ac.uk/~40164965/CardID.php?card=%1$s",
                    CardID);
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);

            CustomRequest stringRequest = new CustomRequest( uri,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                mTextView.setText(response.getString("SPR_FNM1"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    mTextView.setText("That didn't work!2");
                }
            });
            queue.add(stringRequest);
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

