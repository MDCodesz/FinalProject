package edu.uga.cs.finalproject;


import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


// This is a DialogFragment to handle edits to a PurchasedItem.
// The edits are: updates and deletions of existing PurchasedItem.
public class EditPurchasedItemDialogFragment extends DialogFragment {

    public static final String DEBUG_TAG = "EditPurchasedItemDialogFragment";

    // indicate the type of an edit
    //public static final int SAVE = 1;   // update an existing job lead
    public static final int DELETE = 2; // delete an existing job lead

    //private EditText itemNameView;
    private TextView itemNameView;

    int position;     // the position of the edited PurchasedItem on the list of job leads
    String key;
    String item;

    // A callback listener interface to finish up the editing of a PurchasedItem.
    // ReviewListItemActivity implements this listener interface, as it will
    // need to update the list of PurchasedItem and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditPurchasedItemDialogListener {
        void updatePurchasedItem(int position, PurchasedItem purchasedItem, int action);
    }

    public static EditPurchasedItemDialogFragment newInstance(int position, String key, String item) {
        EditPurchasedItemDialogFragment dialog = new EditPurchasedItemDialogFragment();

        // Supply job lead values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putString("item", item);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        item = getArguments().getString( "item" );

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.edit_purchased_item_dialog, getActivity().findViewById( R.id.root ) );

        itemNameView = layout.findViewById( R.id.textView1 );

        // Pre-fill the edit texts with the current values for this job lead.
        // The user will be able to modify them.
        itemNameView.setText( item );


        // AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity());
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Delete Purchased Item" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        //builder.setPositiveButton( "SAVE", new SaveButtonClickListener() );

        // The Delete button handler
        builder.setNeutralButton( "DELETE", new DeleteButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }


    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            //Adding item back to ListItem / the Shopping list
            Context context = getContext(); // Get the Context
            if (context != null) {
                // Adding item back to ListItem / the Shopping list
                ListItem listItem = new ListItem(item);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("itemlists");

                Log.d(DEBUG_TAG, "Pushing new items");

                myRef.push().setValue(listItem)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(DEBUG_TAG, "Successfully pushed");
                                // Show a quick confirmation
                                Toast.makeText(context, listItem.getItemName() + " returned to Shopping List",
                                        Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(DEBUG_TAG, "failed pushed");
                                Toast.makeText(context, "Failed to return " + listItem.getItemName() + " to Shopping list",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }


            // Deleting the item
            PurchasedItem purchasedItem = new PurchasedItem( item);
            purchasedItem.setKey( key );

            // get the Activity's listener to add the new job lead
            EditPurchasedItemDialogFragment.EditPurchasedItemDialogListener listener = (EditPurchasedItemDialogFragment.EditPurchasedItemDialogListener) getActivity();            // add the new job lead
            listener.updatePurchasedItem( position, purchasedItem, DELETE );
            // close the dialog
            dismiss();
        }
    }
}
