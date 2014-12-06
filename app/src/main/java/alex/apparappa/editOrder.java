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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 21/10/14.
 */
public class editOrder extends Activity {
//todo tutto da fare da qui
    
    private EditText txtName;
    private EditText txtStoria;
    private EditText txtCellulare;
    private EditText txtCreatedAt;
    private TextView txtProducts;
    private Button btnSave;
    private Button btnDelete;
    private Button btnCancel;
    private Button btnAddProductsOfProducer;
    private Button btnDeleteFinally;
    private ImageView imgInfoDeleteFinally;

    private String pid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // products JSONArray
    JSONArray productsOfProducer = null;

    private String textListOfProducts="";

    // single producer url
    private static final String url_producer_details = "http://www.apparappa.altervista.org/appaServices/producer/get_producer_details.php";

    // url to update producer
    private static final String url_update_producer = "http://www.apparappa.altervista.org/appaServices/producer/update_producer.php";

    // url to delete producer
    private static final String url_delete_producer = "http://www.apparappa.altervista.org/appaServices/producer/delete_producer.php";

    // url to get list of all products of certain producer
    private static String url_list_of_products_of_producer = "http://www.apparappa.altervista.org/appaServices/productsOfProducer/get_list_of_products_of_producer.php";

    // url to delete product of certain producer
    private static String url_delete_finally_product_of_producer = "http://www.apparappa.altervista.org/appaServices/productsOfProducer/delete_all_products_of_producer.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCER = "produttori";
    private static final String TAG_PID = "idproduttore";
    private static final String TAG_PRODUCTMN = "prodmn";
    private static final String TAG_INVENDITA = "invendita";
    private static final String TAG_NAME = "nome";
    private static final String TAG_STORIA = "storia";
    private static final String TAG_CELLULARE = "cellulare";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        //text list of all products of producer
        txtProducts = (TextView)findViewById(R.id.productsOfProducer);
        imgInfoDeleteFinally = (ImageView)findViewById(R.id.imgInfoDeleteFinally);
        imgInfoDeleteFinally.setImageResource(R.drawable.ic_action_about);

        //add products to producer
        btnAddProductsOfProducer = (Button) findViewById(R.id.btnAddProducstOfProducer);

        // getting producer details from intent
        Intent i = getIntent();

        // getting producer id (pid) from intent
        pid = i.getStringExtra(TAG_PID);

        // Getting complete product details in background thread
        new GetProducerDetails().execute();

        // Loading list of products of certain producer in Background Thread
        new LoadListOfProductsOfProducer().execute();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                new SaveProducerDetails().execute();
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                new DeleteProducer().execute();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        btnAddProductsOfProducer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), changeProductsOfProducer.class);
                i.putExtra(TAG_PID, pid);
                i.putExtra(TAG_NAME, txtName.getText().toString());
                startActivityForResult(i, 100);
            }
        });

        btnDeleteFinally = (Button) findViewById(R.id.btnDeleteFinally);
        btnDeleteFinally.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteFinallyProductsOfProducer().execute();
            }
        });

        imgInfoDeleteFinally.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), R.string.txtInfoDeleteFinally, Toast.LENGTH_LONG).show();
            }
        });



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 100) {
            //Update List
            textListOfProducts="";
            txtProducts.setText(getString(R.string.titleViewProducts));
            new LoadListOfProductsOfProducer().execute();
        }
    }

    /**
     * Background Async Task to Get complete producer details
     */
    class GetProducerDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */

        private JSONObject producer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editOrder.this);
            pDialog.setMessage("Loading producer details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */
        protected String doInBackground(String... parameters) {
            // updating UI from Background Thread
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idproduttore", pid));

                // getting producer details by making HTTP request
                // Note that producer details url will use GET request
                JSONObject json = jsonParser.makeHttpRequest(url_producer_details, "GET", params);

                // check your log for json response
                Log.d("Single Producer Details", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                    JSONArray producerObj = json.getJSONArray(TAG_PRODUCER); // JSON Array

                    // get first producer object from JSON Array
                    //JSONObject
                    producer = producerObj.getJSONObject(0);
                    // producer with this pid found
                    //visualizzazione nel onPostExecute (in background va in crash)
                } else {
                    // producer with pid not found
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();

            // Edit Text
            txtName = (EditText) findViewById(R.id.inputNome);
            txtStoria = (EditText) findViewById(R.id.inputStoria);
            txtCellulare = (EditText) findViewById(R.id.inputCellulare);
            try {
                // display producer data in EditText
                txtName.setText(producer.getString(TAG_NAME));
                txtStoria.setText(producer.getString(TAG_STORIA));
                txtCellulare.setText(producer.getString(TAG_CELLULARE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Background Async Task to  Save producer Details
     */
    class SaveProducerDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editOrder.this);
            pDialog.setMessage("Saving producer ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving producer
         */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String nome = txtName.getText().toString();
            String storia = txtStoria.getText().toString();
            String cellulare = txtCellulare.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, nome));
            params.add(new BasicNameValuePair(TAG_STORIA, storia));
            params.add(new BasicNameValuePair(TAG_CELLULARE, cellulare));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_producer, "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about producer update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update producer
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once producer updated
            pDialog.dismiss();
        }
    }

    /**
     * **************************************************************
     * Background Async Task to Delete Producer
     */
    class DeleteProducer extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editOrder.this);
            pDialog.setMessage("Deleting Producer...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting producer
         */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idproduttore", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(url_delete_producer, "POST", params);

                // check your log for json response
                Log.d("Delete Producer", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // producer successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about producer deletion
                    setResult(100, i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once producer deleted
            pDialog.dismiss();
        }
    }

    /**
     * Background Async Task to Load all products of certain producer by making HTTP Request
     */

    class LoadListOfProductsOfProducer extends AsyncTask<String, String, String> {



        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editOrder.this);
            pDialog.setMessage("Loading List of products of producer. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("idproduttore", pid));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url_list_of_products_of_producer, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("List of all Products of Producer: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products of producer found
                    // Getting Array of Products of Producer
                    productsOfProducer = json.getJSONArray(TAG_PRODUCTMN);
                    textListOfProducts += ": ";
                    // looping through All Products
                    for (int i = 0; i < productsOfProducer.length(); i++) {
                        if(i!=0) textListOfProducts += ", ";

                        JSONObject c = productsOfProducer.getJSONObject(i);

                        // Storing each json item in variable
                        String name = c.getString(TAG_NAME);
                        String invendita = c.getString(TAG_INVENDITA);
                        if(invendita.equals("0"))  textListOfProducts += "("+name+")"; else
                        textListOfProducts += name;
                    }
                    textListOfProducts += "!";
                } else {
                    textListOfProducts += getString(R.string.titleNoProducts);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            txtProducts.setText(txtProducts.getText().toString()+textListOfProducts);
            pDialog.dismiss();
        }
    }
    /*****************************************************************
     * Background Async Task to Delete Finally Products of Producer
     * */

    class DeleteFinallyProductsOfProducer extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editOrder.this);
            pDialog.setMessage("Deleting Products of Producer...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product of producer (really product is not deleted but not for sale)
         * */

        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idproduttore", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(url_delete_finally_product_of_producer, "GET", params);

                // check your log for json response
                Log.d("Delete All Products of Producer", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    //todo serve a qualcosa ?
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
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
            // dismiss the dialog once product deleted
            textListOfProducts="";
            txtProducts.setText(getString(R.string.titleViewProducts));
            new LoadListOfProductsOfProducer().execute();
            pDialog.dismiss();
        }
    }
}