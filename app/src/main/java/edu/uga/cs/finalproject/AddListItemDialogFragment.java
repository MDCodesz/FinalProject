package edu.uga.cs.finalproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

// A DialogFragment class to handle job lead additions from the job lead review activity
// It uses a DialogFragment to allow the input of a new job lead.
public class AddListItemDialogFragment extends DialogFragment {

    private EditText itemNameView;
    private EditText priceView;

    // This interface will be used to obtain the new job lead from an AlertDialog.
    // A class implementing this interface will handle the new job lead, i.e. store it
    // in Firebase and add it to the RecyclerAdapter.
    public interface AddListItemDialogListener {
        void addListItem(ListItem listItem);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the AlertDialog view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_list_item_dialog,
                getActivity().findViewById(R.id.root));

        // get the view objects in the AlertDialog
        itemNameView = layout.findViewById( R.id.editTextText );
        priceView = layout.findViewById( R.id.editTextText2 );

        // create a new AlertDialog
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set its view (inflated above).
        builder.setView(layout);

        // Set the title of the AlertDialog
        builder.setTitle( "New List Item" );
        // Provide the negative button listener
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // close the dialog
                dialog.dismiss();
            }
        });
        // Provide the positive button listener
        builder.setPositiveButton( android.R.string.ok, new AddJobLeadListener() );

        // Create the AlertDialog and show it
        return builder.create();
    }

    private class AddJobLeadListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // get the new job lead data from the user
            String itemName = itemNameView.getText().toString();
            String priceText = priceView.getText().toString();

            // Converting the String to a double
            double price = 0.0;
            if (!priceText.isEmpty()) {
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            // create a new ListItem object
            ListItem listItem = new ListItem( itemName, price );

            // get the Activity's listener to add the new job lead
            AddListItemDialogListener listener = (AddListItemDialogListener) getActivity();

            // add the new job lead
            listener.addListItem( listItem );

            // close the dialog
            dismiss();
        }
    }
}
