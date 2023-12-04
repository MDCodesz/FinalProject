package edu.uga.cs.finalproject;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


// This is a DialogFragment to handle edits to a PurchasedList.
// The edits are: updates and deletions of existing JobLeads.
public class EditPurchasedListDialogFragment extends DialogFragment {

    // indicate the type of an edit
    public static final int SAVE = 1;   // update an existing job lead
    public static final int DELETE = 2; // delete an existing job lead

    private TextView nameView;
    private TextView dateView;
    private EditText priceView;

    int position;     // the position of the edited PurchasedList on the list of job leads
    String key;
    String name;
    String date; 
    double price;

    // A callback listener interface to finish up the editing of a PurchasedList.
    // ReviewJobLeadsActivity implements this listener interface, as it will
    // need to update the list of JobLeads and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditPurchasedListDialogListener {
        void updatePurchasedList(int position, PurchasedList purchasedList, int action);
    }

    public static EditPurchasedListDialogFragment newInstance(int position, String key, String name, String date, double price) {
        EditPurchasedListDialogFragment dialog = new EditPurchasedListDialogFragment();

        // Supply job lead values as an argument.
        Bundle args = new Bundle();
        args.putString( "key", key );
        args.putInt( "position", position );
        args.putString("name", name);
        args.putString("date", date);
        args.putDouble("price", price);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {

        key = getArguments().getString( "key" );
        position = getArguments().getInt( "position" );
        name = getArguments().getString( "name" );
        date = getArguments().getString( "date" );
        price = getArguments().getDouble( "price" );

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate( R.layout.edit_purchased_list_dialog, getActivity().findViewById( R.id.root ) );

        nameView = layout.findViewById( R.id.textView1 );
        dateView = layout.findViewById( R.id.textView2 );
        priceView = layout.findViewById( R.id.editText1 );

        // Pre-fill the edit texts with the current values for this job lead.
        // The user will be able to modify them.
        nameView.setText( name );
        dateView.setText( date );


        String priceVal = String.valueOf(price);
        priceView.setText( priceVal );

        // AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.AlertDialogStyle );
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity());
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "Edit Purchased List" );

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
            String name = nameView.getText().toString();
            String date = nameView.getText().toString();

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


            PurchasedList purchasedList = new PurchasedList( name, date, price );
            purchasedList.setKey( key );

            // get the Activity's listener to add the new job lead
            EditPurchasedListDialogListener listener = (EditPurchasedListDialogFragment.EditPurchasedListDialogListener) getActivity();
            // add the new job lead
            listener.updatePurchasedList( position, purchasedList, SAVE );

            // close the dialog
            dismiss();
        }
    }

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick( DialogInterface dialog, int which ) {

            PurchasedList purchasedList = new PurchasedList( name, date, price);
            purchasedList.setKey( key );

            // get the Activity's listener to add the new job lead
            EditPurchasedListDialogFragment.EditPurchasedListDialogListener listener = (EditPurchasedListDialogFragment.EditPurchasedListDialogListener) getActivity();            // add the new job lead
            listener.updatePurchasedList( position, purchasedList, DELETE );
            // close the dialog
            dismiss();
        }
    }
}
