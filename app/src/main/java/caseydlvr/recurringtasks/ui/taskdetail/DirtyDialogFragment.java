package caseydlvr.recurringtasks.ui.taskdetail;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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
                .setPositiveButton(R.string.discard,
                        ((dialog, which) -> ((TaskDetailFragment) getTargetFragment()).dirtyAlertHandler(true)))
                .setNegativeButton(R.string.keepEditing,
                        ((dialog, which) -> ((TaskDetailFragment) getTargetFragment()).dirtyAlertHandler(false)))
                .create();
    }
}
