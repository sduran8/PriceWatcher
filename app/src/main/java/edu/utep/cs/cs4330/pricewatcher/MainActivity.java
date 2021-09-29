package edu.utep.cs.cs4330.pricewatcher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.ContextMenu;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    static private String name;
    static private String sharedUrl = "0";
    static private String url;
    static private double curr_price;
    static private double init_price;
    static private int percentChange;
    private ItemAdapter itemAdapter;
    private ItemDatabase itemDatabase;
    private PriceFinder pf = new PriceFinder();
    private EditText input_name;
    private EditText input_url;
    private EditText product;
    private TextView current_price;
    private TextView initial_price;
    private TextView price_change;
    private EditText edit_url;

//--ONCREATE----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Gets a URL
        String action = getIntent().getAction();
        String type = getIntent().getType();
        if (Intent.ACTION_SEND.equalsIgnoreCase(action) && type != null && ("text/plain".equals(type))) {
            sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        }
        if (!sharedUrl.equals("0")) { openDialog(); }

        //Sets up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));

        //Checks connection to the Wifi
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
        boolean isWiFi = nInfo.getType() == ConnectivityManager.TYPE_WIFI;
        if(connected && isWiFi) { Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show(); }
        else { startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); }

        //Creates the ItemAdapter and Database
        itemDatabase = new ItemDatabase(this);
        itemAdapter = new ItemAdapter(this, R.layout.watched_item, itemDatabase.allItems());
        itemAdapter.setItemClickListener(item -> itemDatabase.update(item));
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(itemAdapter);
        registerForContextMenu(listView);

        //Initialize texts that will be used
        product = findViewById(R.id.addName);
        current_price = findViewById(R.id.addPrice);
        initial_price = findViewById(R.id.addInitPrice);
        price_change = findViewById(R.id.addPriceChange);
        edit_url = findViewById(R.id.addUrl);
    }

//--CONTEXT-----------------------------------------------------------------------------------------

    //Initializes the context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.item_options, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String clicked = item.toString();
        AdapterView.AdapterContextMenuInfo x = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //Opens a screen where you can view/edit the item
        if(clicked.equals("Edit")) {
            findViewById(R.id.listView).setVisibility(View.INVISIBLE);
            findViewById(R.id.include).setVisibility(View.VISIBLE);
            if(itemAdapter.getItem(x.position).getUrl().contains("bestbuy")) {
                findViewById(R.id.techImage).setVisibility(View.VISIBLE);
                findViewById(R.id.hardwareImage).setVisibility(View.INVISIBLE);
            } else {
                findViewById(R.id.techImage).setVisibility(View.INVISIBLE);
                findViewById(R.id.hardwareImage).setVisibility(View.VISIBLE);
            }
            product.setText(itemAdapter.getItem(x.position).getItem());
            current_price.setText(String.format("$%s", String.valueOf(itemAdapter.getItem(x.position).getCurrentPrice())));
            initial_price.setText(String.format("$%s", String.valueOf(itemAdapter.getItem(x.position).getInitialPrice())));
            price_change.setText(String.format("%s%%", String.valueOf(itemAdapter.getItem(x.position).getPriceChange())));
            edit_url.setText(itemAdapter.getItem(x.position).getUrl());
            findViewById(R.id.saveChanges).setOnClickListener(view -> {
                itemAdapter.getItem(x.position).setItem(product.getText().toString());
                itemAdapter.getItem(x.position).setUrl(edit_url.getText().toString());
                itemDatabase.update(itemAdapter.getItem(x.position));
                itemAdapter.notifyDataSetChanged();
                findViewById(R.id.listView).setVisibility(View.VISIBLE);
                findViewById(R.id.include).setVisibility(View.INVISIBLE);
            });
        }
        //Deletes the item
        if (clicked.equals("Delete")) {
            itemDatabase.delete(itemAdapter.getItem(x.position).getId());
            itemAdapter.remove(itemAdapter.getItem(x.position));
        }
        //Refreshes the item's current price
        if (clicked.equals("Update")) {
            double new_price = itemAdapter.getItem(x.position).getCurrentPrice();
            int price_change = pf.priceChange(new_price, itemAdapter.getItem(x.position).getInitialPrice());
            itemDatabase.update(itemAdapter.getItem(x.position));
            itemAdapter.getItem(x.position).setCurrentPrice(new_price);
            itemAdapter.getItem(x.position).setPriceChange(price_change);
            itemAdapter.notifyDataSetChanged();
        }
        //Goes to the items website
        if (clicked.equals("Website")) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(itemAdapter.getItem(x.position).getUrl()));
            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }

//--OPTIONS-----------------------------------------------------------------------------------------

    //Initializes the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String s = item.toString();
        //Allows you to add an item
        if (s.equals("Add")) { openDialog(); }
        //Clears the entire list
        if (s.equals("Clear")) {
            itemAdapter.clear();
            itemDatabase.deleteAll();
            itemAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }
//--DIALOG------------------------------------------------------------------------------------------

    private void openDialog() {
        final Dialog d = new Dialog(MainActivity.this);
        d.setContentView(R.layout.add_item);
        d.setTitle("Add");
        d.show();
        Button cancelButton = d.findViewById(R.id.cancelButton);
        Button addButton = d.findViewById(R.id.addButton);
        TextView defaultUrl = d.findViewById(R.id.addUrl);
        if (!sharedUrl.equals("0")) { defaultUrl.setText(sharedUrl); }
        pf.getInformation(sharedUrl);

        //Closes the dialog box
        cancelButton.setOnClickListener(view -> d.dismiss());
        //Adds the item to the list then closes the dialog box
        addButton.setOnClickListener(view -> {
            input_name = d.findViewById(R.id.addName);
            input_url = d.findViewById(R.id.addUrl);
            name = input_name.getText().toString();
            if (sharedUrl.equals("0")) { url = input_url.getText().toString(); }
            else { url = sharedUrl; }
            //Current price is retrieved, and the initial price is set
            curr_price = Double.parseDouble(pf.getInformation(url));
            init_price = curr_price;
            percentChange = pf.priceChange(curr_price, init_price);
            Item product = new Item(name, curr_price, init_price, percentChange, url);
            sharedUrl = "";
            itemDatabase.addItem(product);
            itemAdapter.add(product);
            itemAdapter.notifyDataSetChanged();
            d.dismiss();
        });
    }
}