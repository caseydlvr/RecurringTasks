package caseydlvr.recurringtasks.ui.tasklist;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;


/**
 * ItemTouchHelper callback used to handle RecyclerView swipe to dismiss functionality. Currently
 * only supports swiping left or right (no long press drag, no movement, no swiping vertically).
 */
public class SwipeToDismissCallback extends ItemTouchHelper.Callback {

    private ItemTouchSwipeListener mListener;

    SwipeToDismissCallback(ItemTouchSwipeListener listener) {
        mListener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // only enable swiping left or right
        return makeMovementFlags(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mListener.onItemSwiped(viewHolder, direction);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((ItemTouchSwipeTarget) viewHolder).getSwipeForeground();

        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((ItemTouchSwipeTarget) viewHolder).getSwipeForeground();

        int swipeDirection = (dX > 0) ? ItemTouchHelper.RIGHT : ItemTouchHelper.LEFT;

        ((ItemTouchSwipeTarget) viewHolder).prepareSwipeBackground(swipeDirection);

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((ItemTouchSwipeTarget) viewHolder).getSwipeForeground();

        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((ItemTouchSwipeTarget) viewHolder).getSwipeForeground();

            getDefaultUIUtil().onSelected(foregroundView);
        }
    }
}
