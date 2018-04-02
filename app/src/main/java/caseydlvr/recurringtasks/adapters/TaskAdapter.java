package caseydlvr.recurringtasks.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.db.AppDatabase;
import caseydlvr.recurringtasks.models.DurationUnit;
import caseydlvr.recurringtasks.models.Task;
import caseydlvr.recurringtasks.ui.TaskActivity;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> mTasks;
    private RecyclerView mRecyclerView;

    public TaskAdapter(List<Task> tasks) {
        mTasks = tasks;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_item, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        holder.bindTask(mTasks.get(position));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    public void swap(List<Task> tasks) {
        final TaskListDiffCallback diffCallback = new TaskListDiffCallback(mTasks, tasks);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        mTasks.clear();
        mTasks.addAll(tasks);

        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Task mTask;
        private Context mContext;

        @BindView(R.id.taskName) TextView mTaskName;
        @BindView(R.id.dueDate) TextView mDueDate;
        @BindView(R.id.duration) TextView mDuration;
        @BindView(R.id.durationUnit) TextView mDurationUnit;

        public TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindTask(Task task) {
            List<DurationUnit> durationUnits = TaskActivity.buildDurationUnits(mContext);
            mTask = task;

            mTaskName.setText(task.getName());
            mDueDate.setText("4/18/2018");
            mDuration.setText(String.valueOf(task.getDuration()));

            String durationUnit = task.getDurationUnit();
            for (DurationUnit du : durationUnits) {
                if (durationUnit.equals(du.getId())) {
                    mDurationUnit.setText(du.getName().toLowerCase());
                    break;
                }
            }
        }

        @Override
        @OnClick
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, TaskActivity.class);
            intent.putExtra(TaskActivity.EXTRA_TASK_ID, mTask.getId());
            context.startActivity(intent);
        }

        @OnClick(R.id.completeImageView)
        public void onCompleteImageClick(View v) {
            CompleteTask completeTask = new CompleteTask();
            completeTask.execute(mTask);
        }

        private class CompleteTask extends AsyncTask<Task, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Task... tasks) {
                boolean success = true;

                AppDatabase db = AppDatabase.getAppDatabase(mContext);

                try {
                    db.beginTransaction();
                    db.taskDao().delete(tasks[0]);

                    if (tasks[0].isRepeats()) {
                        Task newTask = new Task();
                        newTask.setName(mTask.getName());
                        newTask.setDuration(mTask.getDuration());
                        newTask.setDurationUnit(mTask.getDurationUnit());
                        newTask.setRepeats(mTask.isRepeats());

                        db.taskDao().insert(newTask);
                    }

                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    success = false;
                } finally {
                    db.endTransaction();
                }

                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Snackbar.make(mRecyclerView, "Task completed!", Snackbar.LENGTH_SHORT).show();

                } else {
                    Snackbar.make(mRecyclerView, "Complete failed! Please try again", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class TaskListDiffCallback extends DiffUtil.Callback {

        private final List<Task> mOldTaskList;
        private final List<Task> mNewTaskList;

        public TaskListDiffCallback(List<Task> oldTaskList, List<Task> newTaskList) {
            mOldTaskList = oldTaskList;
            mNewTaskList = newTaskList;
        }

        @Override
        public int getOldListSize() {
            return mOldTaskList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewTaskList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldTaskList.get(oldItemPosition).getId()
                    == mNewTaskList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldTaskList.get(oldItemPosition).equals(mNewTaskList.get(newItemPosition));
        }
    }
}
