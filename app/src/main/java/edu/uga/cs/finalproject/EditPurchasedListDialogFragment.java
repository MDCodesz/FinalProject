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
    public static final int SAVE = 1;   // update an existing purchased list

    private TextView nameView;
    private TextView dateView;
    private EditText priceView;

    int position;     // the position of the edited PurchasedList on the list of job leads
    String key;
    double price;

    String name;
    String date;

    // A callback listener interface to finish up the editing of a PurchasedList.
    public interface EditPurchasedListDialogListener {
        void updatePurchasedListPrice(String purchasedListKey, String name, double newPrice);
    }

    public static EditPurchasedListDialogFragment newInstance(int position, String key, String name, String date, double price) {
        EditPurchasedListDialogFragment dialog = new EditPurchasedListDialogFragment();

        // Supply job lead values as an argument.
        Bundle args = new Bundle();
        args.putString("key", key);
        args.putInt("position", position);
        args.putDouble("price", price);

        args.putString("name", name);
        args.putString("date", date);

        dialog.setArguments(args);


        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        key = getArguments().getString("key");
        position = getArguments().getInt("position");
        price = getArguments().getDouble("price");

        name = getArguments().getString( "name" );
        date = getArguments().getString( "date" );

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.edit_purchased_list_dialog, getActivity().findViewById(R.id.root));

        nameView = layout.findViewById( R.id.editTextN );
        dateView = layout.findViewById( R.id.textView2 );

        priceView = layout.findViewById(R.id.editText1);


        nameView.setText( name );
        dateView.setText( date );

        String priceVal = String.valueOf(price);
        priceView.setText(priceVal);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        builder.setTitle("Edit Purchased List");

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("SAVE", new SaveButtonClickListener());

        return builder.create();
    }

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String name = nameView.getText().toString();
            String priceText = priceView.getText().toString();

            // Converting the String to a double
            double newPrice = 0.0;
            if (!priceText.isEmpty()) {
                try {
                    newPrice = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // Display an error message to the user here
                }
            }

            // get the Activity's listener to update the purchased list price
            EditPurchasedListDialogListener listener = (EditPurchasedListDialogListener) getActivity();
            // update the purchased list price
            listener.updatePurchasedListPrice(key, name, newPrice);

            dismiss();
        }
    }
}
