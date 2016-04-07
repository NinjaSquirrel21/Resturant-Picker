package uwstout.resturantpicker.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uwstout.resturantpicker.Objects.Restaurant;
import uwstout.resturantpicker.R;

/**
 * Created by caval on 4/4/2016.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>{

        private List<Restaurant> restaurants;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            protected TextView titleText;
            protected TextView contentText;
            protected ImageView foodPic;

            public ViewHolder(View itemView) {
                super(itemView);
                titleText = (TextView) itemView.findViewById(R.id.restaurant_title);
                contentText = (TextView) itemView.findViewById(R.id.data);
                foodPic = (ImageView) itemView.findViewById(R.id.foodImage);
              //  card = (CardView) itemView;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public RestaurantAdapter(List<Restaurant> myResturants) {
            restaurants = myResturants;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RestaurantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_cards, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Restaurant restaurant = restaurants.get(position);
            holder.titleText.setText(restaurant.getName());
            holder.contentText.setText(restaurant.getdistance());
            holder.foodPic.setImageResource(R.drawable.burger);

         //   holder.card.setCardBackgroundColor(restaurant.getcost());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return restaurants.size();
        }
}
