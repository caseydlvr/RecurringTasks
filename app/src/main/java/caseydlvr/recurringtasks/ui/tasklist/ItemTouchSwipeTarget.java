package caseydlvr.recurringtasks.ui.tasklist;

import android.view.View;

public interface ItemTouchSwipeTarget {
    View getSwipeForeground();
    void prepareSwipeBackground(int direction);
}
