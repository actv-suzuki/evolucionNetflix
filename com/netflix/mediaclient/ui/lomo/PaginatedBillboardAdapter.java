// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.lomo;

import com.netflix.mediaclient.servicemgr.Trackable;
import android.view.View;
import com.netflix.mediaclient.servicemgr.BasicLoMo;
import java.util.List;
import com.netflix.mediaclient.android.widget.ViewRecycler;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.util.DeviceUtils;
import android.content.Context;
import com.netflix.mediaclient.service.webclient.model.BillboardDetails;

public class PaginatedBillboardAdapter extends BasePaginatedAdapter<BillboardDetails>
{
    private static final String TAG = "PaginatedBillboardAdapter";
    
    public PaginatedBillboardAdapter(final Context context) {
        super(context);
    }
    
    @Override
    protected int computeNumItemsPerPage(final int n, final int n2) {
        return 1;
    }
    
    @Override
    protected int computeNumVideosToFetchPerBatch(final Context context) {
        return 20;
    }
    
    @Override
    public int getRowHeightInPx() {
        int n;
        if (BillboardView.shouldShowArtworkOnly(this.activity)) {
            n = (int)(BasePaginatedAdapter.computeViewPagerWidth((Context)this.activity, false) * 0.5625f);
        }
        else {
            final int computeViewPagerWidth = BasePaginatedAdapter.computeViewPagerWidth((Context)this.activity, false);
            int n2;
            if (DeviceUtils.isLandscape((Context)this.activity)) {
                n2 = 3;
            }
            else {
                n2 = 2;
            }
            n = computeViewPagerWidth / n2;
        }
        Log.v("PaginatedBillboardAdapter", "Computed view height: " + n);
        return n;
    }
    
    @Override
    protected View getView(final ViewRecycler viewRecycler, final List<BillboardDetails> list, final int n, final int n2, final BasicLoMo basicLoMo) {
        BillboardViewGroup billboardViewGroup;
        if ((billboardViewGroup = (BillboardViewGroup)viewRecycler.pop(BillboardViewGroup.class)) == null) {
            billboardViewGroup = new BillboardViewGroup((Context)this.getActivity());
            billboardViewGroup.init(n);
        }
        ((VideoViewGroup<BillboardDetails, V>)billboardViewGroup).updateDataThenViews(list, n, n2, this.getListViewPos(), basicLoMo);
        return (View)billboardViewGroup;
    }
}
