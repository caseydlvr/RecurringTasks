package caseydlvr.recurringtasks.ui.tasklist;

import android.view.View;

/**
 * Interface for a RecyclerView swipe to dismiss target. Implemented by ViewHolders that can be
 * swiped to dismiss.
 */
public interface ItemTouchSwipeTarget {
    /**
     * @return View to show in the foreground while a swipe animation is in progress. This is
     *         typically the View that is being swiped away
     */
    View getSwipeForeground();

    /**
     * @param direction direction of the swipe. Corresponds to an ItemTouchHelper direction constant
     * @see             androidx.recyclerview.widget.ItemTouchHelper
     */
    void prepareSwipeBackground(int direction);
}
