package caseydlvr.recurringtasks.ui;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.DurationUnit;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.viewmodel.TaskListViewModel;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>
        implements ItemTouchSwipeHandler {

    private List<Task> mTasks;
    private TaskListViewModel mViewModel;
    private RecyclerView mRecyclerView;

    TaskAdapter() {}

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
    public int getItemCount() {
        return mTasks != null ? mTasks.size() : 0;
    }

    @Override
    public void onItemSwiped(int position) {
        final Task deletedTask = mTasks.get(position);
        final int deletedIndex = position;

        mTasks.remove(position);
        notifyItemRemoved(position);

        Snackbar.make(mRecyclerView, R.string.taskDeleteSuccess, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo, v -> {
                mTasks.add(deletedIndex, deletedTask);
                notifyItemInserted(deletedIndex);
            }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (event != DISMISS_EVENT_ACTION) mViewModel.delete(deletedTask);
                }
            }).show();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    void setTasks(List<Task> tasks) {
        if (mTasks == null) {
            mTasks = tasks;
            notifyItemRangeChanged(0, tasks.size());
        } else {
            swap(tasks);
        }

        mTasks = tasks;
    }

    void setViewModel(TaskListViewModel viewModel) {
        mViewModel = viewModel;
    }

    public void swap(List<Task> tasks) {
        final TaskListDiffCallback diffCallback = new TaskListDiffCallback(mTasks, tasks);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        mTasks = tasks;

        diffResult.dispatchUpdatesTo(this);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Task mTask;
        private Context mContext;

        @BindView(R.id.taskName) TextView mTaskName;
        @BindView(R.id.dueDateRow) TextView mDueDateRow;
        @BindView(R.id.durationRow) TextView mDurationRow;
        @BindView(R.id.dueStatus) TextView mDueStatus;

        public TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindTask(Task task) {
            List<DurationUnit> durationUnits = TaskActivity.buildDurationUnits(mContext);
            mTask = task;

            switch (mTask.getDueStatus()) {
                case 0:
                    mDueStatus.setText(R.string.overdueStatus);
                    mDueStatus.setBackgroundColor(mContext.getResources().getColor(R.color.overdueColor));
                    mDueStatus.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mDueStatus.setText(R.string.dueStatus);
                    mDueStatus.setBackgroundColor(mContext.getResources().getColor(R.color.dueColor));
                    mDueStatus.setVisibility(View.VISIBLE);
                    break;
                default:
                    mDueStatus.setVisibility(View.GONE);
                    break;
            }

            String dueDateRow = task.getDueDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

            String durationRow = mContext.getString(R.string.durationLabel) + " " + task.getDuration() + " ";

            String durationUnit = task.getDurationUnit();
            for (DurationUnit du : durationUnits) {
                if (durationUnit.equals(du.getId())) {
                    durationRow += du.getName().toLowerCase();
                    break;
                }
            }

            mTaskName.setText(task.getName());
            mDueDateRow.setText(dueDateRow);
            mDurationRow.setText(durationRow);
        }

        @Override
        @OnClick
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, TaskActivity.class);
            intent.putExtra(TaskActivity.EXTRA_TASK_ID, mTask.getId());
            context.startActivity(intent);
        }

        @OnClick(R.id.completeCheckBox)
        public void onCompleteCheckBoxClick(View v) {
            final int position = getAdapterPosition();

            mTasks.remove(position);
            notifyItemRemoved(position);
            ((CheckBox) v).setChecked(false);

            Snackbar.make(mRecyclerView, R.string.taskCompleteSuccess, Snackbar.LENGTH_SHORT)
                .setAction(R.string.undo, l -> {
                    mTasks.add(position, mTask);
                    notifyItemInserted(position);
                }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) mViewModel.complete(mTask);
                    }
                }).show();
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
