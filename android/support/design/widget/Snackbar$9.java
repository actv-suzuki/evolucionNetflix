// 
// Decompiled by Procyon v0.5.30
// 

package android.support.design.widget;

import android.view.View;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;

class Snackbar$9 extends ViewPropertyAnimatorListenerAdapter
{
    final /* synthetic */ Snackbar this$0;
    final /* synthetic */ int val$event;
    
    Snackbar$9(final Snackbar this$0, final int val$event) {
        this.this$0 = this$0;
        this.val$event = val$event;
    }
    
    @Override
    public void onAnimationEnd(final View view) {
        this.this$0.onViewHidden(this.val$event);
    }
    
    @Override
    public void onAnimationStart(final View view) {
        this.this$0.mView.animateChildrenOut(0, 180);
    }
}
