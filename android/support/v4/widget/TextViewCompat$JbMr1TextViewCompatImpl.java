// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.widget;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

class TextViewCompat$JbMr1TextViewCompatImpl extends TextViewCompat$JbTextViewCompatImpl
{
    @Override
    public Drawable[] getCompoundDrawablesRelative(final TextView textView) {
        return TextViewCompatJbMr1.getCompoundDrawablesRelative(textView);
    }
    
    @Override
    public void setCompoundDrawablesRelative(final TextView textView, final Drawable drawable, final Drawable drawable2, final Drawable drawable3, final Drawable drawable4) {
        TextViewCompatJbMr1.setCompoundDrawablesRelative(textView, drawable, drawable2, drawable3, drawable4);
    }
    
    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(final TextView textView, final int n, final int n2, final int n3, final int n4) {
        TextViewCompatJbMr1.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, n, n2, n3, n4);
    }
    
    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(final TextView textView, final Drawable drawable, final Drawable drawable2, final Drawable drawable3, final Drawable drawable4) {
        TextViewCompatJbMr1.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, drawable, drawable2, drawable3, drawable4);
    }
}
