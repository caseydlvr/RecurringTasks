package caseydlvr.recurringtasks.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;


public class SwipeToDismissCallback extends ItemTouchHelper.Callback {

    private ItemTouchSwipeHandler mHandler;

    public SwipeToDismissCallback(ItemTouchSwipeHandler handler) {
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
        mHandler.onItemSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final ColorDrawable background = new ColorDrawable(Color.RED);
        View itemView = viewHolder.itemView;

        if (dX > 0) { // swiping to the right
            background.setBounds(0, itemView.getTop(), (int) (itemView.getLeft() + dX), itemView.getBottom());
        } else { // swiping to the left
            background.setBounds((int) (itemView.getRight()+dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        }

        background.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
