package alex.apparappa;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alex on 21/10/14.
 */
public class allProducers extends ListActivity {
    private Button btnAddProducer;
    private Button btnBack;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> producersList;

    // url to get all products list
    private static String url_all_producers = "http://www.apparappa.altervista.org/appaServices/producer/get_all_producers.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCERS = "produttori";
    private static final String TAG_PID = "idproduttore";
    private static final String TAG_NAME = "nome";

    // products JSONArray
    JSONArray producers = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_producers);

        // Hashmap for ListView
        producersList = new ArrayList<HashMap<String, String>>();

        // Loading producer in Background Thread
        new LoadAllProducers().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single producer
        // launching Edit Producer Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),  editProducer.class);
                // sending pid to next activity
                in.putExtra(TAG_PID, pid);
                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        btnAddProducer = (Button) findViewById(R.id.btnAddProducer);
        btnAddProducer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), addProducer.class);
                startActivity(i);
            }
        });
    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
        class LoadAllProducers extends AsyncTask<String, String, String> {

            /**
             * Before starting background thread Show Progress Dialog
             * */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(allProducers.this);
                pDialog.setMessage("Loading producers. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            /**
             * getting All products from url
             * */
            protected String doInBackground(String... args) {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                // getting JSON string from URL
                JSONObject json = jParser.makeHttpRequest(url_all_producers, "GET", params);

                // Check your log cat for JSON reponse
                Log.d("All Producers: ", json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        producers = json.getJSONArray(TAG_PRODUCERS);

                        // looping through All Producer
                        for (int i = 0; i < producers.length(); i++) {
                            JSONObject c = producers.getJSONObject(i);

                            // Storing each json item in variable
                            String id = c.getString(TAG_PID);
                            String name = c.getString(TAG_NAME);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_PID, id);
                            map.put(TAG_NAME, name);

                            // adding HashList to ArrayList
                            producersList.add(map);
                        }
                    } else {
                        // no products found
                        // Launch Add New producer Activity
                        Intent i = new Intent(getApplicationContext(),
                                addProducer.class);
                        // Closing all previous activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             * **/
            protected void onPostExecute(String file_url) {
                // dismiss the dialog after getting all producers
                pDialog.dismiss();
                // updating UI from Background Thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * Updating parsed JSON data into ListView
                         * */
                        ListAdapter adapter = new SimpleAdapter(
                                allProducers.this, producersList,
                                R.layout.list_item, new String[] { TAG_PID,
                                TAG_NAME},
                                new int[] { R.id.pid, R.id.name });
                        // updating listview
                        setListAdapter(adapter);
                    }
                });
        }
     }
}
