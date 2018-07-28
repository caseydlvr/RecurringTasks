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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.ui.TaskActivity;
import caseydlvr.recurringtasks.ui.settings.SettingsActivity;
import caseydlvr.recurringtasks.viewmodel.TaskListViewModel;

public class TaskListFragment extends Fragment {

    public static final String EXTRA_TAG_ID = "TaskListFragment_Tag_Id";
    public static final String EXTRA_TAG_NAME = "TaskListFragment_Tag_Name";
    public static final String KEY_TAG_ID = "TaskListFragment_Tag_id";
    public static final String KEY_TAG_NAME = "TaskListFragment_Tag_Name";

    private TaskAdapter mTaskAdapter;
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

        setModeFromArgs();
        initActionBar();
        initRecyclerView();
        initNavDrawer();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final TaskListViewModel viewModel =
                ViewModelProviders.of(this).get(TaskListViewModel.class);

        if (mFilterMode) {
            viewModel.setFilterTagId(getArguments().getInt(KEY_TAG_ID));
        }

        viewModel.init();
        mTaskAdapter.setViewModel(viewModel);

        subscribeUi(viewModel);
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

    @OnClick(R.id.fab)
    public void fabClick() {
        ((TaskActivity) getActivity()).showTaskDetailFragment(0);
    }

    private void setModeFromArgs() {
        if (getArguments() == null) return;

        int tagId = getArguments().getInt(KEY_TAG_ID, 0);

        if (tagId > 0) {
            mFilterMode = true;
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
        mRecyclerView.setAdapter(mTaskAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new SwipeToDismissCallback(mTaskAdapter));
        touchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
    }

    private void initNavDrawer() {
        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();

            switch (menuItem.getItemId()) {
                case R.id.navAllTasks:
                    ((TaskActivity) getActivity()).showTaskListFragment();
                    return true;
                case R.id.navEditTags:
                    ((TaskActivity) getActivity()).showTagListFragment();
                    return true;
                case R.id.navSettings:
                    Intent intent = new Intent(getContext(), SettingsActivity.class);
                    startActivity(intent);
                    return true;
                default: // assumed to be a tag filter
                    Tag tag = new Tag(menuItem.getIntent().getStringExtra(EXTRA_TAG_NAME));
                    tag.setId(menuItem.getIntent().getIntExtra(EXTRA_TAG_ID, 0));
                    ((TaskActivity) getActivity()).showTaskListFragmentWithTagFilter(tag);
                    return true;
            }
        });
    }


    /**
     * Start observing ViewModel LiveData with appropriate onChange handling
     *
     * @param viewModel ViewModel to provide data updates for the UI
     */
    private void subscribeUi(TaskListViewModel viewModel) {
        viewModel.getOutstandingTasks().observe(this, tasks -> {
            if (tasks == null || tasks.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                Collections.sort(tasks, new Task.TaskComparator());
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
            mTaskAdapter.setTasks(tasks);
        });

        viewModel.getAllTags().observe(this, tags -> {
            if (tags == null) return;

            for (Tag tag : tags) {
                Intent intent = new Intent()
                        .putExtra(EXTRA_TAG_ID, tag.getId())
                        .putExtra(EXTRA_TAG_NAME, tag.getName());

                mNavigationView.getMenu().add(tag.getName())
                        .setIcon(R.drawable.ic_label)
                        .setIntent(intent);
            }
        });
    }
}
