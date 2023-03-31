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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private FloatingActionButton btnAddRecipe;
    FirebaseUser firebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MyActivity";
    private TextView RecipeTitle, RecipeIngredients, RecipeInstructions;
    private ImageView RecipeImage;
    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.btnAddRecipe);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddRecipeActivity.class);
                startActivity(intent);
            }

        });

        Log.w(TAG, "launching home fragment.");

        RecipeTitle = view.findViewById(R.id.RecipeTitle);
        RecipeIngredients = view.findViewById(R.id.RecipeIngredients);
        RecipeInstructions = view.findViewById(R.id.RecipeInstructions);
        RecipeImage = view.findViewById(R.id.RecipeImage);


        db.collection("recipes")
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RecipeTitle.setText(document.getId());
                                RecipeIngredients.setText(document.get("ingredients").toString());
                                RecipeInstructions.setText(document.get("instructions").toString());
                                Picasso.get().load(document.get("image").toString()).into(RecipeImage);
                                Log.d(TAG, "hf: " + document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return view;
    }


}