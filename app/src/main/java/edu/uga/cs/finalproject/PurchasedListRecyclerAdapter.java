package edu.uga.cs.finalproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all job leads.
 */
public class PurchasedListRecyclerAdapter extends RecyclerView.Adapter<PurchasedListRecyclerAdapter.PurchasedListHolder> {

    public static final String DEBUG_TAG = "PurchasedListRecyclerAdapter";

    private List<PurchasedList> purchasedList;
    private Context context;


    public PurchasedListRecyclerAdapter( List<PurchasedList> purchasedList, Context context ) {
        this.purchasedList = purchasedList;
        this.context = context;

    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    // The adapter must have a ViewHolder class to "hold" one item to show.
    class PurchasedListHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView date;
        TextView price;
        Button button;


        public PurchasedListHolder(View itemView ) {
            super(itemView);

            name = itemView.findViewById( R.id.name );
            date = itemView.findViewById( R.id.date );
            price = itemView.findViewById( R.id.totalPrice );
            button = itemView.findViewById( R.id.itemButton );

        }
    }


    @NonNull
    @Override
    public PurchasedListHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.purchased_list, parent, false );
        return new PurchasedListHolder( view );
    }

    // This method fills in the values of the Views to show a PurchasedList
    @Override
    public void onBindViewHolder( PurchasedListHolder holder, int position ) {
        PurchasedList purchasedListItem = purchasedList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + purchasedListItem );

        String key = purchasedListItem.getKey();
        String name = purchasedListItem.getName();
        String date = purchasedListItem.getDate();
        String dateLabel = "Date" + purchasedListItem.getDate();


        double price = purchasedListItem.getPrice();
        String priceLabel = "Price: $" + String.valueOf(purchasedListItem.getPrice());
        //String price = String.valueOf(purchasedListItem.getPrice());

        holder.name.setText( name);
        holder.date.setText( dateLabel);
        //holder.price.setText( purchasedListItem.getPrice() );
        holder.price.setText(priceLabel);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start PurchasedItemActivity
                Intent intent = new Intent(context, PurchasedItemActivity.class);

                // Pass the key of the PurchasedList to PurchasedItemActivity
                intent.putExtra("purchasedListKey", purchasedListItem.getKey());

                // Start the activity
                context.startActivity(intent);
            }
        });

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
                EditPurchasedListDialogFragment editItemFragment =
                        EditPurchasedListDialogFragment.newInstance( holder.getAdapterPosition(), key, name, date, price);
                editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return purchasedList.size();
    }
}
