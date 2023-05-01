package com.example.cookmate;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ShoppingCartViewDetailActivity extends AppCompatActivity {
    TextView title;
    static RecyclerView shoppingItems;
    static ArrayList<String> items;
    String alphaTitle, alphaItems;

    static RecyclerViewAdapterDetailedView adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_view_detail);
        title = (TextView) findViewById(R.id.CartTitle);
        shoppingItems = findViewById(R.id.CartItems);

        alphaTitle = getIntent().getStringExtra("title");
        alphaItems = getIntent().getStringExtra("items");

        title.setText(alphaTitle);
        alphaItems = alphaItems.replace("[", "").replace("]", "");
        items = new ArrayList<String>(Arrays.asList(alphaItems.split(",")));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new RecyclerViewAdapterDetailedView(getApplicationContext(), items);
        shoppingItems.setAdapter(adapter);
        shoppingItems.setLayoutManager(layoutManager);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}



