// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.util;

import com.netflix.mediaclient.android.widget.RecyclerViewHeaderAdapter;
import android.support.v7.widget.RecyclerView$State;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView$ItemDecoration;

public final class ItemDecorationEdgePadding extends RecyclerView$ItemDecoration
{
    private int edgePaddingMuliplier;
    private int numColumns;
    private int padding;
    
    public ItemDecorationEdgePadding(final int padding, final int numColumns, final int edgePaddingMuliplier) {
        this.padding = padding;
        this.numColumns = numColumns;
        this.edgePaddingMuliplier = edgePaddingMuliplier;
    }
    
    @Override
    public void getItemOffsets(final Rect rect, final View view, final RecyclerView recyclerView, final RecyclerView$State recyclerView$State) {
        if (!((RecyclerViewHeaderAdapter)recyclerView.getAdapter()).isViewHeader(view, recyclerView)) {
            rect.left = this.padding / 2;
            rect.right = this.padding / 2;
            rect.bottom = this.padding / 2;
            rect.top = this.padding / 2;
            if (recyclerView.getChildPosition(view) % this.numColumns == 1) {
                rect.left = this.padding * this.edgePaddingMuliplier;
            }
            if (recyclerView.getChildPosition(view) % this.numColumns == 0) {
                rect.right = this.padding * this.edgePaddingMuliplier;
            }
        }
    }
}
