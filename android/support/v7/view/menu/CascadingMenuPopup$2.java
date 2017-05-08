// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v7.view.menu;

import android.os.SystemClock;
import android.view.MenuItem;
import android.support.v7.widget.MenuItemHoverListener;

class CascadingMenuPopup$2 implements MenuItemHoverListener
{
    final /* synthetic */ CascadingMenuPopup this$0;
    
    CascadingMenuPopup$2(final CascadingMenuPopup this$0) {
        this.this$0 = this$0;
    }
    
    @Override
    public void onItemHoverEnter(final MenuBuilder menuBuilder, final MenuItem menuItem) {
        this.this$0.mSubMenuHoverHandler.removeCallbacksAndMessages((Object)null);
        int i = 0;
        while (true) {
            while (i < this.this$0.mShowingMenus.size()) {
                if (menuBuilder == this.this$0.mShowingMenus.get(i).menu) {
                    if (i == -1) {
                        return;
                    }
                    final int n = i + 1;
                    CascadingMenuPopup$CascadingMenuInfo cascadingMenuPopup$CascadingMenuInfo;
                    if (n < this.this$0.mShowingMenus.size()) {
                        cascadingMenuPopup$CascadingMenuInfo = this.this$0.mShowingMenus.get(n);
                    }
                    else {
                        cascadingMenuPopup$CascadingMenuInfo = null;
                    }
                    this.this$0.mSubMenuHoverHandler.postAtTime((Runnable)new CascadingMenuPopup$2$1(this, cascadingMenuPopup$CascadingMenuInfo, menuItem, menuBuilder), (Object)menuBuilder, SystemClock.uptimeMillis() + 200L);
                    return;
                }
                else {
                    ++i;
                }
            }
            i = -1;
            continue;
        }
    }
    
    @Override
    public void onItemHoverExit(final MenuBuilder menuBuilder, final MenuItem menuItem) {
        this.this$0.mSubMenuHoverHandler.removeCallbacksAndMessages((Object)menuBuilder);
    }
}
