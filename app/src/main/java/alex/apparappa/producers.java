package alex.apparappa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by alex on 21/10/14.
 */
public class producers extends Activity{

    private Button btnViewProducers;
    private Button btnAddProducer;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producers);

        btnAddProducer = (Button) findViewById(R.id.btnAddProducer);
        btnViewProducers = (Button) findViewById(R.id.btnViewProducers);
        btnBack = (Button) findViewById(R.id.btnBack);

        btnViewProducers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), allProducers.class);
                startActivity(i);
            }
        });

        btnAddProducer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            Intent i = new Intent(getApplicationContext(), addProducer.class);
            startActivity(i);
              }
            });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }
}
