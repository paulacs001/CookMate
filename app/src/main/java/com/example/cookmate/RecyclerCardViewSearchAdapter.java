package com.example.cookmate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class RecyclerCardViewSearchAdapter extends RecyclerView.Adapter<RecyclerCardViewSearchAdapter.MyViewHolder> {
    ArrayList<Map<String, Object>> documentList;
    Context context;

    public RecyclerCardViewSearchAdapter(Context context, ArrayList<Map<String, Object> > cards) {
        super();
        this.context = context;
        documentList = cards;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_card_view, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        Map<String, Object> document = documentList.get(position);

        viewHolder.RecipeTitle.setText(document.get("title").toString());
        viewHolder.RecipeIngredients.setText(document.get("ingredients").toString());
        viewHolder.RecipeInstructions.setText(document.get("instructions").toString());
        if (document.get("image") != null){
            String recipe_image_uri = document.get("image").toString();
            Picasso.get().load(recipe_image_uri).into(viewHolder.RecipeImage);
        }

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, RecipeViewDetailActivity.class);
                intent.putExtra("title",document.get("title").toString());
                intent.putExtra("Ingredients",document.get("ingredients").toString());
                intent.putExtra("Instructions",document.get("instructions").toString());
                if (document.get("image") != null){
                    intent.putExtra("Image",document.get("image").toString());
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView RecipeTitle;
        TextView RecipeIngredients;
        TextView RecipeInstructions;
        ImageView RecipeImage;
        LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            RecipeTitle = (TextView) itemView.findViewById(R.id.RecipeTitle);
            RecipeIngredients = (TextView) itemView.findViewById(R.id.RecipeIngredients);
            RecipeInstructions = (TextView) itemView.findViewById(R.id.RecipeInstructions);
            RecipeImage = (ImageView) itemView.findViewById(R.id.RecipeImage);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.CardViewRecipe);
        }
    }
}
