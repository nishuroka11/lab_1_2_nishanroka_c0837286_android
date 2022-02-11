package com.android.productlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.productlist.Adapters.productAdapter;
import com.android.productlist.Database.Product;
import com.android.productlist.Database.ProductDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    LinearLayout ll_product;
    FloatingActionButton fab_addProduct;
    SearchView sv_product;
    ConstraintLayout cl_parent;

    RecyclerView rv_product;
    productAdapter productAda;
    ProductDatabase proddb;

    List<String> product_name = new ArrayList<String>();
    List<Double> product_price = new ArrayList<Double>();
    List<Integer> uid = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        //Initializing all Views and ViewGroups
        ll_product = findViewById(R.id.ll_product);
        fab_addProduct = findViewById(R.id.fab_addproduct);
        sv_product = findViewById(R.id.sv_product);
        cl_parent = findViewById(R.id.cl_parent);

        rv_product = findViewById(R.id.rv_product);

        ll_product.setVisibility(View.GONE); //hiding product layout

        proddb = Room.databaseBuilder(getApplicationContext(), ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        populatedata();

        System.out.println(proddb.productDao().getAll());

        ll_product.setVisibility(View.VISIBLE);
        getSupportActionBar().setTitle("Products"); // setting activity's appbar title

        loadProduct();


        fab_addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListActivity.this,ProductEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ProductListActivity.this);
        alert.setTitle("Exit ?");
        alert.setMessage("Do you want to exit this Application ?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.create().show();
    }

    public void loadProduct()
    {
        // initializing room database
        proddb = Room.databaseBuilder(getApplicationContext(),ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        // getting all product names from room database
        product_name = proddb.productDao().getallproducts();

        // getting all products descriptions from room database
        product_price = proddb.productDao().getallprodprice();

        // getting all product ids from room database
        uid = proddb.productDao().getallids();

        // initializing Product Adapter
        productAda = new productAdapter(product_name,product_price,uid);

        // setting layout manager to Product recyclerview
        rv_product.setLayoutManager(layoutManager);

        // setting adapter to Product recyclerview
        rv_product.setAdapter(productAda);

        // setting search functionality to Product recyclerview
        sv_product.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productAda.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAda.getFilter().filter(newText);
                return false;
            }
        });



        //deleting product on swipe
        ItemTouchHelper.SimpleCallback itscproduct = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                TextView tv_title = viewHolder.itemView.findViewById(R.id.tv_title);
                TextView tv_subtitle = viewHolder.itemView.findViewById(R.id.tv_subtitle);
                TextView tv_prodid = viewHolder.itemView.findViewById(R.id.tv_prodid);
                int produid = Integer.parseInt(tv_prodid.getText().toString());
                String prodname = tv_title.getText().toString();
                Double prodprice = Double.valueOf(tv_subtitle.getText().toString().replace("Price : $",""));

                Product product = proddb.productDao().loadAllByProductids(produid);

                proddb.productDao().deleteproductbyuid(produid);

                product_name.remove(prodname);
                product_price.remove(prodprice);
                uid.remove(Integer.valueOf(produid));

                final int index = viewHolder.getAdapterPosition();

                productAda.notifyItemRemoved(index);

                Snackbar.make(cl_parent,"Product Deleted: "+prodname,5000).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        proddb.productDao().insertProduct(product);
                        product_name.add(index,prodname);
                        product_price.add(index,prodprice);
                        uid.add(index,produid);

                        productAda.notifyItemInserted(index);

                    }
                }).show();

            }
        };

        ItemTouchHelper ithprod = new ItemTouchHelper(itscproduct);
        ithprod.attachToRecyclerView(rv_product); // attaching itemtouchhelper to product recyclerview
    }

    public void populatedata() {

        SharedPreferences pref = getSharedPreferences("pref_first", MODE_PRIVATE);
        int first = pref.getInt("first", 1);

        if (first == 1) {
            List<Product> products = new ArrayList<>();

            Product product = new Product(1, "Flaxseed oil",
                    "Flaxseed oil is super good and very smooth",
                    63.0, 54.54, -123.78);
            products.add(product);

            product = new Product(2	,"Ionic Color Lock"	," Lock in week one color vibrancy for all shades, colors, and blonde tones.",
                    38.0	,45.424722,	-75.695);
            products.add(product);

            product = new Product(3	,"Butter"	,"Butter is lightweight, heavy and super heavy at the same time",
                    200.0,89.33,	-113.889);
            products.add(product);

            product = new Product(4	,"Extra-virgin olive oil"	,"Extra virgin olive oil is very good for health and this oil is also beneficial for hair",
                    302.0,54.54, -123.78);
            products.add(product);

            product = new Product(5	,"Coconut oil"	,"Coconut oil is beneficial for hair and also moisturizing for skin"	,
                    20.0,23.433, -123.78);
            products.add(product);

            product = new Product(6,	"Sesame oil"	,"Sesame oil is very good for health and heart and this oil is also beneficial for hair"	,
                    50.0,53.534444	,-113.490278);
            products.add(product);

            product = new Product(	7,	"Vegetable Shortening","Vegetable Shortening is extract of vegetables for eating or doing something else with it",
                    17.0,33.467,	-744.22);
            products.add(product);

            product = new Product(8,	"Lard (Pork Fat)"	,"Lark is a pork fat and it makes you fat. So avoid eating it unless you wanna get fat."	,
                    232.4,33.467,	-744.22);
            products.add(product);

            product = new Product(9,	"olive oil",	" Extra virgin olive oil is very good for health and this oil is also beneficial for hair."
                    ,340.0,322.43,	-99.0008);
            products.add(product);

            product = new Product(10,	"seed oil"	,"Seed oil is very seedy. Its full of seeds. actually no, its very good.",
                    110.0	,69.2323,-23.23232);
            products.add(product);

            proddb.productDao().insertProducts(products);

            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("first",0);
            editor.apply();
        }
    }
}