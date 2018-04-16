package caseydlvr.recurringtasks.ui;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchSwipeHandler {
    void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction);
}
