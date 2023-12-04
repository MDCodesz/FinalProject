package edu.uga.cs.finalproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        public PurchasedListHolder(View itemView ) {
            super(itemView);

            name = itemView.findViewById( R.id.name );
            date = itemView.findViewById( R.id.date );
            price = itemView.findViewById( R.id.totalPrice );
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
        PurchasedList purchaseListItem = purchasedList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + purchaseListItem );

        String key = purchaseListItem.getKey();
        String name = purchaseListItem.getName();
        String date = purchaseListItem.getDate();

        double price = purchaseListItem.getPrice();
        //String price = String.valueOf(purchaseListItem.getPrice());

        holder.name.setText( purchaseListItem.getName());
        holder.date.setText( purchaseListItem.getDate());
        //holder.price.setText( purchaseListItem.getPrice() );
        holder.price.setText(String.valueOf(purchaseListItem.getPrice()));

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
