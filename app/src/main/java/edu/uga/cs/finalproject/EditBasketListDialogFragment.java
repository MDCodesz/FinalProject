package edu.uga.cs.finalproject;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


// This is a DialogFragment to handle edits to a ListItem.
// The edits are: updates and deletions of existing ListItem.
public class EditBasketListDialogFragment extends DialogFragment {


    public static final String DEBUG_TAG = "EditBasketListDialogFragment";
    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing job lead
    public static final int DELETE = 2; // delete an existing job lead

    private EditText itemNameView;
    private EditText priceView;

    int position;     // the position of the edited ListItem on the list of job leads
    String key;
    String item;
    double price;

    // A callback listener interface to finish up the editing of a ListItem.
    // ReviewListItemActivity implements this listener interface, as it will
    // need to update the list of ListItem and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditBasketListDialogListener {
        void updateListItem(int position, ListItem listItem, int action);
    }

    public static EditBasketListDialogFragment newInstance(int position, String key, String item, double price) {
        EditBasketListDialogFragment dialog = new EditBasketListDialogFragment();

        // Supply job lead values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putString("item", item);
        args.putDouble("price", price);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        item = getArguments().getString( "item" );
        price = getArguments().getDouble( "price" );

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.add_edit_basket_list_dialog, getActivity().findViewById( R.id.root ) );

        itemNameView = layout.findViewById( R.id.editTextText );
        priceView = layout.findViewById( R.id.editTextText2 );

        // Pre-fill the edit texts with the current values for this job lead.
        // The user will be able to modify them.
        itemNameView.setText( item );

        String priceVal = String.valueOf(price);
        priceView.setText( priceVal );

        // AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity());
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Edit Shopping List" );

        // The Cancel button handler
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton( "SAVE", new SaveButtonClickListener() );

        // The Delete button handler
        builder.setNeutralButton( "DELETE", new DeleteButtonClickListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            Log.d(DEBUG_TAG, "****inside save on click");
            String itemName = itemNameView.getText().toString();
            String priceText = priceView.getText().toString();

            // Converting the String to a double
            double price = 0.0;
            if (!priceText.isEmpty()) {
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    //display an error message to the user here
                }
            }

            Log.d(DEBUG_TAG, "Before list item");

            ListItem listItem = new ListItem( itemName, price );
            listItem.setKey( key );

            Log.d(DEBUG_TAG, "After list item");

            // get the Activity's listener to add the new job lead
            EditBasketListDialogListener listener = (EditBasketListDialogFragment.EditBasketListDialogListener) getActivity();
            // add the new job lead

            Log.d(DEBUG_TAG, "After Dialog Listener, calling updateListItem");
            listener.updateListItem( position, listItem, SAVE );

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            Context context = getContext(); // Get the Context
            if (context != null) {
                // Adding item back to ListItem / the Shopping list
                ListItem listItem1 = new ListItem(item);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("itemlists");

                Log.d(DEBUG_TAG, "Pushing new items");

                myRef.push().setValue(listItem1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(DEBUG_TAG, "Successfully pushed");
                                // Show a quick confirmation
                                Toast.makeText(context, listItem1.getItemName() + " returned to Shopping List",
                                        Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(DEBUG_TAG, "failed pushed");
                                Toast.makeText(context, "Failed to return " + listItem1.getItemName() + " to Shopping list",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            ListItem listItem = new ListItem( item);
            listItem.setKey( key );

            // get the Activity's listener to add the new job lead
            EditBasketListDialogFragment.EditBasketListDialogListener listener = (EditBasketListDialogFragment.EditBasketListDialogListener) getActivity();            // add the new job lead
            listener.updateListItem( position, listItem, DELETE );
            // close the dialog
            dismiss();
        }
    }
}
