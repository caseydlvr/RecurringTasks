package caseydlvr.recurringtasks.ui;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.DueStatus;
import caseydlvr.recurringtasks.model.DurationUnit;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.viewmodel.TaskListViewModel;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>
        implements ItemTouchSwipeListener {

    private List<Task> mTasks;
    private TaskListViewModel mViewModel;

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
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        final Task task = mTasks.get(position);

        if (direction == ItemTouchHelper.LEFT) {
            delete(task, position, viewHolder.itemView);
        } else {
            complete(task, position, viewHolder.itemView);
        }
    }

    public void setTasks(List<Task> tasks) {
        if (mTasks == null) {
            mTasks = tasks;
            notifyItemRangeChanged(0, tasks.size());
        } else {
            swap(tasks);
        }

        mTasks = tasks;
    }

    public void setViewModel(TaskListViewModel viewModel) {
        mViewModel = viewModel;
    }

    private void removeItem(int position) {
        mTasks.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, Task task) {
        mTasks.add(position, task);
        notifyItemInserted(position);
    }

    private void swap(List<Task> tasks) {
        final TaskListDiffCallback diffCallback = new TaskListDiffCallback(mTasks, tasks);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        mTasks = tasks;

        diffResult.dispatchUpdatesTo(this);
    }

    private void complete(Task task, int position, View v) {
        removeItem(position);

        Snackbar.make(v, R.string.taskCompleteSuccess, Snackbar.LENGTH_SHORT)
                .setAction(R.string.undo, l -> { addItem(position, task); })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) mViewModel.complete(task);
                    }
                }).show();
    }

    private void delete(Task task, int position, View v) {
        removeItem(position);

        Snackbar.make(v, R.string.taskDeleteSuccess, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, l -> { addItem(position, task); })
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event != DISMISS_EVENT_ACTION) mViewModel.delete(task);
                    }
                }).show();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, ItemTouchSwipeTarget {

        private Task mTask;
        private Context mContext;

        @BindView(R.id.listItemLayout) FrameLayout mListItemLayout;
        @BindView(R.id.swipeBackgroundLayout) ConstraintLayout mSwipeBackgroundLayout;
        @BindView(R.id.taskName) TextView mTaskName;
        @BindView(R.id.dueDateRow) TextView mDueDateRow;
        @BindView(R.id.durationRow) TextView mDurationRow;
        @BindView(R.id.dueStatus) TextView mDueStatus;
        @BindView(R.id.swipeIconLeft) ImageView mSwipeIconLeft;
        @BindView(R.id.swipeIconRight) ImageView mSwipeIconRight;

        TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        void bindTask(Task task) {
            mTask = task;
            DueStatus dueStatus = new DueStatus(mContext, mTask.getDuePriority());

            if (dueStatus.isDefault()) {
                mDueStatus.setVisibility(View.GONE);
            } else {
                mDueStatus.setText(dueStatus.getName());
                mDueStatus.setBackgroundColor(dueStatus.getColor());
                mDueStatus.setVisibility(View.VISIBLE);
            }

            String dueDateRow = task.getDueDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

            String durationRow = mContext.getString(R.string.durationLabel) + " ";

            if (task.getDuration() == 1) {
                durationRow += DurationUnit.build(mContext, task.getDurationUnit())
                        .getNameSingular().toLowerCase();
            } else {
                durationRow += task.getDuration() + " "
                        + DurationUnit.build(mContext, task.getDurationUnit())
                        .getName().toLowerCase();
            }

            mTaskName.setText(task.getName());
            mDueDateRow.setText(dueDateRow);
            mDurationRow.setText(durationRow);
        }

        @Override
        @OnClick
        public void onClick(View v) {
            Intent intent = new Intent(mContext, TaskActivity.class);
            intent.putExtra(TaskActivity.EXTRA_TASK_ID, mTask.getId());
            mContext.startActivity(intent);
        }

        @OnClick(R.id.completeCheckBox)
        public void onCompleteCheckBoxClick(View v) {
            final int position = getAdapterPosition();

            complete(mTask, position, v);
            ((CheckBox) v).setChecked(false);
        }

        @Override
        public View getSwipeForeground() {
            return mListItemLayout;
        }

        @Override
        public void prepareSwipeBackground(int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                mSwipeBackgroundLayout.setBackgroundColor(
                        mContext.getResources().getColor(R.color.deleteColor));
                mSwipeIconRight.setVisibility(View.VISIBLE);
                mSwipeIconLeft.setVisibility(View.INVISIBLE);
            } else {
                mSwipeBackgroundLayout.setBackgroundColor(
                        mContext.getResources().getColor(R.color.completeColor));
                mSwipeIconLeft.setVisibility(View.VISIBLE);
                mSwipeIconRight.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class TaskListDiffCallback extends DiffUtil.Callback {

        private final List<Task> mOldTaskList;
        private final List<Task> mNewTaskList;

        TaskListDiffCallback(List<Task> oldTaskList, List<Task> newTaskList) {
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
