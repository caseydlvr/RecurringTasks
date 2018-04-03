package caseydlvr.recurringtasks.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.models.DurationUnit;
import caseydlvr.recurringtasks.models.Task;


public class TaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private boolean mIsNew = false;
    private AppDatabase mDb;
    private Task mTask;
    private List<DurationUnit> mDurationUnits;

    @BindView(R.id.taskName) EditText mTaskName;
    @BindView(R.id.duration) EditText mDuration;
    @BindView(R.id.durationUnitSpinner) Spinner mDurationUnitSpinner;
    @BindView(R.id.startDate) TextView mStartDate;
    @BindView(R.id.repeats) Switch mRepeats;
    @BindView(R.id.taskViewLayout) ConstraintLayout mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        mDb = AppDatabase.getAppDatabase(this);
        ButterKnife.bind(this);
        mDurationUnits = buildDurationUnits(this);
        populateSpinner();
        int taskId = getIntent().getIntExtra(EXTRA_TASK_ID, -1);

        if (taskId > 0) loadTask(taskId);
        else {
            mIsNew = true;
            mTask = new Task();
            populateViews();
        }
    }

    @OnClick(R.id.saveButton)
    public void saveButtonClick() {
        saveTask();
        hideKeyboard();
    }

    private void saveTask() {
        mTask.setName(mTaskName.getText().toString());
        mTask.setDuration(Integer.parseInt(mDuration.getText().toString()));
        mTask.setDurationUnit(((DurationUnit)mDurationUnitSpinner.getSelectedItem()).getId());
        mTask.setRepeats(mRepeats.isChecked());

        PersistTask persistTask = new PersistTask();
        persistTask.execute(mTask);
    }

    private void populateSpinner() {
        ArrayAdapter<DurationUnit> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mDurationUnits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDurationUnitSpinner.setAdapter(adapter);
    }

    private void loadTask(int id) {
        LoadTask loadTask = new LoadTask();
        loadTask.execute(id);
    }

    private void populateViews() {
        mTaskName.setText(mTask.getName());
        if (mTask.getDuration() > 0) {
            mDuration.setText(String.valueOf(mTask.getDuration()));
        }
        mDurationUnitSpinner.setSelection(getDurationUnitsIndex(mTask.getDurationUnit()));
        mRepeats.setChecked(mTask.isRepeats());

        if (mTask.getStartDate() == null) mTask.setStartDate(LocalDate.now());
        mStartDate.setText(mTask.getStartDate()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    }

    private int getDurationUnitsIndex(String id) {
        for (int i = 0; i < mDurationUnits.size(); i++) {
            if (mDurationUnits.get(i).getId().equals(id)) return i;
        }

        return 0; // default to first position
    }

    private void showResultMessage(int stringId) {
        Snackbar.make(mLayout, stringId, Snackbar.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && imm != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static List<DurationUnit> buildDurationUnits(Context context) {
        List<DurationUnit> durationUnits = new ArrayList<>();
        durationUnits.add(new DurationUnit("day", context.getString(R.string.days)));
        durationUnits.add(new DurationUnit("week", context.getString(R.string.weeks)));
        durationUnits.add(new DurationUnit("month", context.getString(R.string.months)));
        durationUnits.add(new DurationUnit("year", context.getString(R.string.years)));

        return durationUnits;
    }

    private class LoadTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... integers) {
            boolean success = true;

            try {
                mTask = mDb.taskDao().loadById(integers[0]);
            } catch (Exception e) {
                success = false;
            }

            if (mTask == null) {
                mIsNew = true;
                mTask = new Task();
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            populateViews();
        }
    }

    private class PersistTask extends AsyncTask<Task, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Task... tasks) {
            boolean success = true;

            try {
                if (mIsNew) mDb.taskDao().insert(tasks[0]);
                else        mDb.taskDao().update(tasks[0]);

            } catch (Exception e) {
                success = false;
            }

            if (mIsNew && success) mIsNew = false;

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) showResultMessage(R.string.taskSaveSuccess);
            else         showResultMessage(R.string.taskSaveFail);
        }
    }
}
