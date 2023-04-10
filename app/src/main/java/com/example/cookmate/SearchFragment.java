package com.example.cookmate;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.Map;

public class SearchFragment extends Fragment {

    FirebaseUser firebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        search.setLayoutManager(layoutManager);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        try {
            db.collection("recipes")
                    .whereArrayContains("search_keywords", searchText.toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            items.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> recipe = document.getData();
                                recipe.put("id", document.getId());
                                items.add(recipe);
                            }
                            if (adapter == null) {
                                adapter = new RecyclerCardViewSearchAdapter(getContext(), items);
                                search.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch search results.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getContext(), "An error occurred while searching.", Toast.LENGTH_SHORT).show();
        }
    }

}
