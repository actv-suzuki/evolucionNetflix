// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.view.accessibility;

class AccessibilityNodeInfoCompat$AccessibilityNodeInfoApi24Impl extends AccessibilityNodeInfoCompat$AccessibilityNodeInfoApi23Impl
{
    @Override
    public Object getActionSetProgress() {
        return AccessibilityNodeInfoCompatApi24.getActionSetProgress();
    }
    
    @Override
    public int getDrawingOrder(final Object o) {
        return AccessibilityNodeInfoCompatApi24.getDrawingOrder(o);
    }
    
    @Override
    public boolean isImportantForAccessibility(final Object o) {
        return AccessibilityNodeInfoCompatApi24.isImportantForAccessibility(o);
    }
    
    @Override
    public void setDrawingOrder(final Object o, final int n) {
        AccessibilityNodeInfoCompatApi24.setDrawingOrder(o, n);
    }
    
    @Override
    public void setImportantForAccessibility(final Object o, final boolean b) {
        AccessibilityNodeInfoCompatApi24.setImportantForAccessibility(o, b);
    }
}
