// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.lomo;

import android.view.View;
import com.netflix.mediaclient.servicemgr.Trackable;
import java.util.List;
import com.netflix.mediaclient.android.widget.ViewRecycler;
import com.netflix.mediaclient.Log;
import com.netflix.mediaclient.util.DeviceUtils;
import android.content.Context;
import android.util.SparseIntArray;
import android.util.SparseArray;
import com.netflix.mediaclient.servicemgr.Video;

public class PaginatedLoMoAdapter extends BasePaginatedAdapter<Video>
{
    private static final String TAG = "PaginatedLoMoAdapter";
    public static final SparseArray<SparseIntArray> numVideosPerPageTable;
    
    static {
        numVideosPerPageTable = new SparseArray(2);
        final SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(1, 2);
        sparseIntArray.put(2, 3);
        sparseIntArray.put(3, 4);
        sparseIntArray.put(4, 4);
        PaginatedLoMoAdapter.numVideosPerPageTable.put(1, (Object)sparseIntArray);
        final SparseIntArray sparseIntArray2 = new SparseIntArray();
        sparseIntArray2.put(1, 3);
        sparseIntArray2.put(2, 4);
        sparseIntArray2.put(3, 5);
        sparseIntArray2.put(4, 6);
        PaginatedLoMoAdapter.numVideosPerPageTable.put(2, (Object)sparseIntArray2);
    }
    
    public PaginatedLoMoAdapter(final Context context) {
        super(context);
    }
    
    public static int computeNumVideosToFetchPerBatch(final int n) {
        return ((SparseIntArray)PaginatedLoMoAdapter.numVideosPerPageTable.get(1)).get(n) * ((SparseIntArray)PaginatedLoMoAdapter.numVideosPerPageTable.get(2)).get(n);
    }
    
    public static int getViewHeightInPixels(final Context context) {
        final int n = (int)(BasePaginatedAdapter.computeViewPagerWidth(context, true) / ((SparseIntArray)PaginatedLoMoAdapter.numVideosPerPageTable.get(DeviceUtils.getBasicScreenOrientation(context))).get(DeviceUtils.getScreenSizeCategory(context)) * 1.43f + 0.5f);
        Log.v("PaginatedLoMoAdapter", "Computed view height: " + n);
        return n;
    }
    
    @Override
    protected int computeNumItemsPerPage(final int n, final int n2) {
        return ((SparseIntArray)PaginatedLoMoAdapter.numVideosPerPageTable.get(n)).get(n2);
    }
    
    @Override
    protected int computeNumVideosToFetchPerBatch(final Context context) {
        return computeNumVideosToFetchPerBatch(DeviceUtils.getScreenSizeCategory(context));
    }
    
    @Override
    protected View getView(final ViewRecycler viewRecycler, final List<Video> list, final int n, final int n2, final Trackable trackable) {
        LoMoViewGroup loMoViewGroup;
        if ((loMoViewGroup = (LoMoViewGroup)viewRecycler.pop(LoMoViewGroup.class)) == null) {
            loMoViewGroup = new LoMoViewGroup((Context)this.getActivity());
            loMoViewGroup.init(n);
        }
        ((VideoViewGroup<Video, V>)loMoViewGroup).updateDataThenViews(list, n, n2, this.getListViewPos(), trackable);
        return (View)loMoViewGroup;
    }
}