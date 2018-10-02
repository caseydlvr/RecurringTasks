package caseydlvr.recurringtasks.ui.tasklist;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.TaskWithTags;
import caseydlvr.recurringtasks.ui.TaskActivity;
import caseydlvr.recurringtasks.ui.settings.SettingsActivity;
import caseydlvr.recurringtasks.viewmodel.TaskListViewModel;

public class TaskListFragment extends Fragment
        implements TaskActivity.BackPressedListener, TaskAdapter.TagChipClickListener {
    private static final String TAG = TaskListFragment.class.getSimpleName();

    public static final String EXTRA_TAG_ID = "TaskListFragment_Tag_Id";
    public static final String EXTRA_TAG_NAME = "TaskListFragment_Tag_Name";
    public static final String KEY_TAG_ID = "TaskListFragment_Tag_id";
    public static final String KEY_TAG_NAME = "TaskListFragment_Tag_Name";

    private static final int MENU_TAG_ORDER = 2;

    private TaskAdapter mTaskAdapter;
    private TaskListViewModel mViewModel;
    private boolean mFilterMode = false;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.emptyView) TextView mEmptyView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    @BindView(R.id.navView) NavigationView mNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task_list, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TaskListViewModel.class);

        setMode();
        initActionBar();
        initRecyclerView();
        initNavDrawer();
        subscribeUi();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_task_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TaskActivity) getActivity()).registerBackPressedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((TaskActivity) getActivity()).unregisterBackPressedListener(this);
    }


    @OnClick(R.id.fab)
    public void fabClick() {
        ((TaskActivity) getActivity()).showTaskDetailFragment(0);
    }

    @Override
    public void onTagChipClick(Tag tag) {
        navigateToFilterView(tag);
    }

    /**
     * Determines if the list is in all tasks mode or tag filtered mode. First checks the ViewModel,
     * then the Fragment's arguments.
     */
    private void setMode() {
        // if ViewModel already has a filter set, use that
        if (mViewModel.getFilterTagId() > 0) {
            mFilterMode = true;
        } else {
            if (getArguments() == null) return;

            int tagId = getArguments().getInt(KEY_TAG_ID, 0);

            if (tagId > 0) {
                mFilterMode = true;
                mViewModel.setFilterTagId(tagId);
            }
        }
    }

    private void initActionBar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        if (mFilterMode) {
            actionBar.setTitle(getArguments().getString(KEY_TAG_NAME));
        } else {
            actionBar.setTitle(R.string.allTasks);
        }
    }

    private void initRecyclerView() {
        mTaskAdapter = new TaskAdapter();
        mTaskAdapter.setViewModel(mViewModel);
        mTaskAdapter.setTagChipClickListener(this);
        mRecyclerView.setAdapter(mTaskAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new SwipeToDismissCallback(mTaskAdapter));
        touchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
    }

    private void initNavDrawer() {
        if (!mFilterMode) {
            checkNavMenuItem(mNavigationView.getMenu().findItem(R.id.navAllTasks));
        }

        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
            mDrawerLayout.closeDrawer(GravityCompat.START);

            switch (menuItem.getItemId()) {
                case R.id.navAllTasks:
                    // don't need to show new fragment if All Tasks is already showing
                    if (mFilterMode) {
                        ((TaskActivity) getActivity()).showTaskListFragment();
                    }
                    return true;
                case R.id.navEditTags:
                    ((TaskActivity) getActivity()).showTagListFragment();
                    return true;
                case R.id.navSettings:
                    Intent intent = new Intent(getContext(), SettingsActivity.class);
                    startActivity(intent);
                    return true;
                default: // assumed to be a tag filter
                    if (mFilterMode) checkNavMenuItem(menuItem);
                    Tag tag = new Tag(menuItem.getIntent().getStringExtra(EXTRA_TAG_NAME));
                    tag.setId(menuItem.getIntent().getIntExtra(EXTRA_TAG_ID, 0));

                    navigateToFilterView(tag);
                    return true;
            }
        });
    }

    /**
     * Handles request to navigate to a Tag filtered Task list. If a filtered task list is already
     * showing, the current fragment is reused. Otherwise, the request to show a filtered task list
     * is passed to the Activity.
     *
     * @param tag Tag to use for filtering the Task list
     */
    private void navigateToFilterView(Tag tag) {
        // re-use this fragment if it is already in filter mode
        if (mFilterMode) {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle(tag.getName());
            mViewModel.getFilteredTasksWithTags().removeObservers(this);
            mViewModel.setFilterTagId(tag.getId());
            mViewModel.getFilteredTasksWithTags().observe(this, this::handleTasksChanged);
        } else {
            ((TaskActivity) getActivity()).showTaskListFragmentWithTagFilter(tag);
        }
    }

    /**
     * Start observing ViewModel LiveData with appropriate onChange handling
     */
    private void subscribeUi() {
        mViewModel.getAllTags().observe(this, this::handleTagsChanged);

        if (mFilterMode) {
            mViewModel.getFilteredTasksWithTags().observe(this, this::handleTasksChanged);
            mViewModel.getFilterTag().observe(this, tag -> {
                if (tag != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag.getName());
                }
            });
        } else {
            mViewModel.getAllTasksWithTags().observe(this, this::handleTasksChanged);
        }
    }

    /**
     * Handles changes (i.e. a Task is added, removed or changed) to the list of Tasks to display
     * (provided by the ViewModel).
     *
     * @param tasks new list of Tasks
     */
    private void handleTasksChanged(List<TaskWithTags> tasks) {
        if (tasks == null || tasks.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            Collections.sort(tasks, new TaskWithTags.TaskWithTagsComparator());
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
        mTaskAdapter.setTasks(tasks);
    }

    /**
     * Handles changes (i.e. a Tag is added, removed or changed) to the list of Tags to display
     * (provided by the ViewModel).
     *
     * @param tags new list of Tags
     */
    private void handleTagsChanged(List<Tag> tags) {
        mTaskAdapter.setTags(tags);
        populateNavViewTagSubMenu(tags);
    }

    /**
     * Populates the Tag section of the navigation menu with menu items built from the supplied
     * list of Tags.
     *
     * @param tags list of Tags to display in the navigation menu
     */
    private void populateNavViewTagSubMenu(List<Tag> tags) {
        SubMenu tagMenu = mNavigationView.getMenu().findItem(R.id.tagsMenu).getSubMenu();
        tagMenu.clear();
        tagMenu.add(R.id.tagsGroup, R.id.navEditTags, MENU_TAG_ORDER - 1, R.string.editTags)
                .setIcon(R.drawable.ic_edit);

        if (tags == null) return;

        for (Tag tag : tags) {
            Intent intent = new Intent()
                    .putExtra(EXTRA_TAG_ID, tag.getId())
                    .putExtra(EXTRA_TAG_NAME, tag.getName());

            MenuItem newItem = tagMenu.add(R.id.tagsGroup, Menu.NONE, MENU_TAG_ORDER, tag.getName())
                    .setIcon(R.drawable.ic_label)
                    .setIntent(intent);

            // If Tag is the current filter tag, check it
            if (mFilterMode && tag.getId() == mViewModel.getFilterTagId()) {
                checkNavMenuItem(newItem);
            }
        }
    }

    /**
     * Checks the given menuItem in the NavigationView. Ensures this menuItem is the only checked
     * menu item.
     *
     * @param menuItem NavigationView menuItem to check
     */
    private void checkNavMenuItem(MenuItem menuItem) {
        uncheckAllMenuItems(mNavigationView.getMenu());
        menuItem.setChecked(true);
    }

    private void uncheckAllMenuItems(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.hasSubMenu()) {
                uncheckAllMenuItems(menuItem.getSubMenu());
            } else {
                menuItem.setChecked(false);
            }

        }
    }
}
