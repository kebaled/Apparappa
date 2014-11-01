package alex.apparappa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by alex on 06/10/14.
 */
public class products  extends Activity {

    private Button btnViewProducts;
    private Button btnNewProducts;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        btnNewProducts = (Button) findViewById(R.id.btnCreateProduct);
        btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
        btnBack = (Button) findViewById(R.id.btnBack);

        btnViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), allProducts.class);
                startActivity(i);
            }
        });

        btnNewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), addProduct.class);
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
