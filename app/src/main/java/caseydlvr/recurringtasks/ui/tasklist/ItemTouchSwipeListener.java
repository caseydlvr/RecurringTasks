package caseydlvr.recurringtasks.ui.tasklist;

import android.support.v7.widget.RecyclerView;

/**
 * Interface for a RecyclerView swipe to dismiss listener.
 */
public interface ItemTouchSwipeListener {
    /**
     * Callback triggered when the user swipes away a RecyclerView item
     *
     * @param viewHolder ViewHolder swiped by the user
     * @param direction  Direction of the swipe
     */
    void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction);
}
