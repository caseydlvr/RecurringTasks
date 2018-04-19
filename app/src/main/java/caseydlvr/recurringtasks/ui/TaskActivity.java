package caseydlvr.recurringtasks.ui;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.DurationUnit;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.viewmodel.TaskViewModel;


public class TaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_TASK_ID = "extra_task_id";
    private static final int MAX_DURATION = 999;

    private Task mTask;
    private List<DurationUnit> mDurationUnits;
    private TaskViewModel mViewModel;

    @BindView(R.id.taskNameLayout) TextInputLayout mTaskNameLayout;
    @BindView(R.id.taskName) TextInputEditText mTaskName;
    @BindView(R.id.durationLayout) TextInputLayout mDurationLayout;
    @BindView(R.id.duration) TextInputEditText mDuration;
    @BindView(R.id.durationUnitSpinner) Spinner mDurationUnitSpinner;
    @BindView(R.id.startDate) TextView mStartDate;
    @BindView(R.id.repeats) Switch mRepeats;
    @BindView(R.id.taskViewLayout) ConstraintLayout mLayout;
    @BindView(R.id.dueDate) TextView mDueDate;
    @BindView(R.id.taskToolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_close);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mTaskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > mTaskNameLayout.getCounterMaxLength()) {
                    mTaskNameLayout.setError(getString(R.string.taskNameErrorPrefix) + " "
                            + mTaskNameLayout.getCounterMaxLength());
                } else {
                    mTaskNameLayout.setError(null);
                }
            }
        });

        mDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int duration = durationTextToInt(s);
                if (duration > MAX_DURATION) {
                    mDurationLayout.setError(getString(R.string.durationTooLargeErrorPrefix) + " "
                            + MAX_DURATION);
                } else if (duration < 1) {
                    mDurationLayout.setError(getString(R.string.durationMustBePositive));
                } else {
                    mDurationLayout.setError(null);
                }
            }
        });

        mTask = new Task();
        mDurationUnits = DurationUnit.buildList(this);
        populateSpinner();
        populateViews();

        long taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1);
        mViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        if (taskId > 0) {
            mViewModel.init(taskId);
            mViewModel.getTask().observe(this, new Observer<Task>() {
                @Override
                public void onChanged(@Nullable Task task) {
                    if (task == null) {
                        Toast.makeText(getBaseContext(), R.string.taskNotFound, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        mTask = task;
                        populateViews();
                    }
                }
            });
        } else {
            actionBar.setTitle(R.string.newTask);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveTask();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.startDate)
    public void startDateClick() {
        DialogFragment dateFragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(DatePickerDialogFragment.KEY_LOCAL_DATE, mTask.getStartDate().toString());
        dateFragment.setArguments(args);
        dateFragment.show(getFragmentManager(), "start_date_picker");
    }

    @OnItemSelected(R.id.durationUnitSpinner)
    public void durationUnitChange() {
        mTask.setDurationUnit(((DurationUnit)mDurationUnitSpinner.getSelectedItem()).getKey());
        mDueDate.setText(formatDate(mTask.getDueDate()));
    }

    @OnFocusChange(R.id.duration)
    public void durationFocus(boolean hasFocus) {
        if (!hasFocus) durationChange();
    }

    @OnEditorAction(R.id.duration)
    public boolean durationChange() {
        mTask.setDuration(durationTextToInt(mDuration.getText()));
        mDueDate.setText(formatDate(mTask.getDueDate()));

        return false;
    }

    private void saveTask() {
        mTask.setName(mTaskName.getText().toString());
        mTask.setDuration(durationTextToInt(mDuration.getText()));
        mTask.setDurationUnit(((DurationUnit)mDurationUnitSpinner.getSelectedItem()).getKey());
        mTask.setRepeats(mRepeats.isChecked());

        mViewModel.persist(mTask);
    }

    private void populateSpinner() {
        ArrayAdapter<DurationUnit> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mDurationUnits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDurationUnitSpinner.setAdapter(adapter);
    }

    private void populateViews() {
        mTaskName.setText(mTask.getName());
        if (mTask.getDuration() > 0) {
            mDuration.setText(String.valueOf(mTask.getDuration()));
        }
        mDurationUnitSpinner.setSelection(DurationUnit.getIndex(mTask.getDurationUnit()));
        mRepeats.setChecked(mTask.isRepeats());

        if (mTask.getStartDate() == null) mTask.setStartDate(LocalDate.now());
        mStartDate.setText(formatDate(mTask.getStartDate()));
        mDueDate.setText(formatDate(mTask.getDueDate()));
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

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    private int durationTextToInt(Editable s) {
        // Only have to worry about being passed an empty string
        // Non-numeric and negative input is blocked by EditText.inputType set to "number"
        return Integer.parseInt("0" + s);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateString = year + "-"
                + String.format("%02d", month + 1) + "-"
                + String.format("%02d", dayOfMonth);
        mTask.setStartDate(LocalDate.parse(dateString));
        mStartDate.setText(formatDate(mTask.getStartDate()));
        mDueDate.setText(formatDate(mTask.getDueDate()));
    }
}
