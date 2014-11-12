package alex.apparappa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 27/10/14.
 */
public class editProductsOfProducer extends Activity {

    private TextView txtNameEr;//nome del produttore
    private TextView txtName;//nome del prodotto
    private TextView txtInfoTag;
    private EditText editInfoTag;//etichetta informativa del prodotto

    private Button btnSave;
    private Button btnCancel;

    String pid;//del prodotto
    String pidEr;//del produttore

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single product url
    private static final String url_product_of_producer_details = "http://www.apparappa.altervista.org/appaServices/productsOfProducer/get_product_of_producer_details.php";

    // url to update product of producer
    private static final String url_update_product_of_producer = "http://www.apparappa.altervista.org/appaServices/productsOfProducer/update_product_of_producer.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_NAME = "nomeProdotto";
    private static final String TAG_NAMEer = "nomeProduttore";
    private static final String TAG_PRODMN = "prodmn";
    private static final String TAG_PID = "idprodotto";
    private static final String TAG_PIDer = "idproduttore";
    private static final String TAG_INFOTAG = "etichetta";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products_of_producer);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);
        pidEr = i.getStringExtra(TAG_PIDer);

        // Getting complete product details in background thread
        new GetProductOfProducerDetails().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                new SaveProductOfProducerDetails().execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) { finish(); }
        });

    }

    /**
     * Background Async Task to Get complete product details
     * */
    class GetProductOfProducerDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */

        private JSONObject productOfProducer;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editProductsOfProducer.this);
            pDialog.setMessage("Loading product of producer details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... parameters) {
            // updating UI from Background Thread
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idprodotto", pid));
                params.add(new BasicNameValuePair("idproduttore", pidEr));

                // getting product details by making HTTP request
                // Note that product details url will use GET request
                JSONObject json = jsonParser.makeHttpRequest(url_product_of_producer_details, "GET", params);

                // check your log for json response
                //Log.d("Single Product of Producer Details", json.toString());
                Log.d("Single Product of Producer Details", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                    JSONArray productObj = json.getJSONArray(TAG_PRODMN); // JSON Array

                    // get first product object from JSON Array
                    //JSONObject
                    productOfProducer = productObj.getJSONObject(0);
                    // product with this pid and id producer found
                    //visualizzazione nel onPostExecute (in background va in crash)
                }else{  //todo prodotto non trovato qui non dovrebbe mai arrivare
                    // product with pid not found
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
            // dismiss the dialog once got all details
            pDialog.dismiss();

            // Edit Text and TextView
            //txtName = (TextView) findViewById(R.id.txtNameOfProduct);
            txtNameEr = (TextView) findViewById(R.id.txtNameOfProducer);
            txtInfoTag = (TextView) findViewById(R.id.txtInfoTag);
            editInfoTag = (EditText) findViewById(R.id.inputInfoTag);

            try{

                // display product data in EditText
                String tmp=txtNameEr.getText().toString();
                txtNameEr.setText(tmp+" "+productOfProducer.getString(TAG_NAMEer));
                //txtName.setText(productOfProducer.getString(TAG_NAME));
                tmp=txtInfoTag.getText().toString();
                txtInfoTag.setText(tmp+"\n"+ productOfProducer.getString(TAG_NAME).toString() +":");
                editInfoTag.setText(productOfProducer.getString(TAG_INFOTAG));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Background Async Task to  Save product Details
     * */
    class SaveProductOfProducerDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editProductsOfProducer.this);
            pDialog.setMessage("Saving product of producer...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving product
         * */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String InfoTag =editInfoTag.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_PIDer, pidEr));
            params.add(new BasicNameValuePair(TAG_INFOTAG, InfoTag));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_product_of_producer, "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update product
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
            // dismiss the dialog once product of producer (prodmn) updated
            pDialog.dismiss();
        }
    }
}
