package edu.cuhk.csci3310.easydine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddFoodDialog extends AppCompatDialogFragment {
    private EditText foodNameView;
    private EditText foodPriceView;
    private AddFoodDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Add Food Item")
                // cancel button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                // add button
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    // need to be left empty for backward compatibility of android versions
                    }
                });
        foodNameView = view.findViewById(R.id.edit_food_name);
        foodPriceView = view.findViewById(R.id.edit_food_price);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // prevent dialog from closing automatically when ok is pressed when the data is incorrect
        // check all data inputs
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean closeDialog = false;
                if(foodNameView.getText().length() > 0){
                    String foodName = foodNameView.getText().toString();
                    try {
                        Double foodPrice = Double.parseDouble(foodPriceView.getText().toString());
                        // listener listens to NewOrderDetails activity
                        // listener will pass the data to NewOrderDetails activity's applyFoodDetails method
                        listener.applyFoodDetails(foodName, foodPrice);
                        // both inputs are validated, so can close dialog
                        closeDialog = true;
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext() ,"Please enter a valid price", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext() ,"Please fill out both fields correctly", Toast.LENGTH_SHORT).show();
                }
                if(closeDialog)
                    dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
        return dialog;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // context is the NewOrderDetailsActivity
        try {
            listener = (AddFoodDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddFoodDialogListener");
        }
    }

    public interface AddFoodDialogListener{
        void applyFoodDetails(String foodName, Double foodPrice);
    }
}
