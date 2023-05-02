package com.example.cookmate;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AddShoppingListActivity extends AppCompatActivity {
    EditText etTitle, etItems;
    ImageView enter;

    static RecyclerView shoppingItems;
    static ArrayList<String> items;
    Button btnAddShoppingCart;

    static RecyclerViewAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MyActivity";

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_list);

        etTitle = findViewById(R.id.et_title);
        etItems = findViewById(R.id.et_items);
        enter = findViewById(R.id.add);
        shoppingItems = findViewById(R.id.shoppingItems);
        items = new ArrayList<>();
        btnAddShoppingCart = findViewById(R.id.btn_add_list);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();


        Handler handler = new Handler(Looper.getMainLooper());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new RecyclerViewAdapter(getApplicationContext(), items);
        shoppingItems.setAdapter(adapter);
        shoppingItems.setLayoutManager(layoutManager);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                String text = etItems.getText().toString();
                if (text == null || text.length() == 0) {
                    makeToast(getString(R.string.no_item_warning_new_sc));
                } else {
                    addItem(text);
                    etItems.setText("");
                    makeToast(getString(R.string.added_warning_new_sc) + text);
                }
            }
        });

        btnAddShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                String title = etTitle.getText().toString().trim();

                if (title.isEmpty()) {
                    etTitle.setError(getString(R.string.title_warning_new_sc));
                    etTitle.requestFocus();
                }

                if (items.isEmpty()) {
                    etItems.setError(getString(R.string.items_warning_new_sc));
                    etItems.requestFocus();
                }

                // Add recipe to database or perform any other relevant action here

                // Create a new list with a title and items
                Map<String, Object> new_list = new HashMap<>();
                new_list.put("title", title);
                new_list.put("items", items);
                ShoppingListFragment.addCart(new_list);

                db.collection("users").document(uid)
                        .collection("Shopping Lists").document(title).set(new_list)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                // Display success message
                Toast.makeText(getApplicationContext(), "Shopping List added successfully", Toast.LENGTH_SHORT).show();
                finish(); // This will close the current activity and return to the previous one

                // Clear input fields
                etTitle.setText("");
                etItems.setText("");

            }
        });
    }

    public static void removeItem(int remove){
        items.remove(remove);
        Log.d(TAG, "Shopping list items:" + items);
        shoppingItems.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    public static void addItem(String item){
        items.add(item);
        Log.d(TAG, "Shopping list items:" + items);
        shoppingItems.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    Toast t;

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }
}



