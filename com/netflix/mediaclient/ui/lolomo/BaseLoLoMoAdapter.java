// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.ui.lolomo;

import android.graphics.drawable.Drawable;
import com.netflix.mediaclient.util.api.Api16Util;
import java.util.Collection;
import com.netflix.mediaclient.android.app.CommonStatus;
import android.widget.AbsListView;
import com.netflix.mediaclient.android.app.Status;
import com.netflix.mediaclient.ui.lomo.LoMoUtils;
import android.widget.ListView;
import com.netflix.mediaclient.util.ViewUtils;
import android.view.ViewGroup;
import com.netflix.mediaclient.servicemgr.interface_.LoMoType;
import java.util.Iterator;
import com.netflix.mediaclient.util.ThreadUtils;
import com.netflix.mediaclient.servicemgr.ManagerCallback;
import android.content.res.Resources;
import com.netflix.mediaclient.ui.experience.BrowseExperience;
import android.widget.TextView;
import com.netflix.mediaclient.Log;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import com.viewpagerindicator.android.osp.ViewPager;
import com.netflix.mediaclient.util.AndroidUtils;
import android.content.Context;
import com.viewpagerindicator.CirclePageIndicator;
import android.view.View;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.HashSet;
import com.netflix.mediaclient.android.widget.ObjectRecycler$ViewRecycler;
import com.netflix.mediaclient.ui.lomo.LoMoViewPager;
import java.util.Set;
import com.netflix.mediaclient.servicemgr.ServiceManager;
import com.netflix.mediaclient.android.app.LoadingStatus$LoadingStatusCallback;
import java.util.List;
import com.netflix.mediaclient.android.activity.NetflixActivity;
import android.widget.BaseAdapter;
import com.netflix.mediaclient.servicemgr.interface_.BasicLoMo;

public abstract class BaseLoLoMoAdapter<T extends BasicLoMo> extends BaseAdapter implements LoLoMoFrag$ILoLoMoAdapter
{
    private static final String TAG = "BaseLoLoMoAdapter";
    protected final NetflixActivity activity;
    private final LoLoMoFrag frag;
    private boolean hasMoreData;
    private boolean isLoading;
    private int loMoStartIndex;
    private final String lolomoId;
    private long lomoRequestId;
    private boolean lomoRequestPending;
    private final List<T> lomos;
    private LoadingStatus$LoadingStatusCallback mLoadingStatusCallback;
    private ServiceManager manager;
    private final Set<LoMoViewPager> pagerSet;
    private final ObjectRecycler$ViewRecycler viewRecycler;
    
    public BaseLoLoMoAdapter(final LoLoMoFrag frag, final String lolomoId) {
        this.pagerSet = new HashSet<LoMoViewPager>();
        this.isLoading = true;
        this.lomos = new ArrayList<T>(40);
        this.lomoRequestPending = true;
        this.frag = frag;
        this.activity = (NetflixActivity)frag.getActivity();
        this.viewRecycler = frag.getViewRecycler();
        this.lolomoId = lolomoId;
    }
    
    private boolean areRequestsPending() {
        return this.lomoRequestPending;
    }
    
