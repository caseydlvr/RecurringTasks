package caseydlvr.recurringtasks.ui.taglist;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.viewmodel.TagListViewModel;

public class TagDialogFragment extends DialogFragment {
    private static final String TAG = TagDialogFragment.class.getSimpleName();

    public interface TagDialogConfirmListener {
        void onTagDialogConfirmed(Tag tag);
    }

    static final String KEY_TAG_ID = "key_tag_id";
    static final String KEY_TAG_NAME = "key_tag_name";

    private TextInputLayout mTagNameLayout;
    private TextInputEditText mTagNameInput;
    private TagListViewModel mViewModel;
    private Tag mTag;
    private boolean mCreateMode = true;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getTargetFragment()).get(TagListViewModel.class);

        setModeFromArgs();

        View view = getActivity().getLayoutInflater().inflate(R.layout.tag_dialog, null);
        String positiveButtonText = mCreateMode ? getString(R.string.create) : getString(R.string.rename);
        mTagNameLayout = view.findViewById(R.id.tagNameLayout);
        mTagNameInput = view.findViewById(R.id.tagNameInput);

        if (!mCreateMode) {
            mTagNameInput.setText(mTag.getName());
        }

        initValidation();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(positiveButtonText, null)
                .setNegativeButton(R.string.dialogCancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view1 -> {
                    if (inputsAreValid()) {
                        mTag.setName(mTagNameInput.getText().toString());

                        ((TagDialogConfirmListener) getTargetFragment()).onTagDialogConfirmed(mTag);
                        dialogInterface.dismiss();
                    } else {
                        mTagNameLayout.setError(validateTagName());
                    }
                }));

        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mViewModel.setTagPendingEdit(null);
    }

    private void setModeFromArgs() {
        mTag = mViewModel.getTagPendingEdit();

        if (mTag != null) {
            mCreateMode = false;
        } else {
            mTag = new Tag();
        }
    }

    private void initValidation() {
        mTagNameLayout.setCounterMaxLength(Tag.NAME_MAX_LENGTH);

        mTagNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mTagNameLayout.setError(validateTagName());
                mTagNameLayout.setHintEnabled(true);
            }
        });
    }

    @Nullable
    private String validateTagName() {
        Editable input = mTagNameInput.getText();
        String errorText = null;

        if (input.length() > mTagNameLayout.getCounterMaxLength()) {
            errorText = getString(R.string.tagNameTooLongErrorPrefix);
        } else if (input.toString().trim().length() < 1) {
            errorText = getString(R.string.tagNameEmptyErrorPrefix);
        } else if ((mCreateMode && mViewModel.tagNameExists(input.toString()))
                || (!mCreateMode && mViewModel.tagNameExists(input.toString(), mTag.getName()))) {
            errorText = getString(R.string.tagNameExistsError);
        }

        return errorText;
    }

    private boolean inputsAreValid() {
        return validateTagName() == null;
    }
}
