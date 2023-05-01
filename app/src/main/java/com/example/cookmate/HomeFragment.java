package com.example.cookmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FloatingActionButton btnAddRecipe;
    FirebaseUser firebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private static final String TAG = "MyActivity";

    private static RecyclerView recipes;
    static ArrayList<Map<String, Object>> items;

    static RecyclerCardViewRecipeAdapter adapter;
    private ImageView RecipeImage;
    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.btnAddRecipe);
        Button discoverButton = view.findViewById(R.id.discover_button);
        Button forYouButton = view.findViewById(R.id.for_you_button);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddRecipeActivity.class);
                startActivity(intent);
            }

        });


        Log.w(TAG, "launching home fragment.");

        items = new ArrayList<>();

        recipes = view.findViewById(R.id.RecipesCardDisplay);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        forYouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.clear();
                db.collection("recipes")
                        .whereEqualTo("userId", uid)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        items.add(document.getData());
                                        Log.d(TAG, "hf: " + document.getId() + " => " + document.getData());
                                    }
                                    adapter = new RecyclerCardViewRecipeAdapter(getContext(), items);
                                    adapter.notifyDataSetChanged();
                                    recipes.setAdapter(adapter);
                                    recipes.setLayoutManager(layoutManager);
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
                adapter.notifyDataSetChanged();
            }
        });

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.clear();
                db.collection("recipes")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        items.add(document.getData());
                                        Log.d(TAG, "hf: " + document.getId() + " => " + document.getData());
                                    }
                                    adapter = new RecyclerCardViewRecipeAdapter(getContext(), items);
                                    adapter.notifyDataSetChanged();
                                    recipes.setAdapter(adapter);
                                    recipes.setLayoutManager(layoutManager);
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
                adapter.notifyDataSetChanged();
            }
        });

        db.collection("recipes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                items.add(document.getData());
                                Log.d(TAG, "hf: " + document.getId() + " => " + document.getData());
                            }
                            adapter = new RecyclerCardViewRecipeAdapter(getContext(), items);
                            adapter.notifyDataSetChanged();
                            recipes.setAdapter(adapter);
                            recipes.setLayoutManager(layoutManager);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return view;
    }
    public static void addRecipe( Map<String, Object> recipe){
        items.add(recipe);
        recipes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



}