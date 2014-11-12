package alex.apparappa;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alex on 27/10/14.
 */
public class changeProductsOfProducer extends ListActivity {

    private Button btnCancel;
    private Button btnSave;
    private Button btnDeleteFinally;
    private String pid;//pid of product
    private String pidEr;//pid of producEr
    private String nameEr;//nome of producEr


    ArrayList<prodItem> productsOfProducerList;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // url to get all products of certain producer list
    private static String url_all_products_of_producer = "http://www.apparappa.altervista.org/appaServices/productsOfProducer/get_all_products_of_producer.php";

    // url to add product of certain producer
    private static String url_create_products_of_producer = "http://www.apparappa.altervista.org/appaServices/productsOfProducer/create_product_of_producer.php";

    // url to delete product of certain producer
    private static String url_delete_product_of_producer = "http://www.apparappa.altervista.org/appaServices/productsOfProducer/delete_product_of_producer.php";

    // url to delete product of certain producer
    private static String url_delete_finally_product_of_producer = "http://www.apparappa.altervista.org/appaServices/productsOfProducer/delete_all_products_of_producer.php";

    // JSON Node names
    private static final String TAG_PIDer = "idproduttore";
    private static final String TAG_NAMEer = "nome";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTMN = "prodmn";
    private static final String TAG_PID = "idprodotto";
    private static final String TAG_NAME = "nome";
    private static final String TAG_VALUE = "value";//true (if producer produce product) or false

