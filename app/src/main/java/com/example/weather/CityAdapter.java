package com.example.weather;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.db.DataReader;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private final DataReader reader;
    private OnMenuItemClickListener itemMenuClickListener;

    public CityAdapter(DataReader reader) {
        this.reader = reader;
    }

    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CityAdapter.ViewHolder holder, int position) {
        holder.bind(reader.getPosition(position));
    }

    @Override
    public int getItemCount() {
        return reader.getCount();
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.itemMenuClickListener = onMenuItemClickListener;
    }

    public interface OnMenuItemClickListener {
        void onItemSelectClick(City city);

        void onItemDeleteClick(City city);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView cityList;
        private City city;

        public ViewHolder(View itemView) {
            super(itemView);
            cityList = itemView.findViewById(R.id.city_item_label);
            cityList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemMenuClickListener != null) {
                        showPopupMenu(cityList);
                    }
                }
            });
        }

        public void bind(City city) {
            this.city = city;
            cityList.setText(city.getName());
        }

        private void showPopupMenu(View view) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.context_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_edit:
                            itemMenuClickListener.onItemSelectClick(city);
                            return true;
                        case R.id.menu_delete:
                            itemMenuClickListener.onItemDeleteClick(city);
                            return true;
                    }
                    return false;
                }
            });
            popup.show();
        }
    }
}

