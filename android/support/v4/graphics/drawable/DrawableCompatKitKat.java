// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;
import android.annotation.TargetApi;

@TargetApi(19)
class DrawableCompatKitKat
{
    public static int getAlpha(final Drawable drawable) {
        return drawable.getAlpha();
    }
    
    public static boolean isAutoMirrored(final Drawable drawable) {
        return drawable.isAutoMirrored();
    }
    
    public static void setAutoMirrored(final Drawable drawable, final boolean autoMirrored) {
        drawable.setAutoMirrored(autoMirrored);
    }
    
    public static Drawable wrapForTinting(final Drawable drawable) {
        Drawable drawable2 = drawable;
        if (!(drawable instanceof TintAwareDrawable)) {
            drawable2 = new DrawableWrapperKitKat(drawable);
        }
        return drawable2;
    }
}
