package caseydlvr.recurringtasks.ui.taglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import caseydlvr.recurringtasks.R;

public class CreateDialogFragment extends DialogFragment {

    public interface CreateTagClickListener {
        void onCreateTagClick(String tagName);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText tagNameInput = new EditText(getContext());

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.createTag)
                .setView(tagNameInput)
                .setPositiveButton(R.string.create, (dialogInterface, i) -> {
                    ((CreateTagClickListener) getTargetFragment())
                            .onCreateTagClick(tagNameInput.getText().toString());
                    dialogInterface.dismiss();
                })
                .setNegativeButton(R.string.dialogCancel, null)
                .create();
    }
}
