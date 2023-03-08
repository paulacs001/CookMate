package com.example.cookmate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AddShoppingListActivity extends AppCompatActivity {
    private EditText etTitle, etItems;
    private ImageView enter;

    static ListView shoppingItems;
    static ArrayList<String> items;
    private Button btnAddShoppingCart;

    static ListViewAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MyActivity";


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

        shoppingItems.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                String name = items.get(i);
                makeToast(name);
            }
        });

        shoppingItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l){
                makeToast("Removed" + items.get(i));
                removeItem(i);
                return false;
            }
        });

        adapter = new ListViewAdapter(getApplicationContext(), items);
        shoppingItems.setAdapter(adapter);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etItems.getText().toString();
                if (text == null || text.length() == 0) {
                    makeToast("Enter an item");
                } else {
                    addItem(text);
                    etItems.setText("");
                    makeToast("Added " + text);
                }
            }
        });

        btnAddShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                String title = etTitle.getText().toString().trim();

                if (title.isEmpty()) {
                    etTitle.setError("Please enter a title");
                    etTitle.requestFocus();
                    return;
                }

                if (items.isEmpty()) {
                    etItems.setError("Please enter items to buy");
                    etItems.requestFocus();
                    return;
                }

                // Add recipe to database or perform any other relevant action here

                //We will want to save timestamp too
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = dateFormat.format(new Date()); // Find todays date

                // Create a new user with a first and last name
                Map<String, Object> new_list = new HashMap<>();
                new_list.put("title", title);
                new_list.put("items", items);
                new_list.put("timestamp", currentDateTime);

                db.collection("shopping_list").document(title)
                        .set(new_list)
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
        shoppingItems.setAdapter(adapter);
    }
    public static void addItem(String item){
        items.add(item);
        shoppingItems.setAdapter(adapter);
    }
    Toast t;

    private void makeToast(String s) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }
}



