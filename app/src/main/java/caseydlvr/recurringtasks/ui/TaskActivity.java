package caseydlvr.recurringtasks.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

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

    private AppDatabase mDb;
    private Task mTask;

    @BindView(R.id.taskName) EditText mTaskName;
    @BindView(R.id.duration) EditText mDuration;
    @BindView(R.id.durationUnitSpinner) Spinner mDurationUnitSpinner;
    @BindView(R.id.repeats) Switch mRepeats;
    @BindView(R.id.taskViewLayout) ConstraintLayout mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        mDb = AppDatabase.getAppDatabase(this);
        ButterKnife.bind(this);
        populateSpinner();
        mTask = new Task();
    }

    @OnClick(R.id.saveButton)
    public void saveButtonClick() {
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
                buildDurationUnits());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDurationUnitSpinner.setAdapter(adapter);
    }

    private List<DurationUnit> buildDurationUnits() {
        List<DurationUnit> durationUnits = new ArrayList<>();
        durationUnits.add(new DurationUnit("hour", getString(R.string.hours)));
        durationUnits.add(new DurationUnit("day", getString(R.string.days)));
        durationUnits.add(new DurationUnit("week", getString(R.string.weeks)));
        durationUnits.add(new DurationUnit("month", getString(R.string.months)));
        durationUnits.add(new DurationUnit("year", getString(R.string.years)));

        return durationUnits;
    }

    private void showResultMessage(int stringId) {
        Snackbar.make(mLayout, stringId, Snackbar.LENGTH_SHORT).show();
    }

    private class PersistTask extends AsyncTask<Task, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Task... tasks) {
            boolean success = true;

            try {
                mDb.taskDao().insert(tasks[0]);
            } catch (Exception e) {
                success = false;
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) showResultMessage(R.string.taskSaveSuccess);
            else         showResultMessage(R.string.taskSaveFail);
        }
    }
}
