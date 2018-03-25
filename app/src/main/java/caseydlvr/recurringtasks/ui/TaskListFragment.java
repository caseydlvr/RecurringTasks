package caseydlvr.recurringtasks.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.adapters.TaskAdapter;
import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.models.Task;

public class TaskListFragment extends Fragment {

    private List<Task> mTasks;
    private AppDatabase mDb;

    private TaskAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = AppDatabase.getAppDatabase(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task_list, container, false);
        ButterKnife.bind(this, rootView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        loadTasks();

        return rootView;
    }

    private void loadTasks() {
        LoadTask loadTask = new LoadTask();
        loadTask.execute();
    }

    private void populateTaskList() {
        Task[] temp = mTasks.toArray(new Task[mTasks.size()]);
        mAdapter = new TaskAdapter(temp);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class LoadTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean success = true;

            try {
                mTasks = mDb.taskDao().loadAll();
            } catch (Exception e) {
                success = false;
                mTasks = new ArrayList<>();
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            populateTaskList();
        }
    }
}
