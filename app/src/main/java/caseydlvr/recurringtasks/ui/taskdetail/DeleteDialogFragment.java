package caseydlvr.recurringtasks.ui.taskdetail;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import caseydlvr.recurringtasks.R;

/**
 * Confirmation dialog for deleting a Task.
 */
public class DeleteDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.deleteTaskMessage)
                .setPositiveButton(R.string.delete,
                        ((dialog, which) -> ((TaskActivity) getActivity()).deleteHandler()))
                .setNegativeButton(R.string.dialogCancel, null)
                .create();
    }
}
