package caseydlvr.recurringtasks.ui;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;


public class SwipeToDismissCallback extends ItemTouchHelper.Callback {

    private ItemTouchSwipeHandler mHandler;
    private ColorDrawable mBackground;
    private BitmapDrawable mIcon;
//    private Paint mClearPaint = new Paint(); // for clearCanvas

    SwipeToDismissCallback(ItemTouchSwipeHandler handler) {
        mHandler = handler;
//        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    SwipeToDismissCallback(ItemTouchSwipeHandler handler, ColorDrawable background) {
        this(handler);
        mBackground = background;
    }

    SwipeToDismissCallback(ItemTouchSwipeHandler handler, ColorDrawable background, BitmapDrawable icon) {
        this(handler, background);
        mIcon = icon;
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

        if (mBackground == null && mIcon == null) return;

//        boolean isCanceled = dX == 0f && !isCurrentlyActive;
//
//        if (isCanceled) {
//            clearCanvas(c, itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
//            return;
//        }

        boolean swipeRight = dX > 0;
        Rect bounds;
        View itemView = viewHolder.itemView;

        if (swipeRight) {
            bounds = new Rect(itemView.getLeft(), itemView.getTop(), (int) (itemView.getLeft() + dX), itemView.getBottom());
        } else {
            bounds = new Rect((int) (itemView.getRight()+dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        }

        if (mBackground != null) {
            mBackground.setBounds(bounds);
            mBackground.draw(c);
        }

        if (mIcon != null) {
            if (swipeRight) mIcon.setGravity(Gravity.LEFT | Gravity.CLIP_HORIZONTAL);
            else            mIcon.setGravity(Gravity.RIGHT | Gravity.CLIP_HORIZONTAL);

            mIcon.setBounds(bounds);
            mIcon.draw(c);
        }
    }

//    private void clearCanvas(Canvas c, float left, float top, float right, float bottom) {
//        c.drawRect(left, top, right, bottom, mClearPaint);
//    }
}
