package caseydlvr.recurringtasks.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.viewmodel.TaskListViewModel;

public class TaskListFragment extends Fragment {

    private TaskAdapter mTaskAdapter;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task_list, container, false);
        ButterKnife.bind(this, rootView);

        mTaskAdapter = new TaskAdapter();
        mRecyclerView.setAdapter(mTaskAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        ColorDrawable swipeBackgroundColor = new ColorDrawable(getResources().getColor(R.color.deleteColor));
        BitmapDrawable swipeActionIcon = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_delete);
        SwipeToDismissCallback swipeToDismissCallback =
                new SwipeToDismissCallback(mTaskAdapter, swipeBackgroundColor, swipeActionIcon);

        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeToDismissCallback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final TaskListViewModel viewModel =
                ViewModelProviders.of(this).get(TaskListViewModel.class);
        mTaskAdapter.setViewModel(viewModel);

        subscribeUi(viewModel);
    }

    private void subscribeUi(TaskListViewModel viewModel) {
        viewModel.getOutstandingTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                if (tasks != null) Collections.sort(tasks, new Task.TaskComparator());
                mTaskAdapter.setTasks(tasks);
            }
        });

        viewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null) {
                    if (isLoading) ((MainActivity) getActivity()).showLoadingSpinner();
                    else           ((MainActivity) getActivity()).hideLoadingSpinner();
                }
            }
        });
    }
}