    private BaseLoLoMoAdapter$LoMoRowContent createRowContent(final LinearLayout linearLayout, final View view) {
        final CirclePageIndicator circlePageIndicator = new CirclePageIndicator((Context)this.activity);
        final LoMoViewPager viewPager = new LoMoViewPager(this.frag, this.manager, circlePageIndicator, this.viewRecycler, view, this.isGenreList());
        this.pagerSet.add(viewPager);
        viewPager.setFocusable(false);
        linearLayout.addView((View)viewPager);
        circlePageIndicator.setFillColor(-1);
        circlePageIndicator.setPageColor(-11513776);
        circlePageIndicator.setRadius(AndroidUtils.dipToPixels((Context)this.activity, 4));
        circlePageIndicator.setStrokeColor(0);
        circlePageIndicator.setStrokeWidth(0.0f);
        circlePageIndicator.setOnPageChangeListener(viewPager.getOnPageChangeListener());
        circlePageIndicator.setViewPager(viewPager);
        circlePageIndicator.setVisibility(8);
        final LinearLayout$LayoutParams linearLayout$LayoutParams = new LinearLayout$LayoutParams(-1, -2);
        linearLayout$LayoutParams.topMargin = (int)(2.0f * circlePageIndicator.getRadius() + circlePageIndicator.getPaddingTop() + circlePageIndicator.getPaddingBottom() + 1.0f) * -2;
        linearLayout.addView((View)circlePageIndicator, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
        return viewPager;
    }
    
    private BaseLoLoMoAdapter$RowHolder createViewsAndHolder(final View view) {
        Log.v("BaseLoLoMoAdapter", "creating views and holder");
        final LinearLayout linearLayout = (LinearLayout)view.findViewById(2131427567);
        linearLayout.setFocusable(false);
        final TextView textView = (TextView)view.findViewById(2131427569);
        final Resources resources = this.activity.getResources();
        int n;
        if (BrowseExperience.isKubrickKids()) {
            n = 2131230822;
        }
        else {
            n = 2131230840;
        }
        textView.setTextColor(resources.getColor(n));
        return this.createHolder(view, linearLayout, this.initTitleView(view), this.createRowContent(linearLayout, (View)textView), view.findViewById(2131427623));
    }
    
    private void fetchMoreData() {
        this.isLoading = true;
        this.lomoRequestId = System.nanoTime();
        final int n = this.loMoStartIndex + 20 - 1;
        if (Log.isLoggable()) {
            Log.v("BaseLoLoMoAdapter", "fetching more data, starting at index: " + this.loMoStartIndex);
            Log.v("BaseLoLoMoAdapter", "fetching from: " + this.loMoStartIndex + " to: " + n + ", id: " + this.lolomoId);
        }
        if (this.manager == null) {
            Log.w("BaseLoLoMoAdapter", "Manager is null - can't refresh data");
            return;
        }
        this.makeFetchRequest(this.lolomoId, this.loMoStartIndex, n, new BaseLoLoMoAdapter$LoMoCallbacks(this, this.lomoRequestId, n - this.loMoStartIndex));
    }
    
    private void hideLoadingAndErrorViews() {
        this.frag.hideLoadingAndErrorViews();
    }
    
    private void initLoadingState() {
        ThreadUtils.assertOnMain();
        this.lomos.clear();
        this.lomoRequestId = -2147483648L;
        this.lomoRequestPending = true;
        this.hasMoreData = false;
        this.loMoStartIndex = 0;
        this.notifyDataSetChanged();
    }
    
    private boolean isAnyPagerLoading() {
        final Iterator<LoMoViewPager> iterator = this.pagerSet.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isLoading()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isRowAfterBillboardOrCwRow(final int n, LoMoType type) {
        if (n == 1) {
            type = this.getItem(0).getType();
            if (type == LoMoType.BILLBOARD || type == LoMoType.CONTINUE_WATCHING) {
                return true;
            }
        }
        return false;
    }
    
    private void showErrorView() {
        this.frag.showErrorView();
    }
    
    public boolean areAllItemsEnabled() {
        return false;
    }
    
    protected View createDummyView() {
        final View view = new View((Context)this.activity);
        view.setVisibility(8);
        return view;
    }
    
    protected BaseLoLoMoAdapter$RowHolder createHolder(final View view, final LinearLayout linearLayout, final TextView textView, final BaseLoLoMoAdapter$LoMoRowContent baseLoLoMoAdapter$LoMoRowContent, final View view2) {
        return new BaseLoLoMoAdapter$RowHolder((View)linearLayout, textView, baseLoLoMoAdapter$LoMoRowContent, view2);
    }
    
    public int getCount() {
        return this.lomos.size();
    }
    
    protected String getGenreId() {
        return this.lolomoId;
    }
    
    public T getItem(final int n) {
        return this.lomos.get(n);
    }
    
    public long getItemId(final int n) {
        return n;
    }
    
    protected ServiceManager getServiceManager() {
        return this.manager;
    }
    
    protected int getShelfVisibility(final T t, final int n) {
        if (this.isRowAfterBillboardOrCwRow(n, t.getType()) && !BrowseExperience.isKubrickKids()) {
            return 0;
        }
        return 8;
    }
    
    public View getView(final int n, View dummyView, final ViewGroup viewGroup) {
        if (this.activity.destroyed()) {
            Log.d("BaseLoLoMoAdapter", "activity is destroyed - can't getView");
            dummyView = this.createDummyView();
        }
        else {
            View inflate;
            if ((inflate = dummyView) == null) {
                inflate = this.activity.getLayoutInflater().inflate(this.getViewLayoutId(), viewGroup, false);
                inflate.setTag((Object)this.createViewsAndHolder(inflate));
            }
            final BasicLoMo item = this.getItem(n);
            if (item == null) {
                Log.w("BaseLoLoMoAdapter", "Trying to show data for null lomo! Position: " + n);
            }
            else {
                this.updateRowViews((BaseLoLoMoAdapter$RowHolder)inflate.getTag(), (T)item, n);
            }
            final ListView listView = this.frag.getListView();
            int headerViewsCount;
            if (listView == null) {
                headerViewsCount = 0;
            }
            else {
                headerViewsCount = listView.getHeaderViewsCount();
            }
            if (headerViewsCount == 0) {
                int actionBarHeight;
                if (n == 0) {
                    actionBarHeight = this.activity.getActionBarHeight();
                }
                else {
                    actionBarHeight = 0;
                }
                ViewUtils.setPaddingTop(inflate, actionBarHeight);
            }
            dummyView = inflate;
            if (this.hasMoreData) {
                dummyView = inflate;
                if (n == this.getCount() - 1) {
                    this.fetchMoreData();
                    return inflate;
                }
            }
        }
        return dummyView;
    }
    
    protected int getViewLayoutId() {
        return 2130903129;
    }
    
    protected TextView initTitleView(final View view) {
        final TextView textView = (TextView)view.findViewById(2131427568);
        if (Log.isLoggable()) {
            Log.v("BaseLoLoMoAdapter", "Manipulating title padding, view: " + textView);
        }
        if (textView != null) {
            ViewUtils.setPaddingLeft((View)textView, LoMoUtils.getLomoFragImageOffsetLeftPx(this.activity));
        }
        return textView;
    }
    
    public boolean isEnabled(final int n) {
        return false;
    }
    
    protected abstract boolean isGenreList();
    
    public boolean isLoadingData() {
        return this.isLoading || this.isAnyPagerLoading();
    }
    
    protected abstract void makeFetchRequest(final String p0, final int p1, final int p2, final ManagerCallback p3);
    
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        ThreadUtils.assertOnMain();
        if (this.getCount() > 0) {
            this.hideLoadingAndErrorViews();
        }
        else if (!this.areRequestsPending()) {
            this.showErrorView();
        }
    }
    
    protected void onDataLoaded(final Status status) {
        if (this.mLoadingStatusCallback != null) {
            this.mLoadingStatusCallback.onDataLoaded(status);
        }
    }
    
    public void onDestroyView() {
        final Iterator<LoMoViewPager> iterator = this.pagerSet.iterator();
        while (iterator.hasNext()) {
            iterator.next().destroy();
        }
    }
    
    public void onManagerReady(final ServiceManager manager, final Status status) {
        this.manager = manager;
        this.refreshData();
    }
    
    public void onManagerUnavailable(final ServiceManager serviceManager, final Status status) {
        this.manager = null;
    }
    
    public void onPause() {
        final Iterator<LoMoViewPager> iterator = this.pagerSet.iterator();
        while (iterator.hasNext()) {
            iterator.next().onPause();
        }
    }
    
    public void onResume() {
        final Iterator<LoMoViewPager> iterator = this.pagerSet.iterator();
        while (iterator.hasNext()) {
            iterator.next().onResume();
        }
    }
    
    public void onScroll(final AbsListView absListView, final int n, final int n2, final int n3) {
    }
    
    public void onScrollStateChanged(final AbsListView absListView, final int n) {
    }
    
    public void refreshData() {
        Log.v("BaseLoLoMoAdapter", "Refreshing data");
        this.isLoading = true;
        this.initLoadingState();
        this.fetchMoreData();
    }
    
    public void setLoadingStatusCallback(final LoadingStatus$LoadingStatusCallback mLoadingStatusCallback) {
        if (!this.isLoadingData() && mLoadingStatusCallback != null) {
            mLoadingStatusCallback.onDataLoaded(CommonStatus.OK);
            return;
        }
        this.mLoadingStatusCallback = mLoadingStatusCallback;
    }
    
    protected void updateLoMoData(final List<T> list) {
        this.lomos.addAll((Collection<? extends T>)list);
        this.loMoStartIndex += list.size();
        this.notifyDataSetChanged();
    }
    
    protected void updateRowViews(final BaseLoLoMoAdapter$RowHolder baseLoLoMoAdapter$RowHolder, final T t, int dipToPixels) {
        if (Log.isLoggable()) {
            Log.v("BaseLoLoMoAdapter", "Updating LoMo row content: " + t.getTitle() + ", type: " + t.getType() + ", pos: " + dipToPixels);
        }
        final TextView title = baseLoLoMoAdapter$RowHolder.title;
        String text;
        if (t.getType() == LoMoType.BILLBOARD) {
            text = this.activity.getString(2131493292);
        }
        else {
            text = t.getTitle();
        }
        title.setText((CharSequence)text);
        baseLoLoMoAdapter$RowHolder.title.setVisibility(BrowseExperience.get().getLomoRowTitleVisibility(this.activity, t));
        if (baseLoLoMoAdapter$RowHolder.shelf != null) {
            baseLoLoMoAdapter$RowHolder.shelf.setVisibility(this.getShelfVisibility(t, dipToPixels));
        }
        baseLoLoMoAdapter$RowHolder.rowContent.refresh(t, dipToPixels);
        if (BrowseExperience.isKubrickKids()) {
            Api16Util.setBackgroundDrawableCompat(baseLoLoMoAdapter$RowHolder.contentGroup, null);
            if (dipToPixels == this.getCount() - 1) {
                dipToPixels = 1;
            }
            else {
                dipToPixels = 0;
            }
            final View contentGroup = baseLoLoMoAdapter$RowHolder.contentGroup;
            if (dipToPixels != 0) {
                dipToPixels = AndroidUtils.dipToPixels((Context)this.activity, 24);
            }
            else {
                dipToPixels = 0;
            }
            contentGroup.setPadding(0, 0, 0, dipToPixels);
            baseLoLoMoAdapter$RowHolder.title.setTextColor(baseLoLoMoAdapter$RowHolder.defaultTitleColors);
        }
    }
}
