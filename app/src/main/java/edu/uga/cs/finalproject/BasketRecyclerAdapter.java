package edu.uga.cs.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show the shopping basket.
 */
public class BasketRecyclerAdapter extends RecyclerView.Adapter<BasketRecyclerAdapter.ListItemHolder> {
    public static final String DEBUG_TAG = "BasketRecyclerAdapter";

    private List<ListItem> itemList;
    private Context context;

    public BasketRecyclerAdapter( List<ListItem> itemList, Context context ) {
        this.itemList = itemList;
        this.context = context;
    }
    // The adapter must have a ViewHolder class to "hold" one item to show.
    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ListItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView price;

        public ListItemHolder(View itemView ) {
            super(itemView);

            itemName = itemView.findViewById( R.id.itemName );
            price = itemView.findViewById( R.id.price );
        }
    }
    @NonNull
    @Override
    public BasketRecyclerAdapter.ListItemHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.basket_list_item, parent, false );
        return new BasketRecyclerAdapter.ListItemHolder( view );
    }

    // This method fills in the values of the Views to show a ListItem
    @Override
    public void onBindViewHolder(BasketRecyclerAdapter.ListItemHolder holder, int position ) {
        ListItem listItem = itemList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + listItem );

        String key = listItem.getKey();
        String item = listItem.getItemName();

        double price = listItem.getPrice();
        //String price = String.valueOf(listItem.getPrice());

        holder.itemName.setText( listItem.getItemName());
        //holder.price.setText( listItem.getPrice() );
        holder.price.setText(String.valueOf(listItem.getPrice()));

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        // the job leads shown.
        // This will indicate that the user wishes to edit (modify or delete) this item.
        // We create and show an EditJobLeadDialogFragment.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d( TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                //Log.d( TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );
                EditListItemDialogFragment editItemFragment =
                        EditListItemDialogFragment.newInstance( holder.getAdapterPosition(), key, item, price);
                editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}