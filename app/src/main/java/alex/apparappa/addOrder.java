package alex.apparappa;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by alex on 21/10/14.
 */
public class addOrder extends FragmentActivity{
//todo tutto da fare solo copia incollato
    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    private EditText inputData;
    private EditText inputNote;
    private DatePicker dateDate;
    private Button btnAddSaveAndContinue;
    private Button btnCancel;

    // url to create new product
    private static String url_create_order = "http://www.apparappa.altervista.org/appaServices/orderr/create_order.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_order);

        // Edit Text
        inputNote = (EditText) findViewById(R.id.inputNote);
        //todo non so cosa dateDate = (DatePicker) findViewById(R.id.datePicker);


        // Create button
        btnAddSaveAndContinue = (Button) findViewById(R.id.btnSaveAndContinue);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        // button click event
        btnAddSaveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = inputNote.getText().toString();
                if(note.isEmpty()){
                    Toast msg = Toast.makeText(getApplicationContext(), "inserire le note su luogo e ora di consegna", Toast.LENGTH_SHORT);
                    msg.show(); }
                else
                    // creating new product in background thread
                    new CreateNewOrder().execute();
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
     class CreateNewOrder extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(addOrder.this);
            pDialog.setMessage("Creating Order..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating producer
         * */
        protected String doInBackground(String... args) {
            String note, date;
            List<NameValuePair> params;

            note = inputNote.getText().toString();
            //todo non so cosa date =new StringBuilder().append(dateDate.getMonth()).append("-").append(dateDate.getDayOfMonth()).append("-").append(dateDate.getYear()).toString();


            // Building Parameters
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("note", note));
            params.add(new BasicNameValuePair("date", date));

            // getting JSON Object
            // Note that create producer url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_order, "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created producer
                    Intent i = new Intent(getApplicationContext(), allOrders.class);
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

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePicker();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

}