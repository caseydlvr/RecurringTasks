package caseydlvr.recurringtasks.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.os.Bundle;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeParseException;

public class DatePickerDialogFragment extends DialogFragment {

    public static final String KEY_LOCAL_DATE = "key_local_date";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String localDateString = getArguments().getString(KEY_LOCAL_DATE);
        LocalDate date;

        try {
            date = LocalDate.parse(localDateString);
        } catch (DateTimeParseException | NullPointerException e) {
            date = LocalDate.now();
        }

        int year = date.getYear();
        int month = date.getMonthValue() - 1;
        int day = date.getDayOfMonth();

        return new DatePickerDialog(getActivity(),
                (DatePickerDialog.OnDateSetListener)getActivity(),
                year, month, day);
    }
}
