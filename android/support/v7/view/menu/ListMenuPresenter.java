// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v7.view.menu;

import android.util.SparseArray;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.view.ContextThemeWrapper;
import android.support.v7.appcompat.R$layout;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.AdapterView$OnItemClickListener;

public class ListMenuPresenter implements MenuPresenter, AdapterView$OnItemClickListener
{
    ListMenuPresenter$MenuAdapter mAdapter;
    private MenuPresenter$Callback mCallback;
    Context mContext;
    private int mId;
    LayoutInflater mInflater;
    int mItemIndexOffset;
    int mItemLayoutRes;
    MenuBuilder mMenu;
    ExpandedMenuView mMenuView;
    int mThemeRes;
    
    public ListMenuPresenter(final int mItemLayoutRes, final int mThemeRes) {
        this.mItemLayoutRes = mItemLayoutRes;
        this.mThemeRes = mThemeRes;
    }
    
    public ListMenuPresenter(final Context mContext, final int n) {
        this(n, 0);
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(this.mContext);
    }
    
    @Override
    public boolean collapseItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    @Override
    public boolean expandItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    @Override
    public boolean flagActionItems() {
        return false;
    }
    
    public ListAdapter getAdapter() {
        if (this.mAdapter == null) {
            this.mAdapter = new ListMenuPresenter$MenuAdapter(this);
        }
        return (ListAdapter)this.mAdapter;
    }
    
    @Override
    public int getId() {
        return this.mId;
    }
    
    public MenuView getMenuView(final ViewGroup viewGroup) {
        if (this.mMenuView == null) {
            this.mMenuView = (ExpandedMenuView)this.mInflater.inflate(R$layout.abc_expanded_menu_layout, viewGroup, false);
            if (this.mAdapter == null) {
                this.mAdapter = new ListMenuPresenter$MenuAdapter(this);
            }
            this.mMenuView.setAdapter((ListAdapter)this.mAdapter);
            this.mMenuView.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        }
        return this.mMenuView;
    }
    
    @Override
    public void initForMenu(final Context mContext, final MenuBuilder mMenu) {
        if (this.mThemeRes != 0) {
            this.mContext = (Context)new ContextThemeWrapper(mContext, this.mThemeRes);
            this.mInflater = LayoutInflater.from(this.mContext);
        }
        else if (this.mContext != null) {
            this.mContext = mContext;
            if (this.mInflater == null) {
                this.mInflater = LayoutInflater.from(this.mContext);
            }
        }
        this.mMenu = mMenu;
        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        if (this.mCallback != null) {
            this.mCallback.onCloseMenu(menuBuilder, b);
        }
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        this.mMenu.performItemAction((MenuItem)this.mAdapter.getItem(n), this, 0);
    }
    
    @Override
    public void onRestoreInstanceState(final Parcelable parcelable) {
        this.restoreHierarchyState((Bundle)parcelable);
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        if (this.mMenuView == null) {
            return null;
        }
        final Bundle bundle = new Bundle();
        this.saveHierarchyState(bundle);
        return (Parcelable)bundle;
    }
    
    @Override
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        if (!subMenuBuilder.hasVisibleItems()) {
            return false;
        }
        new MenuDialogHelper(subMenuBuilder).show(null);
        if (this.mCallback != null) {
            this.mCallback.onOpenSubMenu(subMenuBuilder);
        }
        return true;
    }
    
    public void restoreHierarchyState(final Bundle bundle) {
        final SparseArray sparseParcelableArray = bundle.getSparseParcelableArray("android:menu:list");
        if (sparseParcelableArray != null) {
            ((View)this.mMenuView).restoreHierarchyState(sparseParcelableArray);
        }
    }
    
    public void saveHierarchyState(final Bundle bundle) {
        final SparseArray sparseArray = new SparseArray();
        if (this.mMenuView != null) {
            ((View)this.mMenuView).saveHierarchyState(sparseArray);
        }
        bundle.putSparseParcelableArray("android:menu:list", sparseArray);
    }
    
    @Override
    public void setCallback(final MenuPresenter$Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    @Override
    public void updateMenuView(final boolean b) {
        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
