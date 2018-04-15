package caseydlvr.recurringtasks.ui;

import android.view.View;

public interface ItemTouchSwipeTarget {
    View getSwipeForeground();
    void prepareSwipeBackground(int swipeDirection);
}
