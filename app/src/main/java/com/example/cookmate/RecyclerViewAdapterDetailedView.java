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

import java.util.ArrayList;

public class RecyclerViewAdapterDetailedView extends RecyclerView.Adapter<RecyclerViewAdapterDetailedView.MyViewHolder> {
    ArrayList<String> list;
    Context context;

    public RecyclerViewAdapterDetailedView(Context context, ArrayList<String> items) {
        super();
        this.context = context;
        list = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_row_detailed_sh_list, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.title.setText(list.get(position));
        viewHolder.number.setText(position + 1 + ".");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView number;
        ImageView duplicate;
        ImageView remove;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            duplicate = (ImageView) itemView.findViewById(R.id.copy);
            remove = (ImageView) itemView.findViewById(R.id.remove);
        }
    }
}
/*

    public RecyclerViewAdapter(Context context, ArrayList<String>items){
        super(context, R.layout.list_row, items);
        this.context = context;
        list = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_row, null);

            TextView number = convertView.findViewById(R.id.number);
            number.setText(position +1 + ".");
            TextView name = convertView.findViewById(R.id.name);
            name.setText(list.get(position));

            ImageView duplicate = convertView.findViewById(R.id.copy);
            ImageView remove = convertView.findViewById(R.id.remove);

            duplicate.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    AddShoppingListActivity.addItem(list.get(position));
                }
            });
            remove.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    AddShoppingListActivity.removeItem(position);
                }
            });

        }
        return convertView;
    }
}

*/
