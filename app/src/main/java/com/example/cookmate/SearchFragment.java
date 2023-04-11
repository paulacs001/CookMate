package com.example.cookmate;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class SearchFragment extends Fragment {

    FirebaseUser firebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MyActivity";
    private Button searchButton;
    private SearchView searchView;
    private static RecyclerView search;
    static ArrayList<Map<String, Object>> items;
    static RecyclerCardViewSearchAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_view);
        searchButton = view.findViewById(R.id.searchbutton);

        items = new ArrayList<>();
        search = view.findViewById(R.id.RecipesCardDisplay);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SEARCH", "Button clicked");
                String searchText = searchView.getQuery().toString();
                if (searchText.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a search term.", Toast.LENGTH_SHORT).show();
                } else {
                    searchRecipes(searchText);
                }
            }
        });

        return view;
    }

    private void searchRecipes(String searchText) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        Log.d("SEARCH", "Searching for: " + searchText);
        try {
            db.collection("recipes")
                    .whereArrayContains("title", searchText)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("ENTERED", "Succesful");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    items.add(document.getData());
                                    Log.d(TAG, "hf: " + document.getId() + " => " + document.getData());
                                }
                                adapter = new RecyclerCardViewSearchAdapter(getContext(), items);
                                search.setAdapter(adapter);
                                search.setLayoutManager(layoutManager);
                            } else {
                                Toast.makeText(getContext(), "Failed to fetch search results.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        } catch (Exception e) {
            Toast.makeText(getContext(), "An error occurred while searching.", Toast.LENGTH_SHORT).show();
        }
    }

}
