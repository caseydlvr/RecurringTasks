package caseydlvr.recurringtasks.ui.taglist;

import android.app.Dialog;
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

public class CreateDialogFragment extends DialogFragment {

    public interface CreateTagClickListener {
        void onCreateTagClick(String tagName);
    }

    private TextInputLayout mTagNameLayout;
    private TextInputEditText mTagNameInput;
    private TagListViewModel mViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getTargetFragment()).get(TagListViewModel.class);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.create_tag_dialog, null);
        mTagNameLayout = view.findViewById(R.id.tagNameLayout);
        mTagNameInput = view.findViewById(R.id.tagNameInput);

        initValidation();


        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(R.string.create, null)
                .setNegativeButton(R.string.dialogCancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view1 -> {
                    if (inputsAreValid()) {
                        ((CreateTagClickListener) getTargetFragment())
                                .onCreateTagClick(mTagNameInput.getText().toString());
                        dialogInterface.dismiss();
                    } else {
                        mTagNameLayout.setError(validateTagName());
                    }
                }));

        return dialog;
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
        } else if (mViewModel.tagNameExists(input.toString())) {
            errorText = getString(R.string.tagNameExistsError);
        }

        return errorText;
    }

    private boolean inputsAreValid() {
        return validateTagName() == null;
    }
}
