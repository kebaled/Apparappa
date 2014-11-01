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
 * Created by alex on 21/10/14.
 */
public class addProducer extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    private EditText inputNome;
    private EditText inputStoria;
    private EditText inputCellulare;
    private Button btnAddProducer;
    private Button btnCancel;

    // url to create new product
    private static String url_create_producer = "http://www.apparappa.altervista.org/appaServices/producer/create_producer.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_producer);

        // Edit Text
        inputNome = (EditText) findViewById(R.id.inputNome);
        inputStoria = (EditText) findViewById(R.id.inputStoria);
        inputCellulare = (EditText) findViewById(R.id.inputCellulare);

        // Create button
        btnAddProducer = (Button) findViewById(R.id.btnAddProducer);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        // button click event
        btnAddProducer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = inputNome.getText().toString();
                if(nome.isEmpty()){
                    Toast msg = Toast.makeText(getApplicationContext(), "inserire almeno il nome", Toast.LENGTH_SHORT);
                    msg.show(); }
                else
                    // creating new product in background thread
                    new CreateNewProducer().execute();
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
     class CreateNewProducer extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(addProducer.this);//ok era NewProducerActivity??
            pDialog.setMessage("Creating Producer..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating producer
         * */
        protected String doInBackground(String... args) {
            String nome,storia,cellulare;
            List<NameValuePair> params;

            nome = inputNome.getText().toString();
            storia = inputStoria.getText().toString();
            cellulare = inputCellulare.getText().toString();

            // Building Parameters
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nome", nome));
            params.add(new BasicNameValuePair("storia", storia));
            params.add(new BasicNameValuePair("cellulare", cellulare));

            // getting JSON Object
            // Note that create producer url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_producer, "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created producer
                    Intent i = new Intent(getApplicationContext(), allProducers.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create producer
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