// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v7.widget;

import android.widget.Adapter;
import android.view.MotionEvent;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.view.ViewGroup$LayoutParams;
import android.view.View$MeasureSpec;
import android.graphics.drawable.Drawable;
import android.support.v7.appcompat.R$attr;
import android.util.AttributeSet;
import android.os.Build$VERSION;
import android.support.v7.internal.widget.TintManager;
import android.graphics.Rect;
import android.content.Context;
import android.support.v4.view.TintableBackgroundView;
import android.widget.Spinner;
import android.database.DataSetObserver;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ThemedSpinnerAdapter;
import android.content.res.Resources$Theme;
import android.widget.SpinnerAdapter;
import android.widget.ListAdapter;

class AppCompatSpinner$DropDownAdapter implements ListAdapter, SpinnerAdapter
{
    private SpinnerAdapter mAdapter;
    private ListAdapter mListAdapter;
    
    public AppCompatSpinner$DropDownAdapter(final SpinnerAdapter mAdapter, final Resources$Theme resources$Theme) {
        this.mAdapter = mAdapter;
        if (mAdapter instanceof ListAdapter) {
            this.mListAdapter = (ListAdapter)mAdapter;
        }
        if (resources$Theme != null) {
            if (AppCompatSpinner.IS_AT_LEAST_M && mAdapter instanceof ThemedSpinnerAdapter) {
                final ThemedSpinnerAdapter themedSpinnerAdapter = (ThemedSpinnerAdapter)mAdapter;
                if (themedSpinnerAdapter.getDropDownViewTheme() != resources$Theme) {
                    themedSpinnerAdapter.setDropDownViewTheme(resources$Theme);
                }
            }
            else if (mAdapter instanceof android.support.v7.widget.ThemedSpinnerAdapter) {
                final android.support.v7.widget.ThemedSpinnerAdapter themedSpinnerAdapter2 = (android.support.v7.widget.ThemedSpinnerAdapter)mAdapter;
                if (themedSpinnerAdapter2.getDropDownViewTheme() == null) {
                    themedSpinnerAdapter2.setDropDownViewTheme(resources$Theme);
                }
            }
        }
    }
    
    public boolean areAllItemsEnabled() {
        final ListAdapter mListAdapter = this.mListAdapter;
        return mListAdapter == null || mListAdapter.areAllItemsEnabled();
    }
    
    public int getCount() {
        if (this.mAdapter == null) {
            return 0;
        }
        return this.mAdapter.getCount();
    }
    
    public View getDropDownView(final int n, final View view, final ViewGroup viewGroup) {
        if (this.mAdapter == null) {
            return null;
        }
        return this.mAdapter.getDropDownView(n, view, viewGroup);
    }
    
    public Object getItem(final int n) {
        if (this.mAdapter == null) {
            return null;
        }
        return this.mAdapter.getItem(n);
    }
    
    public long getItemId(final int n) {
        if (this.mAdapter == null) {
            return -1L;
        }
        return this.mAdapter.getItemId(n);
    }
    
    public int getItemViewType(final int n) {
        return 0;
    }
    
    public View getView(final int n, final View view, final ViewGroup viewGroup) {
        return this.getDropDownView(n, view, viewGroup);
    }
    
    public int getViewTypeCount() {
        return 1;
    }
    
    public boolean hasStableIds() {
        return this.mAdapter != null && this.mAdapter.hasStableIds();
    }
    
    public boolean isEmpty() {
        return this.getCount() == 0;
    }
    
    public boolean isEnabled(final int n) {
        final ListAdapter mListAdapter = this.mListAdapter;
        return mListAdapter == null || mListAdapter.isEnabled(n);
    }
    
    public void registerDataSetObserver(final DataSetObserver dataSetObserver) {
        if (this.mAdapter != null) {
            this.mAdapter.registerDataSetObserver(dataSetObserver);
        }
    }
    
    public void unregisterDataSetObserver(final DataSetObserver dataSetObserver) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(dataSetObserver);
        }
    }
}