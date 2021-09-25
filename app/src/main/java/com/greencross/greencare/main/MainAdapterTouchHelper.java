package com.greencross.greencare.main;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by adammcneilly on 9/8/15.
 */
public class MainAdapterTouchHelper extends ItemTouchHelper.SimpleCallback {
    private final String TAG = MainAdapterTouchHelper.class.getSimpleName();

    private MainAdapter mCardAdapter;

    public MainAdapterTouchHelper(MainAdapter movieAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mCardAdapter = movieAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mCardAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * 움직임 허용 할 방향 세팅
     * getMovementFlags가 Override 되지 않은 경우 상하로만 이동 가능
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mCardAdapter.remove(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        Log.i(TAG, "onSelectedChanged.actionState="+actionState);

        boolean isDrag = actionState == ItemTouchHelper.ACTION_STATE_DRAG;
        this.mCardAdapter.setDragState(isDrag);

    }
}
