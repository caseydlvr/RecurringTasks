package caseydlvr.recurringtasks.ui.taskdetail;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import caseydlvr.recurringtasks.model.NotificationOption;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.viewmodel.TaskDetailViewModel;


public class TaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_TASK_ID = "TaskActivity_Task_Id";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

    private Task mTask;
    private TaskDetailViewModel mViewModel;
    private boolean mCreateMode = true;
    private boolean mIsTaskNameValidationON = false;

    // clean Task values
    private String mCleanTaskName;
    private int mCleanDuration;
    private String mCleanDurationUnit;
    private LocalDate mCleanStartDate;
    private boolean mCleanRepeats;
    private String mCleanNotificationOption;

    @BindView(R.id.taskNameLayout) TextInputLayout mTaskNameLayout;
    @BindView(R.id.taskName) TextInputEditText mTaskName;
    @BindView(R.id.durationLayout) TextInputLayout mDurationLayout;
    @BindView(R.id.duration) TextInputEditText mDuration;
    @BindView(R.id.durationUnit) Spinner mDurationUnit;
    @BindView(R.id.startDate) TextView mStartDate;
    @BindView(R.id.repeating) Switch mRepeating;
    @BindView(R.id.notificationOption) Spinner mNotificationOption;
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

        populateSpinner(mDurationUnit, DurationUnit.buildList(this));
        populateSpinner(mNotificationOption, NotificationOption.buildList(this));
        mTask = new Task();
        setCleanValues(mTask);

        long taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1);
        mViewModel = ViewModelProviders.of(this).get(TaskDetailViewModel.class);

        if (taskId > 0) {
            mCreateMode = false;
            // don't automatically show keyboard when activity launches for existing tasks
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            mViewModel.init(taskId);
            mViewModel.getTask().observe(this, task -> {
                if (task == null) {
                    showResultMessage(R.string.taskNotFound);
                    finish();
                } else {
                    setCleanValues(task);

                    // rely on onRestoreInstanceState to populate views to preserve unsaved user input
                    if (savedInstanceState == null) {
                        mTask = task;
                        populateViews();
                    }

                    // don't turn on validation until after views are populated from DB values
                    initValidation();
                }
            });
        } else {
            actionBar.setTitle(R.string.newTask);

            // rely on onRestoreInstanceState to populate views to preserve unsaved user input
            if (savedInstanceState == null) populateViews();

            initValidation();
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
        int menuId;

        if (mCreateMode) menuId = R.menu.menu_task_create;
        else             menuId = R.menu.menu_task_update;

        getMenuInflater().inflate(menuId, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (validateInputs()) {
                    hideKeyboard();
                    saveTask();

                    if (mCreateMode) showResultMessage(R.string.taskCreateSuccess);
                    else             showResultMessage(R.string.taskSaveSuccess);

                    finish();
                } else if (!mIsTaskNameValidationON) {
                    mTaskNameLayout.setError(validateTaskName());
                    mIsTaskNameValidationON = true;
                }
                return true;

            case R.id.action_complete:
                completeTask();
                showResultMessage(R.string.taskCompleteSuccess);
                finish();
                return true;

            case R.id.action_delete:
                showDeleteDialog();
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

    @OnItemSelected(R.id.durationUnit)
    public void durationUnitChange() {
        mTask.setDurationUnit(((DurationUnit) mDurationUnit.getSelectedItem()).getKey());
        // changing durationUnit changes the calculated dueDate
        mDueDate.setText(formatDate(mTask.getDueDate()));
    }

    @OnFocusChange(R.id.duration)
    public void durationFocus(boolean hasFocus) {
        if (!hasFocus) durationChange();
    }

    @OnEditorAction(R.id.duration)
    public boolean durationChange() {
        mTask.setDuration(getDurationInput());
        // changing duration changes the calculated dueDate
        mDueDate.setText(formatDate(mTask.getDueDate()));

        return false;
    }

    /**
     * Handles the user performing a delete action
     */
    protected void deleteHandler() {
        deleteTask();
        showResultMessage(R.string.taskDeleteSuccess);
        finish();
    }

    /**
     * Commits the current values of all user editable views to mTask, then tells the ViewModel to
     * save this Task
     */
    private void saveTask() {
        populateTaskFromInput();

        mViewModel.persist(mTask);
    }

    /**
     * Tells the ViewModel to complete this Task
     */
    private void completeTask() {
        // don't want observer's onChanged callback to fire after complete() deletes the task
        mViewModel.getTask().removeObservers(this);
        mViewModel.complete(mTask);
    }

    /**
     * Tells the ViewModel to delete this Task
     */
    private void deleteTask() {
        // don't want observer's onChanged callback to fire after complete() deletes the task
        mViewModel.getTask().removeObservers(this);
        mViewModel.delete(mTask);
    }

    /**
     * Shows a delete confirmation dialog
     */
    private void showDeleteDialog() {
        DialogFragment deleteDialog = new DeleteDialogFragment();
        deleteDialog.show(getFragmentManager(), "delete_dialog");
    }

    /**
     * Updates mTasks with values from user editable views
     */
    private void populateTaskFromInput() {
        mTask.setName(mTaskName.getText().toString());
        mTask.setDuration(getDurationInput());
        mTask.setDurationUnit(getDurationUnitInput());
        mTask.setStartDate(getStartDateInput());
        mTask.setRepeating(mRepeating.isChecked());
        mTask.setNotificationOption(getNotificationOptionInput());
    }

    /**
     * Populates the duration unit drop down with all possible duration unit choices
     */
    private void populateSpinner(Spinner spinner, List<?> items) {
        ArrayAdapter<?> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Populate views with data from mTask
     */
    private void populateViews() {
        mTaskName.setText(mTask.getName());
        if (mTask.getDuration() > 0) {
            mDuration.setText(String.valueOf(mTask.getDuration()));
        }
        mDurationUnit.setSelection(DurationUnit.getIndex(mTask.getDurationUnit()));
        mRepeating.setChecked(mTask.isRepeating());
        mNotificationOption.setSelection(NotificationOption.getIndex(mTask.getNotificationOption()));

        if (mTask.getStartDate() == null) mTask.setStartDate(LocalDate.now());
        mStartDate.setText(formatDate(mTask.getStartDate()));
        mDueDate.setText(formatDate(mTask.getDueDate()));
    }

    /**
     * Sets clean values to use when checking if there are unsaved values
     *
     * @param task Task to use to set clean values
     */
    private void setCleanValues(Task task) {
        mCleanTaskName = task.getName();
        mCleanDuration = task.getDuration();
        mCleanDurationUnit = task.getDurationUnit();
        mCleanStartDate = task.getStartDate();
        mCleanRepeats = task.isRepeating();
        mCleanNotificationOption = task.getNotificationOption();
    }

    /**
     * @return boolean indicating whether there is unsaved user input
     */
    private boolean isDirty() {
        return !mCleanTaskName.equals(mTaskName.getText().toString())
                || mCleanDuration != getDurationInput()
                || !mCleanDurationUnit.equals(getDurationUnitInput())
                || !mCleanStartDate.equals(mTask.getStartDate())
                || mCleanRepeats != mRepeating.isChecked()
                || !mCleanNotificationOption.equals(getNotificationOptionInput());
    }

    /**
     * Shows a confirmation dialog notifying the user there are unsaved changes to the Task
     */
    private void showDirtyAlert() {
        String message;

        if (mCreateMode) message = getString(R.string.areYouSureNew);
        else             message = getString(R.string.areYouSureChanges);

        DialogFragment dirtyFragment = new DirtyDialogFragment();
        Bundle args = new Bundle();
        args.putString(DirtyDialogFragment.KEY_MESSAGE, message);
        dirtyFragment.setArguments(args);
        dirtyFragment.show(getFragmentManager(), "dirty_dialog");
    }

    /**
     * Convenience method for showing a message to the user after performing an action. For example,
     * a success or failure message. Ensures all result messages are displayed in a consistent
     * manner.
     *
     * @param stringId id of String resource of the message to show
     */
    private void showResultMessage(int stringId) {
        Toast.makeText(getBaseContext(), stringId, Toast.LENGTH_SHORT).show();
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

    /**
     * Convenience method for converting a LocalDate to a String for displaying the date. Ensures
     * all dates are displayed in a consistent format.
     *
     * @param date LocalDate
     * @return     String representing date
     */
    @NonNull
    private String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    /**
     * @return String durationUnit key of the currently selected duration unit
     */
    private String getDurationUnitInput() {
        return ((DurationUnit) mDurationUnit.getSelectedItem()).getKey();
    }

    private String getNotificationOptionInput() {
        return ((NotificationOption) mNotificationOption.getSelectedItem()).getKey();
    }

    /**
     * Converts the String stored in the duration view to an int that can be stored as a Task's
     * duration
     *
     * @return int representing a duration
     */
    private int getDurationInput() {
        // Only have to worry about being passed an empty string
        // Non-numeric and negative input is blocked by EditText.inputType set to "number"
        return Integer.parseInt("0" + mDuration.getText());
    }

    /**
     * Converts String stored in the startDate view to a LocalDate that can be stored as a
     * Task's startDate
     *
     * @return startDate LocalDate
     */
    private LocalDate getStartDateInput() {
        return LocalDate.parse(mStartDate.getText(), DATE_FORMAT);
    }

    /**
     * Updates the text of the startDate and dueDate views. To do this, mTask's startDate is updated
     * so that the new dueDate can be calculated.
     *
     * @param dateString String representing a startDate that can be parsed by LocalDate.Parse()
     */
    private void updateDates(String dateString) {
        mTask.setStartDate(LocalDate.parse(dateString));
        mStartDate.setText(formatDate(mTask.getStartDate()));
        mDueDate.setText(formatDate(mTask.getDueDate()));
    }

    /**
     * Initializes input validation
     */
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
                mIsTaskNameValidationON = true;
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

    /**
     * @return String error message if taskName is invalid, otherwise null
     */
    @Nullable
    private String validateTaskName() {
        Editable input = mTaskName.getText();
        String errorText = null;

        if (input.length() > mTaskNameLayout.getCounterMaxLength()) {
            errorText = taskNameTooLongMessage();
        } else if (input.length() < 1
                || input.toString().trim().length() < 1) {
            errorText = taskNameEmptyMessage();
        }

        return errorText;
    }

    /**
     * @return String error message if duration is invalid, otherwise null
     */
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

    /**
     * Validates all inputs. Stops as soon as an invalid input is found and shows the error message
     * returned by the validation method.
     *
     * @return boolean indicating if all user inputs are valid (true) or not (false)
     */
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

    @NonNull
    private String taskNameEmptyMessage() {
        return getString(R.string.taskNameEmptyError);
    }

    @NonNull
    private String taskNameTooLongMessage() {
        return getString(R.string.taskNameTooLongErrorPrefix) + " "
                + mTaskNameLayout.getCounterMaxLength();
    }

    @NonNull
    private String durationTooSmallMessage() {
        return getString(R.string.durationTooSmallError);
    }

    @NonNull
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
