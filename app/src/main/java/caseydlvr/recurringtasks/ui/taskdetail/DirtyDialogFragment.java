package caseydlvr.recurringtasks.ui.taskdetail;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import caseydlvr.recurringtasks.R;

/**
 * Confirmation dialog for navigating away from screen while there is unsaved data"
 */
public class DirtyDialogFragment extends DialogFragment {

    public static final String KEY_MESSAGE = "key_message";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(KEY_MESSAGE);

        return new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.discard, (dialog, which) -> getActivity().finish())
                .setNegativeButton(R.string.keepEditing, null)
                .create();
    }
}
