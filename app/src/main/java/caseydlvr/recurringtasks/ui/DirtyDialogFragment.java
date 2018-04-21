package caseydlvr.recurringtasks.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import caseydlvr.recurringtasks.R;

public class DirtyDialogFragment extends DialogFragment {

    public static final String KEY_MESSAGE = "key_message";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(KEY_MESSAGE);

        return new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.discard, (dialog, which) -> {
                    getActivity().finish();
                })
                .setNegativeButton(R.string.keepEditing, null)
                .create();
    }
}
