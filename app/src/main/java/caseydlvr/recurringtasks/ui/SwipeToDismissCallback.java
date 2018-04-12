package caseydlvr.recurringtasks.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;

import caseydlvr.recurringtasks.R;


public class SwipeToDismissCallback extends ItemTouchHelper.Callback {

    private ItemTouchSwipeHandler mHandler;
//    private Paint mClearPaint = new Paint(); // for clearCanvas

    public SwipeToDismissCallback(ItemTouchSwipeHandler handler) {
        mHandler = handler;
//        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        final ColorDrawable background = new ColorDrawable(Color.RED);
        final BitmapDrawable icon = (BitmapDrawable) itemView.getContext().getDrawable(R.drawable.ic_delete);

//        boolean isCanceled = dX == 0f && !isCurrentlyActive;
//
//        if (isCanceled) {
//            clearCanvas(c, itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
//            return;
//        }

        if (dX > 0) { // swiping to the right
            background.setBounds(0, itemView.getTop(), (int) (itemView.getLeft() + dX), itemView.getBottom());
            icon.setGravity(Gravity.LEFT | Gravity.CLIP_HORIZONTAL);
            icon.setBounds(itemView.getLeft(), itemView.getTop(), (int) (itemView.getLeft() + dX), itemView.getBottom());
        } else { // swiping to the left
            background.setBounds((int) (itemView.getRight()+dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            icon.setGravity(Gravity.RIGHT | Gravity.CLIP_HORIZONTAL);
            icon.setBounds((int) (itemView.getRight()+dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        }

        background.draw(c);
        icon.draw(c);
    }

//    private void clearCanvas(Canvas c, float left, float top, float right, float bottom) {
//        c.drawRect(left, top, right, bottom, mClearPaint);
//    }
}
