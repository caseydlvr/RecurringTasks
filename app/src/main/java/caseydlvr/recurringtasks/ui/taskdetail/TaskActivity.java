package caseydlvr.recurringtasks.ui.taskdetail;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import caseydlvr.recurringtasks.R;


public class TaskActivity extends AppCompatActivity {
    private static String TAG = TaskActivity.class.getSimpleName();

    public interface BackPressedListener {
        void onBackPressed();
    }

    public static final String EXTRA_TASK_ID = "TaskActivity_Task_Id";

    private List<BackPressedListener> mBackPressedListeners = new ArrayList<>();

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if (savedInstanceState != null) return;

        showDetailFragment();
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedListeners.isEmpty()) {
            super.onBackPressed();
        } else {
            for (BackPressedListener listener : mBackPressedListeners) {
                listener.onBackPressed();
            }
        }
    }

    public void registerBackPressedListener(BackPressedListener listener) {
        mBackPressedListeners.add(listener);
    }

    public void unregisterBackPressedListener(BackPressedListener listener) {
        mBackPressedListeners.remove(listener);
    }

    private void showDetailFragment() {
        long taskId = getIntent().getLongExtra(TaskActivity.EXTRA_TASK_ID, -1);
        Bundle args = new Bundle();
        args.putLong(TaskDetailFragment.KEY_TASK_ID, taskId);

        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && imm != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }
}
