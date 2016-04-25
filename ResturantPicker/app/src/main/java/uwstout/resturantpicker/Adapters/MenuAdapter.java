package uwstout.resturantpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;

import uwstout.resturantpicker.Objects.Food;
import uwstout.resturantpicker.R;

/**
 * Created by caval on 4/24/2016.
 */
public class MenuAdapter extends ArrayAdapter<Food> {
    private Vector<Food> food;

    public MenuAdapter(Context context, int resource, Vector<Food> food) {
        super(context, resource);
        this.food = food;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.restaurant_list_item, null);
        }

        if(food != null){
            TextView restName = (TextView) v.findViewById(R.id.restaurant_name);
            TextView orderCount = (TextView) v.findViewById(R.id.order_count);

            if(restName != null){
                restName.setText(food.get(position).getDescription());
            }
        }

        return v;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return food.size();
    }


}
