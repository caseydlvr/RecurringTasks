package caseydlvr.recurringtasks.ui.taskdetail;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
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
                        ((dialog, which) -> ((TaskDetailFragment) getTargetFragment()).deleteHandler()))
                .setNegativeButton(R.string.dialogCancel, null)
                .create();
    }
}
