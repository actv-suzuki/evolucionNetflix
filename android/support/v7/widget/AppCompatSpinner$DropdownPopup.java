// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v7.widget;

import android.view.ViewTreeObserver;
import android.widget.PopupWindow$OnDismissListener;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.support.v4.view.ViewCompat;
import android.graphics.drawable.Drawable;
import android.widget.SpinnerAdapter;
import android.widget.AdapterView$OnItemClickListener;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.widget.ListAdapter;

class AppCompatSpinner$DropdownPopup extends ListPopupWindow
{
    ListAdapter mAdapter;
    private CharSequence mHintText;
    private final Rect mVisibleRect;
    final /* synthetic */ AppCompatSpinner this$0;
    
    public AppCompatSpinner$DropdownPopup(final AppCompatSpinner appCompatSpinner, final Context context, final AttributeSet set, final int n) {
        this.this$0 = appCompatSpinner;
        super(context, set, n);
        this.mVisibleRect = new Rect();
        this.setAnchorView((View)appCompatSpinner);
        this.setModal(true);
        this.setPromptPosition(0);
        this.setOnItemClickListener((AdapterView$OnItemClickListener)new AppCompatSpinner$DropdownPopup$1(this, appCompatSpinner));
    }
    
    void computeContentWidth() {
        final Drawable background = this.getBackground();
        int right;
        if (background != null) {
            background.getPadding(this.this$0.mTempRect);
            if (ViewUtils.isLayoutRtl((View)this.this$0)) {
                right = this.this$0.mTempRect.right;
            }
            else {
                right = -this.this$0.mTempRect.left;
            }
        }
        else {
            final Rect mTempRect = this.this$0.mTempRect;
            this.this$0.mTempRect.right = 0;
            mTempRect.left = 0;
            right = 0;
        }
        final int paddingLeft = this.this$0.getPaddingLeft();
        final int paddingRight = this.this$0.getPaddingRight();
        final int width = this.this$0.getWidth();
        if (this.this$0.mDropDownWidth == -2) {
            int compatMeasureContentWidth = this.this$0.compatMeasureContentWidth((SpinnerAdapter)this.mAdapter, this.getBackground());
            final int n = this.this$0.getContext().getResources().getDisplayMetrics().widthPixels - this.this$0.mTempRect.left - this.this$0.mTempRect.right;
            if (compatMeasureContentWidth > n) {
                compatMeasureContentWidth = n;
            }
            this.setContentWidth(Math.max(compatMeasureContentWidth, width - paddingLeft - paddingRight));
        }
        else if (this.this$0.mDropDownWidth == -1) {
            this.setContentWidth(width - paddingLeft - paddingRight);
        }
        else {
            this.setContentWidth(this.this$0.mDropDownWidth);
        }
        int horizontalOffset;
        if (ViewUtils.isLayoutRtl((View)this.this$0)) {
            horizontalOffset = width - paddingRight - this.getWidth() + right;
        }
        else {
            horizontalOffset = right + paddingLeft;
        }
        this.setHorizontalOffset(horizontalOffset);
    }
    
    public CharSequence getHintText() {
        return this.mHintText;
    }
    
    boolean isVisibleToUser(final View view) {
        return ViewCompat.isAttachedToWindow(view) && view.getGlobalVisibleRect(this.mVisibleRect);
    }
    
    @Override
    public void setAdapter(final ListAdapter listAdapter) {
        super.setAdapter(listAdapter);
        this.mAdapter = listAdapter;
    }
    
    public void setPromptText(final CharSequence mHintText) {
        this.mHintText = mHintText;
    }
    
    @Override
    public void show() {
        final boolean showing = this.isShowing();
        this.computeContentWidth();
        this.setInputMethodMode(2);
        super.show();
        this.getListView().setChoiceMode(1);
        this.setSelection(this.this$0.getSelectedItemPosition());
        if (!showing) {
            final ViewTreeObserver viewTreeObserver = this.this$0.getViewTreeObserver();
            if (viewTreeObserver != null) {
                final AppCompatSpinner$DropdownPopup$2 appCompatSpinner$DropdownPopup$2 = new AppCompatSpinner$DropdownPopup$2(this);
                viewTreeObserver.addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)appCompatSpinner$DropdownPopup$2);
                this.setOnDismissListener((PopupWindow$OnDismissListener)new AppCompatSpinner$DropdownPopup$3(this, (ViewTreeObserver$OnGlobalLayoutListener)appCompatSpinner$DropdownPopup$2));
            }
        }
    }
}
