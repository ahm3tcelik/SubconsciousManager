package com.ahmetc.subconsciousmanager.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.ahmetc.subconsciousmanager.Models.Categories;
import com.ahmetc.subconsciousmanager.R;
import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesHolder> {
    private Context context;
    private Dialog dialog;
    private ArrayList<Categories> categoriesArrayList;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public CategoriesAdapter(Context context, ArrayList<Categories> categoriesArrayList, Dialog dialog) {
        this.context = context;
        this.categoriesArrayList = categoriesArrayList;
        this.dialog = dialog;
        preferences = context.getSharedPreferences("english",Context.MODE_PRIVATE);
    }
    @NonNull
    @Override
    public CategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        return new CategoriesHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull CategoriesHolder holder, int position) {
        final Categories category = categoriesArrayList.get(position);
        holder.catName.setText(category.getCategory_name());
        holder.catIcon.setImageResource(context.getResources().getIdentifier(category.getIcon_path(),
                "drawable",context.getPackageName()));
        holder.categoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = preferences.edit();
                editor.putInt("category_id",category.getCategory_id());
                editor.apply();
                dialog.dismiss();
            }
        });

    }
    @Override
    public int getItemCount() {
        return categoriesArrayList.size();
    }

    class CategoriesHolder extends RecyclerView.ViewHolder {
        private ImageView catIcon;
        private TextView catName;
        private CardView categoryLayout;
        public CategoriesHolder(@NonNull View itemView) {
            super(itemView);
            catIcon = itemView.findViewById(R.id.catIcon);
            catName = itemView.findViewById(R.id.catName);
            categoryLayout = itemView.findViewById(R.id.categoryLayout);
        }
    }
}
