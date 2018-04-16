package caseydlvr.recurringtasks.ui;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;


public class SwipeToDismissCallback extends ItemTouchHelper.Callback {

    private ItemTouchSwipeHandler mHandler;

    SwipeToDismissCallback(ItemTouchSwipeHandler handler) {
        mHandler = handler;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
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
        mHandler.onItemSwiped(viewHolder, direction);
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
