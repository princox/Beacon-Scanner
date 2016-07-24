package com.hogervries.beaconscanner.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by Mitchell de Vries.
 * Criminal Intent
 * version 2
 */

public class GeneralDialogFragment extends DialogFragment {

    private OnDialogFragmentClickListener onDialogFragmentClickListener;
    private String title, message;

    public interface OnDialogFragmentClickListener {
        void onSaveClicked();
    }

    // Create an instance of the Dialog with the input
    public GeneralDialogFragment newInstance(String title, String message, OnDialogFragmentClickListener listener) {
        GeneralDialogFragment frag = new GeneralDialogFragment();
        this.onDialogFragmentClickListener = listener;
        this.title = title;
        this.message = message;
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onDialogFragmentClickListener.onSaveClicked();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }
}
