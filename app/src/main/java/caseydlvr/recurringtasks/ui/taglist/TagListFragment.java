package caseydlvr.recurringtasks.ui.taglist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import caseydlvr.recurringtasks.R;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.viewmodel.TagListViewModel;

public class TagListFragment extends Fragment implements CreateDialogFragment.CreateTagClickListener {
    private static final String TAG = TagListFragment.class.getSimpleName();

    public static final String KEY_TASK_ID = "key_task_id";

    private TagAdapter mTagAdapter = new TagAdapter();
    private TagListViewModel mViewModel;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.addTagFab) FloatingActionButton mAddTagFab;
    @BindView(R.id.emptyView) TextView mEmptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tag_list, container, false);
        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(null);
        actionBar.setTitle("Select tags");

        initRecyclerView();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TagListViewModel.class);

        long taskId = getArguments().getLong(KEY_TASK_ID, -1);
        mViewModel.setTaskId(taskId);

        mTagAdapter.setViewModel(mViewModel);
        subscribeUi(mViewModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateTagClick(String tagName) {
        mViewModel.addTag(tagName);
    }

    private void initRecyclerView() {
        mRecyclerView.setAdapter(mTagAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
    }

    private void subscribeUi(@NonNull TagListViewModel viewModel) {
        viewModel.getAllTags().observe(this, tags -> {
            if (tags == null || tags.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
            mTagAdapter.setTags(tags);
        });
    }

    @OnClick(R.id.addTagFab)
    public void fabClick() {
        DialogFragment createDialog = new CreateDialogFragment();
        createDialog.setTargetFragment(this, 0);
        createDialog.show(getFragmentManager(), "create_dialog");
    }
}
