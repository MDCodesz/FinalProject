package edu.uga.cs.finalproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all job leads.
 */
public class PurchasedItemRecyclerAdapter extends RecyclerView.Adapter<PurchasedItemRecyclerAdapter.PurchasedItemHolder> {

    public static final String DEBUG_TAG = "PurchasedItemRecyclerAdapter";

    private List<PurchasedItem> purchasedItemList;
    private Context context;
    public PurchasedItemRecyclerAdapter( List<PurchasedItem> purchasedItemList, Context context ) {
        this.purchasedItemList = purchasedItemList;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    // The adapter must have a ViewHolder class to "hold" one item to show.
    class PurchasedItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        public PurchasedItemHolder(View itemView ) {
            super(itemView);

            itemName = itemView.findViewById( R.id.itemName );
        }
    }


//    public PurchasedItemRecyclerAdapter(List<PurchasedItem> purchasedItemList) {
//        this.purchasedItemList = purchasedItemList;
//    }

    @NonNull
    @Override
    public PurchasedItemHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.purchased_item, parent, false );
        return new PurchasedItemHolder( view );
    }

    // This method fills in the values of the Views to show a PurchasedItem
    @Override
    public void onBindViewHolder( PurchasedItemHolder holder, int position ) {
        PurchasedItem purchasedItem = purchasedItemList.get( position );

        Log.d( DEBUG_TAG, "onBindViewHolder: " + purchasedItem );

        String key = purchasedItem.getKey();
        String item = purchasedItem.getItemName();

        holder.itemName.setText( purchasedItem.getItemName());

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
                EditPurchasedItemDialogFragment editItemFragment =
                        EditPurchasedItemDialogFragment.newInstance( holder.getAdapterPosition(), key, item);
                editItemFragment.show( ((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return purchasedItemList.size();
    }
}
