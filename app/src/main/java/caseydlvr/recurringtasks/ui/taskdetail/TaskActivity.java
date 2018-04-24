package caseydlvr.recurringtasks.ui.taskdetail;

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
import android.view.View;
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
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

    private Task mTask;
    private List<DurationUnit> mDurationUnits;
    private TaskViewModel mViewModel;

    private String mCleanTaskName;
    private int mCleanDuration;
    private String mCleanDurationUnit;
    private LocalDate mCleanStartDate;
    private boolean mCleanRepeats;
    private boolean mCleanNotifications;

    @BindView(R.id.taskNameLayout) TextInputLayout mTaskNameLayout;
    @BindView(R.id.taskName) TextInputEditText mTaskName;
    @BindView(R.id.durationLayout) TextInputLayout mDurationLayout;
    @BindView(R.id.duration) TextInputEditText mDuration;
    @BindView(R.id.durationUnitSpinner) Spinner mDurationUnitSpinner;
    @BindView(R.id.startDate) TextView mStartDate;
    @BindView(R.id.repeating) Switch mRepeating;
    @BindView(R.id.notifications) Switch mNotifications;
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

        initValidation();

        mDurationUnits = DurationUnit.buildList(this);
        populateSpinner();
        mTask = new Task();
        setCleanValues(mTask);

        // rely on onRestoreInstanceState to populate views to preserve unsaved user input
        if (savedInstanceState == null) populateViews();

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
                        setCleanValues(task);

                        // rely on onRestoreInstanceState to populate views to preserve unsaved user input
                        if (savedInstanceState == null) {
                            mTask = task;
                            populateViews();
                        }
                    }
                }
            });
        } else {
            actionBar.setTitle(R.string.newTask);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        populateTaskFromInput();
        mDueDate.setText(formatDate(mTask.getDueDate()));
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
                if (validateInputs()) {
                    hideKeyboard();
                    saveTask();
                    finish();
                }
                return true;

            case android.R.id.home:
                if (isDirty()) {
                    showDirtyAlert();
                    return true;
                } else {
                    return super.onOptionsItemSelected(item);
                }

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isDirty()) {
            showDirtyAlert();
        } else {
            super.onBackPressed();
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
        mTask.setDuration(getDurationInput());
        mDueDate.setText(formatDate(mTask.getDueDate()));

        return false;
    }

    private void saveTask() {
        populateTaskFromInput();

        mViewModel.persist(mTask);
    }

    private void populateTaskFromInput() {
        mTask.setName(mTaskName.getText().toString());
        mTask.setDuration(getDurationInput());
        mTask.setDurationUnit(getDurationUnitInput());
        mTask.setStartDate(getStartDateInput());
        mTask.setRepeating(mRepeating.isChecked());
        mTask.setUsesNotifications(mNotifications.isChecked());
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
        mRepeating.setChecked(mTask.isRepeating());
        mNotifications.setChecked(mTask.usesNotifications());

        if (mTask.getStartDate() == null) mTask.setStartDate(LocalDate.now());
        mStartDate.setText(formatDate(mTask.getStartDate()));
        mDueDate.setText(formatDate(mTask.getDueDate()));
    }

    private void setCleanValues(Task task) {
        mCleanTaskName = task.getName();
        mCleanDuration = task.getDuration();
        mCleanDurationUnit = task.getDurationUnit();
        mCleanStartDate = task.getStartDate();
        mCleanRepeats = task.isRepeating();
        mCleanNotifications = task.usesNotifications();
    }

    private boolean isDirty() {
        return !mCleanTaskName.equals(mTaskName.getText().toString())
                || mCleanDuration != getDurationInput()
                || !mCleanDurationUnit.equals(getDurationUnitInput())
                || !mCleanStartDate.equals(mTask.getStartDate())
                || mCleanRepeats != mRepeating.isChecked()
                || mCleanNotifications != mNotifications.isChecked();
    }

    private void showDirtyAlert() {
        String message;

        if (mTask.getId() == 0) message = getString(R.string.areYouSureNew);
        else                    message = getString(R.string.areYouSureChanges);

        DialogFragment dirtyFragment = new DirtyDialogFragment();
        Bundle args = new Bundle();
        args.putString(DirtyDialogFragment.KEY_MESSAGE, message);
        dirtyFragment.setArguments(args);
        dirtyFragment.show(getFragmentManager(), "dirty_dialog");
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

    private void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    private String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    private String getDurationUnitInput() {
        return ((DurationUnit)mDurationUnitSpinner.getSelectedItem()).getKey();
    }

    private int getDurationInput() {
        // Only have to worry about being passed an empty string
        // Non-numeric and negative input is blocked by EditText.inputType set to "number"
        return Integer.parseInt("0" + mDuration.getText());
    }

    private LocalDate getStartDateInput() {
        return LocalDate.parse(mStartDate.getText(), DATE_FORMAT);
    }

    private void updateDates(String dateString) {
        mTask.setStartDate(LocalDate.parse(dateString));
        mStartDate.setText(formatDate(mTask.getStartDate()));
        mDueDate.setText(formatDate(mTask.getDueDate()));
    }

    private void initValidation() {
        mTaskNameLayout.setCounterMaxLength(Task.NAME_MAX_LENGTH);

        mTaskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTaskNameLayout.setError(validateTaskName());
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
                mDurationLayout.setError(validateDuration());
            }
        });
    }

    @Nullable
    private String validateTaskName() {
        Editable input = mTaskName.getText();
        String errorText = null;

        if (input.length() > mTaskNameLayout.getCounterMaxLength()) {
            errorText = taskNameTooLongMessage();
        } else if (input.length() < 1) {
            errorText = taskNameEmptyMessage();
        }

        return errorText;
    }

    @Nullable
    private String validateDuration() {
        int duration = getDurationInput();
        String errorText = null;

        if (duration > Task.DURATION_MAX) {
            errorText = durationTooLargeMessage();
        } else if (duration < Task.DURATION_MIN) {
            errorText = durationTooSmallMessage();
        }

        return errorText;
    }

    private boolean validateInputs() {
        String errorText = validateTaskName();
        if (errorText != null) {
            Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
            mTaskName.requestFocus();
            showKeyboard(mTaskName);
            return false;
        }

        errorText = validateDuration();
        if (errorText != null) {
            Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
            mDuration.requestFocus();
            showKeyboard(mDuration);
            return false;
        }

        return true;

    }

    private String taskNameEmptyMessage() {
        return getString(R.string.taskNameEmptyError);
    }

    private String taskNameTooLongMessage() {
        return getString(R.string.taskNameTooLongErrorPrefix) + " "
                + mTaskNameLayout.getCounterMaxLength();
    }

    private String durationTooSmallMessage() {
        return getString(R.string.durationTooSmallError);
    }

    private String durationTooLargeMessage() {
        return getString(R.string.durationTooLargeErrorPrefix) + " "
                + Task.DURATION_MAX;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateString = year + "-"
                + String.format("%02d", month + 1) + "-"
                + String.format("%02d", dayOfMonth);
        updateDates(dateString);
    }
}
