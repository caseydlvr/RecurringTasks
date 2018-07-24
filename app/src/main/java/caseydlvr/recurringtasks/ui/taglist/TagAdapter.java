package caseydlvr.recurringtasks.ui.taglist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.viewmodel.TagListViewModel;

public class TagAdapter  extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    private static final String TAG = TagAdapter.class.getSimpleName();

    private List<Tag> mTags;
    private TagListViewModel mViewModel;
    private RecyclerView mRecyclerView;

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_list_item, parent, false);

        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        Tag tag = mTags.get(position);
        holder.bindTag(tag);
        holder.setChecked(mViewModel.getCheckedTags().contains(tag));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return mTags != null ? mTags.size() : 0;
    }

    public void setViewModel(TagListViewModel viewModel) {
        mViewModel = viewModel;
    }

    public void setTags(List<Tag> newTags) {
        List<Tag> oldTags = mTags;
        mTags = newTags;
        if (oldTags == null) {
            notifyItemRangeChanged(0, newTags.size());
        } else {
            DiffUtil.calculateDiff(new TagListDiffCallback(oldTags, newTags))
                    .dispatchUpdatesTo(this);
        }
    }

    public void setTagsChecked(List<Tag> checkedTags) {
        if (mTags == null) return;

        for (int i = 0; i < mTags.size(); i++) {
            Tag tag = mTags.get(i);
            TagViewHolder viewHolder = (TagViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);

            if (viewHolder != null) {
                viewHolder.setChecked(checkedTags.contains(tag));
            }
        }
    }

    public class TagViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Tag mTag;

        @BindView(R.id.tagName) TextView mTagName;
        @BindView(R.id.tagCheckBox) CheckBox mTagCheckBox;

        TagViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTag(Tag tag) {
            mTag = tag;
            mTagName.setText(tag.getName());
        }

        @Override
        @OnClick
        public void onClick(View view) {
            onTagCheckboxClick();
        }

        @OnClick(R.id.tagCheckBox)
        void onTagCheckboxClick() {
            if (mTagCheckBox.isChecked()) {
                mViewModel.addTaskTag(mTags.get(getAdapterPosition()));
            } else {
                mViewModel.removeTaskTag(mTags.get(getAdapterPosition()));
            }
        }

        void setChecked(boolean checked) {
            if (checked != mTagCheckBox.isChecked()) {
                mTagCheckBox.setChecked(checked);
            }
        }
    }

    public class TagListDiffCallback extends DiffUtil.Callback {

        private final List<Tag> mOldTagList;
        private final List<Tag> mNewTagList;

        TagListDiffCallback(List<Tag> oldTagList, List<Tag> newTagList) {
            mOldTagList = oldTagList;
            mNewTagList = newTagList;
        }

        @Override
        public int getOldListSize() {
            return mOldTagList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewTagList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldTagList.get(oldItemPosition).getId()
                    == mNewTagList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldTagList.get(oldItemPosition).equals(mNewTagList.get(newItemPosition));
        }
    }
}
