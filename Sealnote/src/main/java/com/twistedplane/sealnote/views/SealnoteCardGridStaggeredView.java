package com.twistedplane.sealnote.views;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListAdapter;
import com.twistedplane.sealnote.data.CardGridStaggeredCursorAdapter;
import com.twistedplane.sealnote.utils.PreferenceHandler;
import it.gmariotti.cardslib.library.extra.staggeredgrid.internal.CardGridStaggeredArrayAdapter;
import it.gmariotti.cardslib.library.extra.staggeredgrid.view.CardGridStaggeredView;

/**
 * Specialized CardGridStaggeredView class used in SealNote.
 *
 *   + Add support for cursor adapter
 *   + Changes to make column mode change as per latest Sealnote settings
 *     preferences
 */
public class SealnoteCardGridStaggeredView extends CardGridStaggeredView {
    public final static String TAG = "SealnoteCardGridStaggeredView";

    protected CardGridStaggeredCursorAdapter mCursorAdapter;

    public SealnoteCardGridStaggeredView(Context context) {
        super(context);
    }

    public SealnoteCardGridStaggeredView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SealnoteCardGridStaggeredView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Uses CardGridStaggeredCursorAdapter to avoid dispatch to generic
     * ListAdapter version of this function which doesn't support this
     * kind of adapter.
     */
    public void setAdapter(CardGridStaggeredCursorAdapter adapter) {
        super.setAdapter(adapter);
        adapter.setRowLayoutId(list_card_layout_resourceID);
        adapter.setCardGridView(this);
        mCursorAdapter = adapter;
    }

    /**
     * Uses CardGridStaggeredCursorAdapter to avoid dispatch to generic
     * ListAdapter version of this function which doesn't support this
     * kind of adapter.
     */
    public void setExternalAdapter(ListAdapter adapter, CardGridStaggeredCursorAdapter cardCursorAdapter) {
        setAdapter(adapter);

        mCursorAdapter = cardCursorAdapter;
        mCursorAdapter.setCardGridView(this);
        mCursorAdapter.setRowLayoutId(list_card_layout_resourceID);
    }

    /**
     * Add support for CardGridStaggeredCursorAdapter.
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter instanceof CardGridStaggeredArrayAdapter) {
            setAdapter((CardGridStaggeredArrayAdapter)adapter);
        } else if (adapter instanceof CardGridStaggeredCursorAdapter) {
            setAdapter((CardGridStaggeredCursorAdapter)adapter);
        } else {
            Log.w(TAG, "You are using a generic adapter. Pay attention: your adapter has to call cardGridArrayAdapter#getView method.");
            super.setAdapter(adapter);
        }
    }

    /**
     * Apart from usual behaviour, update latest column count from SealNote
     * preference value.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        updateGridColumnCount();
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * Update column count as per current orientation of device and SealNote
     * multi-column/single-column preference value.
     */
    public void updateGridColumnCount() {
        if (!PreferenceHandler.isMultiColumnGridEnabled(getContext())) {
            // Single-column mode is enabled
            setColumnCount(1);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setColumnCountPortrait(2);
                setColumnCountLandscape(3);
            } else {
                setColumnCountLandscape(3);
                setColumnCountPortrait(2);
            }
        }
    }
}
