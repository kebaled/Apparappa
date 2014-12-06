package alex.apparappa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by alex on 23/11/14.
 */
public class orders extends Activity {

    private Button btnViewOrders;
    private Button btnNewOrder;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        btnNewOrder = (Button) findViewById(R.id.btnCreateOrder);
        btnViewOrders = (Button) findViewById(R.id.btnViewOrders);
        btnBack = (Button) findViewById(R.id.btnBack);

        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), allOrders.class);
                startActivity(i);
            }
        });

        btnNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), addOrder.class);
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
