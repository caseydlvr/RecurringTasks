package caseydlvr.recurringtasks.ui.tasklist;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchSwipeListener {
    void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction);
}
