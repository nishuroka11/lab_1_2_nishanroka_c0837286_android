package com.android.productlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.productlist.Database.Product;
import com.android.productlist.Database.ProductDatabase;

public class ViewEntryActivity extends AppCompatActivity {

    TextView tv_prodEdit, tv_productId, tv_productName, tv_productDescription, tv_productPrice, tv_provLoc;

    ProductDatabase pdtdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        String product_name = getIntent().getStringExtra("product_name");
        int uid = getIntent().getIntExtra("uid",0);
        getSupportActionBar().setTitle(product_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_prodEdit = findViewById(R.id.tv_prodedit);
        tv_productId = findViewById(R.id.tv_productid);
        tv_productName = findViewById(R.id.tv_productname);
        tv_productDescription = findViewById(R.id.tv_productdesc);
        tv_productPrice = findViewById(R.id.tv_productprice);
        tv_provLoc = findViewById(R.id.tv_provloc);

        pdtdb = Room.databaseBuilder(getApplicationContext(),ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        Product allproducts = pdtdb.productDao().loadAllByProductids(uid);

        tv_productName.setText(String.valueOf(allproducts.getProductname()));
        tv_productDescription.setText(String.valueOf(allproducts.getProductdescription()));
        tv_productPrice.setText(String.valueOf(allproducts.getProductprice()));

        tv_provLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEntryActivity.this,MapsActivity.class);
                intent.putExtra("movable","false");
                intent.putExtra("product_name",product_name);

                System.out.println("latitude"+ allproducts.getLat());
                intent.putExtra("latitude",allproducts.getLat());
                intent.putExtra("longitude",allproducts.getLng());
                startActivity(intent);
            }
        });

        tv_prodEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEntryActivity.this,ProductEntryActivity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("product_name",product_name);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}