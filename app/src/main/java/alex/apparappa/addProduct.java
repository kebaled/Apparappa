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
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 06/10/14.
 */
public class addProduct extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;
    private Button btnAddProduct;
    private Button btnCancel;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    EditText inputPrice;
    EditText inputDesc;

    // url to create new product
    private static String url_create_product = "http://www.apparappa.altervista.org/appaServices/create_product.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Edit Text
        inputName = (EditText) findViewById(R.id.inputName);
        inputPrice = (EditText) findViewById(R.id.inputPrice);
        inputDesc = (EditText) findViewById(R.id.inputDesc);

        // Create button
        btnAddProduct = (Button) findViewById(R.id.btnAddProduct);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        // button click event
        btnAddProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                String name = inputName.getText().toString();
                if(name.isEmpty()) {
                    Toast msg = Toast.makeText(getApplicationContext(), "inserire almeno il nome", Toast.LENGTH_SHORT);
                    msg.show();
                } else
                new CreateNewProduct().execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {finish();}
        });
    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(addProduct.this);//ok era NewProductActivity??
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String name, price, description;
            List<NameValuePair> params;

            name = inputName.getText().toString();
            price = inputPrice.getText().toString();
            description = inputDesc.getText().toString();

            // Building Parameters
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nome", name));
            params.add(new BasicNameValuePair("prezzo", price));
            params.add(new BasicNameValuePair("descrizione", description));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_product, "POST", params);


            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), allProducts.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                 // failed to create product
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
}