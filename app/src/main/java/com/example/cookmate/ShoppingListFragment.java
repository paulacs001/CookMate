package com.example.cookmate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ShoppingListFragment extends Fragment {

    private FloatingActionButton btnAddShoppingList;
    FirebaseUser firebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "MyActivity";
    private TextView CartTitle, CartItems;
    private static RecyclerView carts;

    static ArrayList<Map<String, Object>> items;
    static RecyclerCardViewAdapter adapter;

    private FirebaseAuth mAuth;

    public ShoppingListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        FloatingActionButton fab = view.findViewById(R.id.btnAddShoppingList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddShoppingListActivity.class);
                startActivity(intent);
            }
        });

        Log.w(TAG, "launching shopping cart fragment.");

        items = new ArrayList<>();

        carts = view.findViewById(R.id.shoppingCardDisplay);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        /// adapter = new RecyclerCardViewAdapter(getContext(), );
        /// carts.setAdapter(adapter);
        /// carts.setLayoutManager(layoutManager);

        /// CartTitle = view.findViewById(R.id.CartTitle);
        /// CartItems = view.findViewById(R.id.CartItems);


        db.collection("users").document(uid)
                .collection("Shopping Lists")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                items.add(document.getData());
                                Log.d(TAG, "hf: " + document.getId() + " => " + document.getData());
                            }
                            adapter = new RecyclerCardViewAdapter(getContext(), items);
                            carts.setAdapter(adapter);
                            carts.setLayoutManager(layoutManager);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return view;
    }
    public static void addCart( Map<String, Object> cart){
        items.add(cart);
        carts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}