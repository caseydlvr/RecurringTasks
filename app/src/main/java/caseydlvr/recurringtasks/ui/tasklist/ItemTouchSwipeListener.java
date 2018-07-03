package caseydlvr.recurringtasks.ui.tasklist;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Interface for a RecyclerView swipe to dismiss listener. Implemented by classes that wish to
 * respond to swipe to dismiss actions.
 */
public interface ItemTouchSwipeListener {
    /**
     * Callback triggered when the user swipes away a RecyclerView item
     *
     * @param viewHolder ViewHolder swiped by the user
     * @param direction  Direction of the swipe. Corresponds to an ItemTouchHelper direction constant
     * @see              androidx.recyclerview.widget.ItemTouchHelper
     */
    void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction);
}