    // products JSONArray
    JSONArray productsOfProducer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_products_of_producer);

        // getting producer details from intent
        Intent i = getIntent();
        // getting producer id (pid) e name from intent
        pidEr = i.getStringExtra(TAG_PIDer);
        nameEr = i.getStringExtra(TAG_NAMEer);

        TextView txtName = (TextView) findViewById(R.id.txtNameOfProducer);
        txtName.setText("Produttore: "+nameEr);

        productsOfProducerList = new ArrayList<prodItem>();

        // Loading products of certain producer in Background Thread
        new LoadAllProductsOfProducer().execute();

        btnDeleteFinally = (Button) findViewById(R.id.btnDeleteFinally);
        btnDeleteFinally.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteFinallyProductsOfProducer().execute();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSaveList);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Response from Edit Products Of producer Activity
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
     * Background Async Task to Load all products of certain producer by making HTTP Request
     */
    class LoadAllProductsOfProducer extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(changeProductsOfProducer.this);
            pDialog.setMessage("Loading products of producer. Please wait...");
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
            params.add(new BasicNameValuePair("idproduttore", pidEr));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products_of_producer, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products of Producer: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products of producer found
                    // Getting Array of Products of Producer

                    productsOfProducer = json.getJSONArray(TAG_PRODUCTMN);

                    // looping through All Products
                    for (int i = 0; i < productsOfProducer.length(); i++) {
                        JSONObject c = productsOfProducer.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String value = c.getString(TAG_VALUE);

                        prodItem productItem = new prodItem(id, name, value);
                        productsOfProducerList.add(productItem);
                    }
                } else {

                    //todo
                    // no products of producer found
                    //qui non dovrei mai arrivare: bisogna prima far inserire i prodotti
                    // Launch Add New product Activity
                    //
                    //Toast msg = Toast.makeText(getApplicationContext(), "inserire prima i prodotti", Toast.LENGTH_SHORT);
                    //msg.show();
                    /* Intent i = new Intent(getApplicationContext(), addProduct.class);//
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);*/
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
            pDialog.dismiss();
            // updating UI from Background Thread

            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter dataAdapter = new MyCustomAdapter(changeProductsOfProducer.this, R.layout.list_check_item, productsOfProducerList);
                    setListAdapter(dataAdapter);
                }
            });
        }

        private class MyCustomAdapter extends ArrayAdapter<prodItem> {

            private ArrayList<prodItem> prodList;

            public MyCustomAdapter(Context context, int textViewResourceId,
                                   ArrayList<prodItem> prodList) {
                super(context, textViewResourceId, prodList);
                this.prodList = new ArrayList<prodItem>();
                this.prodList.addAll(prodList);
            }

            private class ViewProdItem {
                TextView pid;
                TextView name;
                CheckBox cbProd;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                final ViewProdItem holderProdItem;
                Log.v("ConvertView", String.valueOf(position + 1));

                if (convertView == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = vi.inflate(R.layout.list_check_item, null);

                    holderProdItem = new ViewProdItem();
                    holderProdItem.pid = (TextView) convertView.findViewById(R.id.pid);
                    holderProdItem.name = (TextView) convertView.findViewById(R.id.nameP);
                    holderProdItem.cbProd = (CheckBox) convertView.findViewById(R.id.checkP);

                    convertView.setTag(holderProdItem);

                    holderProdItem.cbProd.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            CheckBox cb = holderProdItem.cbProd;
                            prodItem productItem = prodList.get(position);
                            productItem.setSelected(cb.isChecked());
                            //serve????
                            pid = holderProdItem.pid.getText().toString();

                            if(productItem.isSelected())
                                new CreateNewProductOfProducer().execute();
                            else
                                new DeleteProductOfProducer().execute();

/*
                                Toast.makeText(getApplicationContext(),
                                        "Clicked on Checkbox: " + cb.getText() +
                                                " is " + cb.isChecked(),
                                        Toast.LENGTH_LONG).show();
*/
                        }

                        /* copia incollato todo gestire cancellazione
        qui? o tutti assieme
    // url to delete product
    private static final String url_delete_product = "http://www.apparappa.altervista.org/appaServices/delete_product.php";
.....ecc
                         */
                    });

                    holderProdItem.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //todo if è checked ....  else magari checca il check
                            prodItem productItem = prodList.get(position);
                            if(productItem.isSelected()){

                            //if(holderProdItem.cbProd.isSelected()) {
                                Intent i = new Intent(getApplicationContext(), editProductsOfProducer.class);
                                i.putExtra(TAG_PIDer, pidEr);
                                pid = holderProdItem.pid.getText().toString();
                                i.putExtra(TAG_PID, pid);

                                startActivityForResult(i, 100);//todo: gestire aggiornamento del nome per esempio dopo il ritorno
                            }
                            else
                                Toast.makeText(getApplicationContext(), "spunta il checkbox", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    holderProdItem = (ViewProdItem) convertView.getTag();
                }

                prodItem productItem = prodList.get(position);
                //holder.code.setText(" (" +  country.getCode() + ")");
                holderProdItem.pid.setText(productItem.getPid());
                holderProdItem.name.setText(productItem.getName());
                holderProdItem.cbProd.setChecked(productItem.isSelected());
                holderProdItem.name.setTag(productItem);

                return convertView;
            }
        }
    }

    /**
     * Background Async Task to Create new product of producer
     * */
    class CreateNewProductOfProducer extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(changeProductsOfProducer.this);
            pDialog.setMessage("Creating Product of Producer..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating producer
         * */
        protected String doInBackground(String... args) {

            List<NameValuePair> params;

            // Building Parameters
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("idprodotto", pid));
            params.add(new BasicNameValuePair("idproduttore", pidEr));

            // getting JSON Object
            // Note that create producer url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_create_products_of_producer, "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product of producer
                    //todo fuori in qualche modo Toast.makeText(getApplicationContext(),"prodotto inserito").show();

                } else {
                    //todo fuori in qualche modo Toast.makeText(getApplicationContext(),"prodotto già presente, adesso in vendita").show();
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
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }

    /*****************************************************************
     * Background Async Task to Delete Product
     * */

    class DeleteProductOfProducer extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(changeProductsOfProducer.this);
            pDialog.setMessage("Deleting Product...");
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
                params.add(new BasicNameValuePair("idprodotto", pid));
                params.add(new BasicNameValuePair("idproduttore", pidEr));

                // getting product details by making HTTP request
                JSONObject json = jParser.makeHttpRequest(url_delete_product_of_producer, "GET", params);

                // check your log for json response
                Log.d("Delete Product of Producer", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
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
            pDialog = new ProgressDialog(changeProductsOfProducer.this);
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
                params.add(new BasicNameValuePair("idproduttore", pidEr));

                // getting product details by making HTTP request
                JSONObject json = jParser.makeHttpRequest(url_delete_finally_product_of_producer, "GET", params);

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
            pDialog.dismiss();
        }
    }
}