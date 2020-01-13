package caseydlvr.recurringtasks.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.ButterKnife;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.ui.taglist.TagListFragment;
import caseydlvr.recurringtasks.ui.taskdetail.TaskDetailFragment;
import caseydlvr.recurringtasks.ui.tasklist.TaskListFragment;


public class TaskActivity extends AppCompatActivity {
    private static final String TAG = TaskActivity.class.getSimpleName();

    /**
     * Interface to allow Fragments to override the Activity's onBackPressed handling.
     */
    public interface BackPressedListener {
        /**
         * @return true if back event was handled, false if back still needs to be handled
         */
        boolean onBackPressed();
    }

    public interface SignOnListener {
        void onSignedIn();
        void onSignedOut();
    }

    public static final String EXTRA_TASK_ID = "TaskActivity_Task_Id";
    public static final String EXTRA_MODE = "TaskActivity_Mode";
    public static final String MODE_TASK_LIST = "task_list";
    public static final String MODE_TASK_DETAIL = "task_detail";
    public static final String MODE_TAG_LIST = "tag_list";
    private static final String BACK_STACK_NAME_TASK_LIST_ALL = "task_list_all";
    private static final String TAG_TASK_LIST_ALL = "task_list_all";
    private static final String TAG_TASK_LIST_FILTER = "task_list_filter";
    private static final String TAG_TAG_LIST = "tag_list";
    private static final int RC_SIGN_IN = 1;

    private List<BackPressedListener> mBackPressedListeners = new ArrayList<>();
    private List<SignOnListener> mSignOnListeners = new ArrayList<>();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private List<AuthUI.IdpConfig> mAuthProviders = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);

        initAuth();

        if (savedInstanceState != null) return;

        parseIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        // if any listener indicates it has handled the back event, skip default back handling
        for (BackPressedListener listener : mBackPressedListeners) {
            if (listener.onBackPressed()) return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() < 2) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else {
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Sign in cancelled!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "Sign-in error: " + response.getError(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void registerBackPressedListener(BackPressedListener listener) {
        mBackPressedListeners.add(listener);
    }

    public void unregisterBackPressedListener(BackPressedListener listener) {
        mBackPressedListeners.remove(listener);
    }

    public void registerSignOnListener(SignOnListener listener) {
        mSignOnListeners.add(listener);
    }

    public void unregisterSignOnListener(SignOnListener listener) {
        mSignOnListeners.remove(listener);
    }

    private void parseIntent() {
        String mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) mode = MODE_TASK_LIST;

        switch (mode) {
            case MODE_TASK_LIST:
                showTaskListFragment();
                break;
            case MODE_TASK_DETAIL:
                showTaskDetailFragment(getTaskIdExtra());
                break;
            case MODE_TAG_LIST:
                showTagListForTask(getTaskIdExtra());
                break;
            default:
                showTaskListFragment();
        }
    }

    public void showTaskListFragment() {
        getSupportFragmentManager().popBackStackImmediate(BACK_STACK_NAME_TASK_LIST_ALL,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_TASK_LIST_ALL);

        if (fragment == null) {
            fragment = new TaskListFragment();
        }

        showFragment(fragment, TAG_TASK_LIST_ALL, BACK_STACK_NAME_TASK_LIST_ALL);
    }

    public void showTaskListFragmentWithTagFilter(Tag tag) {
        Bundle args = new Bundle();
        args.putString(TaskListFragment.KEY_TAG_ID, tag.getId());
        args.putString(TaskListFragment.KEY_TAG_NAME, tag.getName());

        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);

        showFragment(fragment, TAG_TASK_LIST_FILTER, null);
    }

    public void showTaskDetailFragment(String taskId) {
        Bundle args = new Bundle();
        args.putString(TaskDetailFragment.KEY_TASK_ID, taskId);

        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(args);

        showFragment(fragment);
    }

    public void showTagListFragment() {
        Bundle args = new Bundle();
        args.putString(TagListFragment.KEY_MODE, TagListFragment.MODE_EDIT);

        TagListFragment fragment = new TagListFragment();
        fragment.setArguments(args);

        showFragment(fragment, TAG_TAG_LIST, null);
    }

    public void showTagListForTask(String taskId) {
        Bundle args = new Bundle();
        args.putString(TagListFragment.KEY_MODE, TagListFragment.MODE_TASK);
        args.putString(TagListFragment.KEY_TASK_ID, taskId);

        TagListFragment fragment = new TagListFragment();
        fragment.setArguments(args);

        showFragment(fragment);
    }

    private void showFragment(Fragment fragment, String fragmentTag, String backStackName) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, fragmentTag);
        transaction.addToBackStack(backStackName);
        transaction.commit();
    }

    private void showFragment(Fragment fragment) {
        showFragment(fragment, null, null);
    }

    private String getTaskIdExtra() {
        return getIntent().getStringExtra(EXTRA_TASK_ID);
    }

    private void initAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                for (SignOnListener listener : mSignOnListeners) {
                    listener.onSignedIn();
                }
            } else {
                for (SignOnListener listener : mSignOnListeners) {
                    listener.onSignedOut();
                }
                startAuthFlow();
            }
        };
    }

    private void startAuthFlow() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(mAuthProviders)
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN);
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
