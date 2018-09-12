package caseydlvr.recurringtasks.ui.shared;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import caseydlvr.recurringtasks.R;

/**
 * Confirmation dialog for deleting a Task.
 */
public class DeleteDialogFragment extends DialogFragment {

    /**
     * Calling (target) Fragment must implement this interface to receive a callback when the user confirms deletion
     */
    public interface DeleteDialogListener {
        void onDeleteDialogConfirmed();
    }

    public static final String KEY_MESSAGE = "key_message";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(KEY_MESSAGE);

        return new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(R.string.delete,
                        ((dialog, which) -> ((DeleteDialogListener) getTargetFragment()).onDeleteDialogConfirmed()))
                .setNegativeButton(R.string.dialogCancel, null)
                .create();
    }
}
