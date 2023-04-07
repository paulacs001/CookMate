package com.example.cookmate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class RecyclerCardViewAdapter extends RecyclerView.Adapter<RecyclerCardViewAdapter.MyViewHolder> {
    ArrayList<Map<String, Object>> documentList;
    Context context;

    public RecyclerCardViewAdapter(Context context, ArrayList<Map<String, Object> > cards) {
        super();
        this.context = context;
        documentList = cards;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_cart_view, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        Map<String, Object> document = documentList.get(position);
        viewHolder.cartTitle.setText(document.get("title").toString());
        viewHolder.cartItems.setText(document.get("items").toString());

    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cartTitle;
        TextView cartItemsTitle;
        TextView cartItems;
        ImageView cartImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            cartTitle = (TextView) itemView.findViewById(R.id.CartTitle);
            cartItemsTitle = (TextView) itemView.findViewById(R.id.CartItemsTitle);
            cartItems = (TextView) itemView.findViewById(R.id.CartItems);
            cartImage = (ImageView) itemView.findViewById(R.id.CartImage);
        }
    }
}
