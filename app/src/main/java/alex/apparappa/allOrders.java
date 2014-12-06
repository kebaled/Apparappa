package alex.apparappa;

/**
 * Created by alex on 06/10/14.
 */

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class allOrders extends ListActivity {
    private Button  btnBack2Order;
    private Button  btnAddOrder;
    private Button  btnBack;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> ordersList;//todo lista fatta da: etichetta(del) , data, stato(inviato/inpreparazione)

    // url to get all products list
    private static String url_all_orders = "http://www.apparappa.altervista.org/appaServices/order/get_all_orders.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ORDERS = "moduli";
    private static final String TAG_PID = "idmodulo";
    private static final String TAG_DATE = "data";
    private static final String TAG_STATE = "stato";//inviato/inpreparazione

    // products JSONArray
    JSONArray orders = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        // Hashmap for ListView
        ordersList= new ArrayList<HashMap<String, String>>();//todo cambia il tipo

        // Loading products in Background Thread
        new LoadAllOrders().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(), editOrder.class);
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

        btnAddOrder = (Button) findViewById(R.id.btnAddOrder);
        btnAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), btnAddOrder.class);
                startActivity(i);
            }
        });
    }

    // Response from Edit Order Activity
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
    class LoadAllOrders extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(allOrders.this);
            pDialog.setMessage("Loading orders. Please wait...");
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
            JSONObject json = jParser.makeHttpRequest(url_all_orders, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Orders: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    orders = json.getJSONArray(TAG_ORDERS);

                    // looping through All Products
                    for (int i = 0; i < orders.length(); i++) {
                        JSONObject c = orders.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String date = c.getString(TAG_DATE);
                        String state = c.getString(TAG_STATE);

                        // creating new HashMap//todo non è una mappa
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_DATE, date);

                        // adding HashList to ArrayList
                        ordersList.add(map);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            addOrder.class);
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
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            allOrders.this, ordersList,
                            R.layout.list_item, new String[] { TAG_PID,
                            TAG_DATE},
                            new int[] { R.id.pid, R.id.date });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }
}