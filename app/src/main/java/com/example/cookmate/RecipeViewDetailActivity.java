package com.example.cookmate;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;


public class RecipeViewDetailActivity extends AppCompatActivity {
    TextView title;
    TextView Ingredients;
    TextView Instructions;
    ImageView Image;
    String alphaTitle;
    String alphaIngredients;
    String alphaInstructions;
    String alphaImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_view_detail);
        title = (TextView) findViewById(R.id.RecipeTitle);
        Ingredients = (TextView) findViewById(R.id.RecipeIngredients);
        Instructions = (TextView) findViewById(R.id.RecipeInstructions);
        Image = (ImageView) findViewById(R.id.RecipeImageClick);
        alphaTitle = getIntent().getStringExtra("title");
        alphaIngredients = getIntent().getStringExtra("Ingredients");
        alphaInstructions = getIntent().getStringExtra("Instructions");

        title.setText(alphaTitle);
        Ingredients.setText(alphaIngredients);
        Instructions.setText(alphaInstructions);
        if (getIntent().hasExtra("Image")) {
            alphaImageUrl = getIntent().getStringExtra("Image");
            Glide.with(this).load(alphaImageUrl).into(Image);
        } else {
            Glide.with(this).load(R.drawable.ic_recipe).into(Image);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}



