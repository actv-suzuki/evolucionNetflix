// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.widget;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.os.Parcelable;
import android.view.View$MeasureSpec;
import android.view.KeyEvent;
import android.support.v4.view.MotionEventCompat;
import android.view.ViewGroup$MarginLayoutParams;
import android.support.v4.view.GravityCompat;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.os.SystemClock;
import android.view.ViewGroup$LayoutParams;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.graphics.Paint;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import android.view.ViewGroup;

public class DrawerLayout extends ViewGroup implements DrawerLayoutImpl
{
    private static final boolean ALLOW_EDGE_LOCK = false;
    static final boolean CAN_HIDE_DESCENDANTS;
    private static final boolean CHILDREN_DISALLOW_INTERCEPT = true;
    private static final int DEFAULT_SCRIM_COLOR = -1728053248;
    private static final int DRAWER_ELEVATION = 10;
    static final DrawerLayout$DrawerLayoutCompatImpl IMPL;
    static final int[] LAYOUT_ATTRS;
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_LOCKED_OPEN = 2;
    public static final int LOCK_MODE_UNDEFINED = 3;
    public static final int LOCK_MODE_UNLOCKED = 0;
    private static final int MIN_DRAWER_MARGIN = 64;
    private static final int MIN_FLING_VELOCITY = 400;
    private static final int PEEK_DELAY = 160;
    private static final boolean SET_DRAWER_SHADOW_FROM_ELEVATION;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    private static final String TAG = "DrawerLayout";
    private static final float TOUCH_SLOP_SENSITIVITY = 1.0f;
    private final DrawerLayout$ChildAccessibilityDelegate mChildAccessibilityDelegate;
    private boolean mChildrenCanceledTouch;
    private boolean mDisallowInterceptRequested;
    private boolean mDrawStatusBarBackground;
    private float mDrawerElevation;
    private int mDrawerState;
    private boolean mFirstLayout;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private Object mLastInsets;
    private final DrawerLayout$ViewDragCallback mLeftCallback;
    private final ViewDragHelper mLeftDragger;
    private DrawerLayout$DrawerListener mListener;
    private List<DrawerLayout$DrawerListener> mListeners;
    private int mLockModeEnd;
    private int mLockModeLeft;
    private int mLockModeRight;
    private int mLockModeStart;
    private int mMinDrawerMargin;
    private final ArrayList<View> mNonDrawerViews;
    private final DrawerLayout$ViewDragCallback mRightCallback;
    private final ViewDragHelper mRightDragger;
    private int mScrimColor;
    private float mScrimOpacity;
    private Paint mScrimPaint;
    private Drawable mShadowEnd;
    private Drawable mShadowLeft;
    private Drawable mShadowLeftResolved;
    private Drawable mShadowRight;
    private Drawable mShadowRightResolved;
    private Drawable mShadowStart;
    private Drawable mStatusBarBackground;
    private CharSequence mTitleLeft;
    private CharSequence mTitleRight;
    
    static {
        final boolean b = true;
        LAYOUT_ATTRS = new int[] { 16842931 };
        CAN_HIDE_DESCENDANTS = (Build$VERSION.SDK_INT >= 19);
        SET_DRAWER_SHADOW_FROM_ELEVATION = (Build$VERSION.SDK_INT >= 21 && b);
        if (Build$VERSION.SDK_INT >= 21) {
            IMPL = new DrawerLayout$DrawerLayoutCompatImplApi21();
            return;
        }
        IMPL = new DrawerLayout$DrawerLayoutCompatImplBase();
    }
    
    public DrawerLayout(final Context context) {
        this(context, null);
    }
    
    public DrawerLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public DrawerLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mChildAccessibilityDelegate = new DrawerLayout$ChildAccessibilityDelegate(this);
        this.mScrimColor = -1728053248;
        this.mScrimPaint = new Paint();
        this.mFirstLayout = true;
        this.mLockModeLeft = 3;
        this.mLockModeRight = 3;
        this.mLockModeStart = 3;
        this.mLockModeEnd = 3;
        this.mShadowStart = null;
        this.mShadowEnd = null;
        this.mShadowLeft = null;
        this.mShadowRight = null;
        this.setDescendantFocusability(262144);
        final float density = this.getResources().getDisplayMetrics().density;
        this.mMinDrawerMargin = (int)(64.0f * density + 0.5f);
        final float n2 = 400.0f * density;
        this.mLeftCallback = new DrawerLayout$ViewDragCallback(this, 3);
        this.mRightCallback = new DrawerLayout$ViewDragCallback(this, 5);
        (this.mLeftDragger = ViewDragHelper.create(this, 1.0f, this.mLeftCallback)).setEdgeTrackingEnabled(1);
        this.mLeftDragger.setMinVelocity(n2);
        this.mLeftCallback.setDragger(this.mLeftDragger);
        (this.mRightDragger = ViewDragHelper.create(this, 1.0f, this.mRightCallback)).setEdgeTrackingEnabled(2);
        this.mRightDragger.setMinVelocity(n2);
        this.mRightCallback.setDragger(this.mRightDragger);
        this.setFocusableInTouchMode(true);
        ViewCompat.setImportantForAccessibility((View)this, 1);
        ViewCompat.setAccessibilityDelegate((View)this, new DrawerLayout$AccessibilityDelegate(this));
        ViewGroupCompat.setMotionEventSplittingEnabled(this, false);
        if (ViewCompat.getFitsSystemWindows((View)this)) {
            DrawerLayout.IMPL.configureApplyInsets((View)this);
            this.mStatusBarBackground = DrawerLayout.IMPL.getDefaultStatusBarBackground(context);
        }
        this.mDrawerElevation = density * 10.0f;
        this.mNonDrawerViews = new ArrayList<View>();
    }
    
    static String gravityToString(final int n) {
        if ((n & 0x3) == 0x3) {
            return "LEFT";
        }
        if ((n & 0x5) == 0x5) {
            return "RIGHT";
        }
        return Integer.toHexString(n);
    }
    
    private static boolean hasOpaqueBackground(final View view) {
        final boolean b = false;
        final Drawable background = view.getBackground();
        boolean b2 = b;
        if (background != null) {
            b2 = b;
            if (background.getOpacity() == -1) {
                b2 = true;
            }
        }
        return b2;
    }
    
    private boolean hasPeekingDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            if (((DrawerLayout$LayoutParams)this.getChildAt(i).getLayoutParams()).isPeeking) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasVisibleDrawer() {
        return this.findVisibleDrawer() != null;
    }
    
    static boolean includeChildForAccessibility(final View view) {
        return ViewCompat.getImportantForAccessibility(view) != 4 && ViewCompat.getImportantForAccessibility(view) != 2;
    }
    
    private boolean mirror(final Drawable drawable, final int n) {
        if (drawable == null || !DrawableCompat.isAutoMirrored(drawable)) {
            return false;
        }
        DrawableCompat.setLayoutDirection(drawable, n);
        return true;
    }
    
    private Drawable resolveLeftShadow() {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        if (layoutDirection == 0) {
            if (this.mShadowStart != null) {
                this.mirror(this.mShadowStart, layoutDirection);
                return this.mShadowStart;
            }
        }
        else if (this.mShadowEnd != null) {
            this.mirror(this.mShadowEnd, layoutDirection);
            return this.mShadowEnd;
        }
        return this.mShadowLeft;
    }
    
    private Drawable resolveRightShadow() {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        if (layoutDirection == 0) {
            if (this.mShadowEnd != null) {
                this.mirror(this.mShadowEnd, layoutDirection);
                return this.mShadowEnd;
            }
        }
        else if (this.mShadowStart != null) {
            this.mirror(this.mShadowStart, layoutDirection);
            return this.mShadowStart;
        }
        return this.mShadowRight;
    }
    
    private void resolveShadowDrawables() {
        if (DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION) {
            return;
        }
        this.mShadowLeftResolved = this.resolveLeftShadow();
        this.mShadowRightResolved = this.resolveRightShadow();
    }
    
    private void updateChildrenImportantForAccessibility(final View view, final boolean b) {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if ((!b && !this.isDrawerView(child)) || (b && child == view)) {
                ViewCompat.setImportantForAccessibility(child, 1);
            }
            else {
                ViewCompat.setImportantForAccessibility(child, 4);
            }
        }
    }
    
    public void addDrawerListener(final DrawerLayout$DrawerListener drawerLayout$DrawerListener) {
        if (drawerLayout$DrawerListener == null) {
            return;
        }
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<DrawerLayout$DrawerListener>();
        }
        this.mListeners.add(drawerLayout$DrawerListener);
    }
    
    public void addFocusables(final ArrayList<View> list, final int n, final int n2) {
        final int n3 = 0;
        if (this.getDescendantFocusability() == 393216) {
            return;
        }
        final int childCount = this.getChildCount();
        int i = 0;
        boolean b = false;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            if (this.isDrawerView(child)) {
                if (this.isDrawerOpen(child)) {
                    b = true;
                    child.addFocusables((ArrayList)list, n, n2);
                }
            }
            else {
                this.mNonDrawerViews.add(child);
            }
            ++i;
        }
        if (!b) {
            for (int size = this.mNonDrawerViews.size(), j = n3; j < size; ++j) {
                final View view = this.mNonDrawerViews.get(j);
                if (view.getVisibility() == 0) {
                    view.addFocusables((ArrayList)list, n, n2);
                }
            }
        }
        this.mNonDrawerViews.clear();
    }
    
    public void addView(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        super.addView(view, n, viewGroup$LayoutParams);
        if (this.findOpenDrawer() != null || this.isDrawerView(view)) {
            ViewCompat.setImportantForAccessibility(view, 4);
        }
        else {
            ViewCompat.setImportantForAccessibility(view, 1);
        }
        if (!DrawerLayout.CAN_HIDE_DESCENDANTS) {
            ViewCompat.setAccessibilityDelegate(view, this.mChildAccessibilityDelegate);
        }
    }
    
    void cancelChildViewTouch() {
        int i = 0;
        if (!this.mChildrenCanceledTouch) {
            final long uptimeMillis = SystemClock.uptimeMillis();
            final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
            while (i < this.getChildCount()) {
                this.getChildAt(i).dispatchTouchEvent(obtain);
                ++i;
            }
            obtain.recycle();
            this.mChildrenCanceledTouch = true;
        }
    }
    
    boolean checkDrawerViewAbsoluteGravity(final View view, final int n) {
        return (this.getDrawerViewAbsoluteGravity(view) & n) == n;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof DrawerLayout$LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams);
    }
    
    public void closeDrawer(final int n) {
        this.closeDrawer(n, true);
    }
    
    public void closeDrawer(final int n, final boolean b) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        if (drawerWithGravity == null) {
            throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(n));
        }
        this.closeDrawer(drawerWithGravity, b);
    }
    
    public void closeDrawer(final View view) {
        this.closeDrawer(view, true);
    }
    
    public void closeDrawer(final View view, final boolean b) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a sliding drawer");
        }
        final DrawerLayout$LayoutParams drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)view.getLayoutParams();
        if (this.mFirstLayout) {
            drawerLayout$LayoutParams.onScreen = 0.0f;
            drawerLayout$LayoutParams.openState = 0;
        }
        else if (b) {
            drawerLayout$LayoutParams.openState |= 0x4;
            if (this.checkDrawerViewAbsoluteGravity(view, 3)) {
                this.mLeftDragger.smoothSlideViewTo(view, -view.getWidth(), view.getTop());
            }
            else {
                this.mRightDragger.smoothSlideViewTo(view, this.getWidth(), view.getTop());
            }
        }
        else {
            this.moveDrawerToOffset(view, 0.0f);
            this.updateDrawerState(drawerLayout$LayoutParams.gravity, 0, view);
            view.setVisibility(4);
        }
        this.invalidate();
    }
    
    public void closeDrawers() {
        this.closeDrawers(false);
    }
    
    void closeDrawers(final boolean b) {
        final int childCount = this.getChildCount();
        int i = 0;
        boolean b2 = false;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            final DrawerLayout$LayoutParams drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)child.getLayoutParams();
            boolean b3 = b2;
            if (this.isDrawerView(child)) {
                if (b && !drawerLayout$LayoutParams.isPeeking) {
                    b3 = b2;
                }
                else {
                    final int width = child.getWidth();
                    boolean b4;
                    if (this.checkDrawerViewAbsoluteGravity(child, 3)) {
                        b4 = (b2 | this.mLeftDragger.smoothSlideViewTo(child, -width, child.getTop()));
                    }
                    else {
                        b4 = (b2 | this.mRightDragger.smoothSlideViewTo(child, this.getWidth(), child.getTop()));
                    }
                    drawerLayout$LayoutParams.isPeeking = false;
                    b3 = b4;
                }
            }
            ++i;
            b2 = b3;
        }
        this.mLeftCallback.removeCallbacks();
        this.mRightCallback.removeCallbacks();
        if (b2) {
            this.invalidate();
        }
    }
    
    public void computeScroll() {
        final int childCount = this.getChildCount();
        float max = 0.0f;
        for (int i = 0; i < childCount; ++i) {
            max = Math.max(max, ((DrawerLayout$LayoutParams)this.getChildAt(i).getLayoutParams()).onScreen);
        }
        this.mScrimOpacity = max;
        if (this.mLeftDragger.continueSettling(true) | this.mRightDragger.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    void dispatchOnDrawerClosed(View rootView) {
        final DrawerLayout$LayoutParams drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)rootView.getLayoutParams();
        if ((drawerLayout$LayoutParams.openState & 0x1) == 0x1) {
            drawerLayout$LayoutParams.openState = 0;
            if (this.mListeners != null) {
                for (int i = this.mListeners.size() - 1; i >= 0; --i) {
                    this.mListeners.get(i).onDrawerClosed(rootView);
                }
            }
            this.updateChildrenImportantForAccessibility(rootView, false);
            if (this.hasWindowFocus()) {
                rootView = this.getRootView();
                if (rootView != null) {
                    rootView.sendAccessibilityEvent(32);
                }
            }
        }
    }
    
    void dispatchOnDrawerOpened(final View view) {
        final DrawerLayout$LayoutParams drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)view.getLayoutParams();
        if ((drawerLayout$LayoutParams.openState & 0x1) == 0x0) {
            drawerLayout$LayoutParams.openState = 1;
            if (this.mListeners != null) {
                for (int i = this.mListeners.size() - 1; i >= 0; --i) {
                    this.mListeners.get(i).onDrawerOpened(view);
                }
            }
            this.updateChildrenImportantForAccessibility(view, true);
            if (this.hasWindowFocus()) {
                this.sendAccessibilityEvent(32);
            }
        }
    }
    
    void dispatchOnDrawerSlide(final View view, final float n) {
        if (this.mListeners != null) {
            for (int i = this.mListeners.size() - 1; i >= 0; --i) {
                this.mListeners.get(i).onDrawerSlide(view, n);
            }
        }
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        final int height = this.getHeight();
        final boolean contentView = this.isContentView(view);
        int n2 = 0;
        final int n3 = 0;
        int width = this.getWidth();
        final int save = canvas.save();
        int n4 = width;
        if (contentView) {
            final int childCount = this.getChildCount();
            int i = 0;
            n2 = n3;
        Label_0117_Outer:
            while (i < childCount) {
                final View child = this.getChildAt(i);
                while (true) {
                    Label_0191: {
                        if (child == view || child.getVisibility() != 0 || !hasOpaqueBackground(child) || !this.isDrawerView(child)) {
                            break Label_0191;
                        }
                        int n5;
                        int left;
                        if (child.getHeight() < height) {
                            n5 = n2;
                            left = width;
                        }
                        else if (this.checkDrawerViewAbsoluteGravity(child, 3)) {
                            int right = child.getRight();
                            if (right <= n2) {
                                right = n2;
                            }
                            n5 = right;
                            left = width;
                        }
                        else {
                            left = child.getLeft();
                            n5 = n2;
                            if (left >= width) {
                                break Label_0191;
                            }
                        }
                        ++i;
                        width = left;
                        n2 = n5;
                        continue Label_0117_Outer;
                    }
                    int left = width;
                    int n5 = n2;
                    continue;
                }
            }
            canvas.clipRect(n2, 0, width, this.getHeight());
            n4 = width;
        }
        final boolean drawChild = super.drawChild(canvas, view, n);
        canvas.restoreToCount(save);
        if (this.mScrimOpacity > 0.0f && contentView) {
            this.mScrimPaint.setColor((int)(((this.mScrimColor & 0xFF000000) >>> 24) * this.mScrimOpacity) << 24 | (this.mScrimColor & 0xFFFFFF));
            canvas.drawRect((float)n2, 0.0f, (float)n4, (float)this.getHeight(), this.mScrimPaint);
        }
        else {
            if (this.mShadowLeftResolved != null && this.checkDrawerViewAbsoluteGravity(view, 3)) {
                final int intrinsicWidth = this.mShadowLeftResolved.getIntrinsicWidth();
                final int right2 = view.getRight();
                final float max = Math.max(0.0f, Math.min(right2 / this.mLeftDragger.getEdgeSize(), 1.0f));
                this.mShadowLeftResolved.setBounds(right2, view.getTop(), intrinsicWidth + right2, view.getBottom());
                this.mShadowLeftResolved.setAlpha((int)(255.0f * max));
                this.mShadowLeftResolved.draw(canvas);
                return drawChild;
            }
            if (this.mShadowRightResolved != null && this.checkDrawerViewAbsoluteGravity(view, 5)) {
                final int intrinsicWidth2 = this.mShadowRightResolved.getIntrinsicWidth();
                final int left2 = view.getLeft();
                final float max2 = Math.max(0.0f, Math.min((this.getWidth() - left2) / this.mRightDragger.getEdgeSize(), 1.0f));
                this.mShadowRightResolved.setBounds(left2 - intrinsicWidth2, view.getTop(), left2, view.getBottom());
                this.mShadowRightResolved.setAlpha((int)(255.0f * max2));
                this.mShadowRightResolved.draw(canvas);
                return drawChild;
            }
        }
        return drawChild;
    }
    
    View findDrawerWithGravity(int i) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection((View)this));
        int childCount;
        View child;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if ((this.getDrawerViewAbsoluteGravity(child) & 0x7) == (absoluteGravity & 0x7)) {
                return child;
            }
        }
        return null;
    }
    
    View findOpenDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if ((((DrawerLayout$LayoutParams)child.getLayoutParams()).openState & 0x1) == 0x1) {
                return child;
            }
        }
        return null;
    }
    
    View findVisibleDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (this.isDrawerView(child) && this.isDrawerVisible(child)) {
                return child;
            }
        }
        return null;
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup$LayoutParams)new DrawerLayout$LayoutParams(-1, -1);
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return (ViewGroup$LayoutParams)new DrawerLayout$LayoutParams(this.getContext(), set);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams instanceof DrawerLayout$LayoutParams) {
            return (ViewGroup$LayoutParams)new DrawerLayout$LayoutParams((DrawerLayout$LayoutParams)viewGroup$LayoutParams);
        }
        if (viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams) {
            return (ViewGroup$LayoutParams)new DrawerLayout$LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
        }
        return (ViewGroup$LayoutParams)new DrawerLayout$LayoutParams(viewGroup$LayoutParams);
    }
    
    public float getDrawerElevation() {
        if (DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION) {
            return this.mDrawerElevation;
        }
        return 0.0f;
    }
    
    public int getDrawerLockMode(int n) {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        switch (n) {
            case 3: {
                if (this.mLockModeLeft != 3) {
                    return this.mLockModeLeft;
                }
                if (layoutDirection == 0) {
                    n = this.mLockModeStart;
                }
                else {
                    n = this.mLockModeEnd;
                }
                if (n != 3) {
                    return n;
                }
                break;
            }
            case 5: {
                if (this.mLockModeRight != 3) {
                    return this.mLockModeRight;
                }
                if (layoutDirection == 0) {
                    n = this.mLockModeEnd;
                }
                else {
                    n = this.mLockModeStart;
                }
                if (n != 3) {
                    return n;
                }
                break;
            }
            case 8388611: {
                if (this.mLockModeStart != 3) {
                    return this.mLockModeStart;
                }
                if (layoutDirection == 0) {
                    n = this.mLockModeLeft;
                }
                else {
                    n = this.mLockModeRight;
                }
                if (n != 3) {
                    return n;
                }
                break;
            }
            case 8388613: {
                if (this.mLockModeEnd != 3) {
                    return this.mLockModeEnd;
                }
                if (layoutDirection == 0) {
                    n = this.mLockModeRight;
                }
                else {
                    n = this.mLockModeLeft;
                }
                if (n != 3) {
                    return n;
                }
                break;
            }
        }
        return 0;
    }
    
    public int getDrawerLockMode(final View view) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a drawer");
        }
        return this.getDrawerLockMode(((DrawerLayout$LayoutParams)view.getLayoutParams()).gravity);
    }
    
    public CharSequence getDrawerTitle(int absoluteGravity) {
        absoluteGravity = GravityCompat.getAbsoluteGravity(absoluteGravity, ViewCompat.getLayoutDirection((View)this));
        if (absoluteGravity == 3) {
            return this.mTitleLeft;
        }
        if (absoluteGravity == 5) {
            return this.mTitleRight;
        }
        return null;
    }
    
    int getDrawerViewAbsoluteGravity(final View view) {
        return GravityCompat.getAbsoluteGravity(((DrawerLayout$LayoutParams)view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection((View)this));
    }
    
    float getDrawerViewOffset(final View view) {
        return ((DrawerLayout$LayoutParams)view.getLayoutParams()).onScreen;
    }
    
    public Drawable getStatusBarBackgroundDrawable() {
        return this.mStatusBarBackground;
    }
    
    boolean isContentView(final View view) {
        return ((DrawerLayout$LayoutParams)view.getLayoutParams()).gravity == 0;
    }
    
    public boolean isDrawerOpen(final int n) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        return drawerWithGravity != null && this.isDrawerOpen(drawerWithGravity);
    }
    
    public boolean isDrawerOpen(final View view) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a drawer");
        }
        return (((DrawerLayout$LayoutParams)view.getLayoutParams()).openState & 0x1) == 0x1;
    }
    
    boolean isDrawerView(final View view) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(((DrawerLayout$LayoutParams)view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(view));
        return (absoluteGravity & 0x3) != 0x0 || (absoluteGravity & 0x5) != 0x0;
    }
    
    public boolean isDrawerVisible(final int n) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        return drawerWithGravity != null && this.isDrawerVisible(drawerWithGravity);
    }
    
    public boolean isDrawerVisible(final View view) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a drawer");
        }
        return ((DrawerLayout$LayoutParams)view.getLayoutParams()).onScreen > 0.0f;
    }
    
    void moveDrawerToOffset(final View view, final float n) {
        final float drawerViewOffset = this.getDrawerViewOffset(view);
        final int width = view.getWidth();
        int n2 = (int)(width * n) - (int)(drawerViewOffset * width);
        if (!this.checkDrawerViewAbsoluteGravity(view, 3)) {
            n2 = -n2;
        }
        view.offsetLeftAndRight(n2);
        this.setDrawerViewOffset(view, n);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
            final int topInset = DrawerLayout.IMPL.getTopInset(this.mLastInsets);
            if (topInset > 0) {
                this.mStatusBarBackground.setBounds(0, 0, this.getWidth(), topInset);
                this.mStatusBarBackground.draw(canvas);
            }
        }
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        boolean b = false;
        final int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        final boolean shouldInterceptTouchEvent = this.mLeftDragger.shouldInterceptTouchEvent(motionEvent);
        final boolean shouldInterceptTouchEvent2 = this.mRightDragger.shouldInterceptTouchEvent(motionEvent);
        boolean b2 = false;
        Label_0063: {
            switch (actionMasked) {
                case 0: {
                    final float x = motionEvent.getX();
                    final float y = motionEvent.getY();
                    this.mInitialMotionX = x;
                    this.mInitialMotionY = y;
                    while (true) {
                        Label_0214: {
                            if (this.mScrimOpacity <= 0.0f) {
                                break Label_0214;
                            }
                            final View topChildUnder = this.mLeftDragger.findTopChildUnder((int)x, (int)y);
                            if (topChildUnder == null || !this.isContentView(topChildUnder)) {
                                break Label_0214;
                            }
                            b2 = true;
                            this.mDisallowInterceptRequested = false;
                            this.mChildrenCanceledTouch = false;
                            break Label_0063;
                        }
                        b2 = false;
                        continue;
                    }
                }
                case 2: {
                    if (this.mLeftDragger.checkTouchSlop(3)) {
                        this.mLeftCallback.removeCallbacks();
                        this.mRightCallback.removeCallbacks();
                        b2 = false;
                        break Label_0063;
                    }
                    break;
                }
                case 1:
                case 3: {
                    this.closeDrawers(true);
                    this.mDisallowInterceptRequested = false;
                    this.mChildrenCanceledTouch = false;
                    break;
                }
            }
            b2 = false;
        }
        if ((shouldInterceptTouchEvent | shouldInterceptTouchEvent2) || b2 || this.hasPeekingDrawer() || this.mChildrenCanceledTouch) {
            b = true;
        }
        return b;
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (n == 4 && this.hasVisibleDrawer()) {
            keyEvent.startTracking();
            return true;
        }
        return super.onKeyDown(n, keyEvent);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        if (n == 4) {
            final View visibleDrawer = this.findVisibleDrawer();
            if (visibleDrawer != null && this.getDrawerLockMode(visibleDrawer) == 0) {
                this.closeDrawers();
            }
            return visibleDrawer != null;
        }
        return super.onKeyUp(n, keyEvent);
    }
    
    protected void onLayout(final boolean b, int topMargin, final int n, int i, final int n2) {
        this.mInLayout = true;
        final int n3 = i - topMargin;
        int childCount;
        View child;
        DrawerLayout$LayoutParams drawerLayout$LayoutParams;
        int measuredWidth;
        int measuredHeight;
        int n4;
        float n5;
        int n6;
        int n7;
        int n8;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)child.getLayoutParams();
                if (this.isContentView(child)) {
                    child.layout(drawerLayout$LayoutParams.leftMargin, drawerLayout$LayoutParams.topMargin, drawerLayout$LayoutParams.leftMargin + child.getMeasuredWidth(), drawerLayout$LayoutParams.topMargin + child.getMeasuredHeight());
                }
                else {
                    measuredWidth = child.getMeasuredWidth();
                    measuredHeight = child.getMeasuredHeight();
                    if (this.checkDrawerViewAbsoluteGravity(child, 3)) {
                        topMargin = -measuredWidth;
                        n4 = (int)(measuredWidth * drawerLayout$LayoutParams.onScreen) + topMargin;
                        n5 = (measuredWidth + n4) / measuredWidth;
                    }
                    else {
                        n4 = n3 - (int)(measuredWidth * drawerLayout$LayoutParams.onScreen);
                        n5 = (n3 - n4) / measuredWidth;
                    }
                    if (n5 != drawerLayout$LayoutParams.onScreen) {
                        n6 = 1;
                    }
                    else {
                        n6 = 0;
                    }
                    switch (drawerLayout$LayoutParams.gravity & 0x70) {
                        default: {
                            child.layout(n4, drawerLayout$LayoutParams.topMargin, measuredWidth + n4, measuredHeight + drawerLayout$LayoutParams.topMargin);
                            break;
                        }
                        case 80: {
                            topMargin = n2 - n;
                            child.layout(n4, topMargin - drawerLayout$LayoutParams.bottomMargin - child.getMeasuredHeight(), measuredWidth + n4, topMargin - drawerLayout$LayoutParams.bottomMargin);
                            break;
                        }
                        case 16: {
                            n7 = n2 - n;
                            n8 = (n7 - measuredHeight) / 2;
                            if (n8 < drawerLayout$LayoutParams.topMargin) {
                                topMargin = drawerLayout$LayoutParams.topMargin;
                            }
                            else {
                                topMargin = n8;
                                if (n8 + measuredHeight > n7 - drawerLayout$LayoutParams.bottomMargin) {
                                    topMargin = n7 - drawerLayout$LayoutParams.bottomMargin - measuredHeight;
                                }
                            }
                            child.layout(n4, topMargin, measuredWidth + n4, measuredHeight + topMargin);
                            break;
                        }
                    }
                    if (n6 != 0) {
                        this.setDrawerViewOffset(child, n5);
                    }
                    if (drawerLayout$LayoutParams.onScreen > 0.0f) {
                        topMargin = 0;
                    }
                    else {
                        topMargin = 4;
                    }
                    if (child.getVisibility() != topMargin) {
                        child.setVisibility(topMargin);
                    }
                }
            }
        }
        this.mInLayout = false;
        this.mFirstLayout = false;
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int mode = View$MeasureSpec.getMode(n);
        final int mode2 = View$MeasureSpec.getMode(n2);
        int size = View$MeasureSpec.getSize(n);
        final int size2 = View$MeasureSpec.getSize(n2);
        while (true) {
            Label_0581: {
                if (mode == 1073741824) {
                    final int n3 = size;
                    if (mode2 == 1073741824) {
                        break Label_0581;
                    }
                }
                if (!this.isInEditMode()) {
                    throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
                }
                if (mode != Integer.MIN_VALUE && mode == 0) {
                    size = 300;
                }
                int n3;
                int n4;
                if (mode2 == Integer.MIN_VALUE) {
                    n3 = size;
                    n4 = size2;
                }
                else {
                    n3 = size;
                    if (mode2 != 0) {
                        break Label_0581;
                    }
                    n3 = size;
                    n4 = 300;
                }
                this.setMeasuredDimension(n3, n4);
                final boolean b = this.mLastInsets != null && ViewCompat.getFitsSystemWindows((View)this);
                final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
                int n5 = 0;
                int n6 = 0;
                for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                    final View child = this.getChildAt(i);
                    if (child.getVisibility() != 8) {
                        final DrawerLayout$LayoutParams drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)child.getLayoutParams();
                        if (b) {
                            final int absoluteGravity = GravityCompat.getAbsoluteGravity(drawerLayout$LayoutParams.gravity, layoutDirection);
                            if (ViewCompat.getFitsSystemWindows(child)) {
                                DrawerLayout.IMPL.dispatchChildInsets(child, this.mLastInsets, absoluteGravity);
                            }
                            else {
                                DrawerLayout.IMPL.applyMarginInsets(drawerLayout$LayoutParams, this.mLastInsets, absoluteGravity);
                            }
                        }
                        if (this.isContentView(child)) {
                            child.measure(View$MeasureSpec.makeMeasureSpec(n3 - drawerLayout$LayoutParams.leftMargin - drawerLayout$LayoutParams.rightMargin, 1073741824), View$MeasureSpec.makeMeasureSpec(n4 - drawerLayout$LayoutParams.topMargin - drawerLayout$LayoutParams.bottomMargin, 1073741824));
                        }
                        else {
                            if (!this.isDrawerView(child)) {
                                throw new IllegalStateException("Child " + child + " at index " + i + " does not have a valid layout_gravity - must be Gravity.LEFT, " + "Gravity.RIGHT or Gravity.NO_GRAVITY");
                            }
                            if (DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION && ViewCompat.getElevation(child) != this.mDrawerElevation) {
                                ViewCompat.setElevation(child, this.mDrawerElevation);
                            }
                            final int n7 = this.getDrawerViewAbsoluteGravity(child) & 0x7;
                            boolean b2;
                            if (n7 == 3) {
                                b2 = true;
                            }
                            else {
                                b2 = false;
                            }
                            if ((b2 && n5 != 0) || (!b2 && n6 != 0)) {
                                throw new IllegalStateException("Child drawer has absolute gravity " + gravityToString(n7) + " but this " + "DrawerLayout" + " already has a " + "drawer view along that edge");
                            }
                            if (b2) {
                                n5 = 1;
                            }
                            else {
                                n6 = 1;
                            }
                            child.measure(getChildMeasureSpec(n, this.mMinDrawerMargin + drawerLayout$LayoutParams.leftMargin + drawerLayout$LayoutParams.rightMargin, drawerLayout$LayoutParams.width), getChildMeasureSpec(n2, drawerLayout$LayoutParams.topMargin + drawerLayout$LayoutParams.bottomMargin, drawerLayout$LayoutParams.height));
                        }
                    }
                }
                return;
            }
            int n4 = size2;
            continue;
        }
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof DrawerLayout$SavedState)) {
            super.onRestoreInstanceState(parcelable);
        }
        else {
            final DrawerLayout$SavedState drawerLayout$SavedState = (DrawerLayout$SavedState)parcelable;
            super.onRestoreInstanceState(drawerLayout$SavedState.getSuperState());
            if (drawerLayout$SavedState.openDrawerGravity != 0) {
                final View drawerWithGravity = this.findDrawerWithGravity(drawerLayout$SavedState.openDrawerGravity);
                if (drawerWithGravity != null) {
                    this.openDrawer(drawerWithGravity);
                }
            }
            if (drawerLayout$SavedState.lockModeLeft != 3) {
                this.setDrawerLockMode(drawerLayout$SavedState.lockModeLeft, 3);
            }
            if (drawerLayout$SavedState.lockModeRight != 3) {
                this.setDrawerLockMode(drawerLayout$SavedState.lockModeRight, 5);
            }
            if (drawerLayout$SavedState.lockModeStart != 3) {
                this.setDrawerLockMode(drawerLayout$SavedState.lockModeStart, 8388611);
            }
            if (drawerLayout$SavedState.lockModeEnd != 3) {
                this.setDrawerLockMode(drawerLayout$SavedState.lockModeEnd, 8388613);
            }
        }
    }
    
    public void onRtlPropertiesChanged(final int n) {
        this.resolveShadowDrawables();
    }
    
    protected Parcelable onSaveInstanceState() {
        final DrawerLayout$SavedState drawerLayout$SavedState = new DrawerLayout$SavedState(super.onSaveInstanceState());
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final DrawerLayout$LayoutParams drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)this.getChildAt(i).getLayoutParams();
            boolean b;
            if (drawerLayout$LayoutParams.openState == 1) {
                b = true;
            }
            else {
                b = false;
            }
            boolean b2;
            if (drawerLayout$LayoutParams.openState == 2) {
                b2 = true;
            }
            else {
                b2 = false;
            }
            if (b || b2) {
                drawerLayout$SavedState.openDrawerGravity = drawerLayout$LayoutParams.gravity;
                break;
            }
        }
        drawerLayout$SavedState.lockModeLeft = this.mLockModeLeft;
        drawerLayout$SavedState.lockModeRight = this.mLockModeRight;
        drawerLayout$SavedState.lockModeStart = this.mLockModeStart;
        drawerLayout$SavedState.lockModeEnd = this.mLockModeEnd;
        return (Parcelable)drawerLayout$SavedState;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        this.mLeftDragger.processTouchEvent(motionEvent);
        this.mRightDragger.processTouchEvent(motionEvent);
        switch (motionEvent.getAction() & 0xFF) {
            default: {
                return true;
            }
            case 0: {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                return true;
            }
            case 1: {
                final float x2 = motionEvent.getX();
                final float y2 = motionEvent.getY();
                final View topChildUnder = this.mLeftDragger.findTopChildUnder((int)x2, (int)y2);
                while (true) {
                    Label_0221: {
                        if (topChildUnder == null || !this.isContentView(topChildUnder)) {
                            break Label_0221;
                        }
                        final float n = x2 - this.mInitialMotionX;
                        final float n2 = y2 - this.mInitialMotionY;
                        final int touchSlop = this.mLeftDragger.getTouchSlop();
                        if (n * n + n2 * n2 >= touchSlop * touchSlop) {
                            break Label_0221;
                        }
                        final View openDrawer = this.findOpenDrawer();
                        if (openDrawer == null) {
                            break Label_0221;
                        }
                        final boolean b = this.getDrawerLockMode(openDrawer) == 2;
                        this.closeDrawers(b);
                        this.mDisallowInterceptRequested = false;
                        return true;
                    }
                    final boolean b = true;
                    continue;
                }
            }
            case 3: {
                this.closeDrawers(true);
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                return true;
            }
        }
    }
    
    public void openDrawer(final int n) {
        this.openDrawer(n, true);
    }
    
    public void openDrawer(final int n, final boolean b) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        if (drawerWithGravity == null) {
            throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(n));
        }
        this.openDrawer(drawerWithGravity, b);
    }
    
    public void openDrawer(final View view) {
        this.openDrawer(view, true);
    }
    
    public void openDrawer(final View view, final boolean b) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a sliding drawer");
        }
        final DrawerLayout$LayoutParams drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)view.getLayoutParams();
        if (this.mFirstLayout) {
            drawerLayout$LayoutParams.onScreen = 1.0f;
            drawerLayout$LayoutParams.openState = 1;
            this.updateChildrenImportantForAccessibility(view, true);
        }
        else if (b) {
            drawerLayout$LayoutParams.openState |= 0x2;
            if (this.checkDrawerViewAbsoluteGravity(view, 3)) {
                this.mLeftDragger.smoothSlideViewTo(view, 0, view.getTop());
            }
            else {
                this.mRightDragger.smoothSlideViewTo(view, this.getWidth() - view.getWidth(), view.getTop());
            }
        }
        else {
            this.moveDrawerToOffset(view, 1.0f);
            this.updateDrawerState(drawerLayout$LayoutParams.gravity, 0, view);
            view.setVisibility(0);
        }
        this.invalidate();
    }
    
    public void removeDrawerListener(final DrawerLayout$DrawerListener drawerLayout$DrawerListener) {
        if (drawerLayout$DrawerListener != null && this.mListeners != null) {
            this.mListeners.remove(drawerLayout$DrawerListener);
        }
    }
    
    public void requestDisallowInterceptTouchEvent(final boolean mDisallowInterceptRequested) {
        super.requestDisallowInterceptTouchEvent(mDisallowInterceptRequested);
        this.mDisallowInterceptRequested = mDisallowInterceptRequested;
        if (mDisallowInterceptRequested) {
            this.closeDrawers(true);
        }
    }
    
    public void requestLayout() {
        if (!this.mInLayout) {
            super.requestLayout();
        }
    }
    
    public void setChildInsets(final Object mLastInsets, final boolean mDrawStatusBarBackground) {
        this.mLastInsets = mLastInsets;
        this.mDrawStatusBarBackground = mDrawStatusBarBackground;
        this.setWillNotDraw(!mDrawStatusBarBackground && this.getBackground() == null);
        this.requestLayout();
    }
    
    public void setDrawerElevation(final float mDrawerElevation) {
        this.mDrawerElevation = mDrawerElevation;
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (this.isDrawerView(child)) {
                ViewCompat.setElevation(child, this.mDrawerElevation);
            }
        }
    }
    
    @Deprecated
    public void setDrawerListener(final DrawerLayout$DrawerListener mListener) {
        if (this.mListener != null) {
            this.removeDrawerListener(this.mListener);
        }
        if (mListener != null) {
            this.addDrawerListener(mListener);
        }
        this.mListener = mListener;
    }
    
    public void setDrawerLockMode(final int n) {
        this.setDrawerLockMode(n, 3);
        this.setDrawerLockMode(n, 5);
    }
    
    public void setDrawerLockMode(final int n, final int n2) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(n2, ViewCompat.getLayoutDirection((View)this));
        switch (n2) {
            case 3: {
                this.mLockModeLeft = n;
                break;
            }
            case 5: {
                this.mLockModeRight = n;
                break;
            }
            case 8388611: {
                this.mLockModeStart = n;
                break;
            }
            case 8388613: {
                this.mLockModeEnd = n;
                break;
            }
        }
        if (n != 0) {
            ViewDragHelper viewDragHelper;
            if (absoluteGravity == 3) {
                viewDragHelper = this.mLeftDragger;
            }
            else {
                viewDragHelper = this.mRightDragger;
            }
            viewDragHelper.cancel();
        }
        switch (n) {
            case 2: {
                final View drawerWithGravity = this.findDrawerWithGravity(absoluteGravity);
                if (drawerWithGravity != null) {
                    this.openDrawer(drawerWithGravity);
                    return;
                }
                break;
            }
            case 1: {
                final View drawerWithGravity2 = this.findDrawerWithGravity(absoluteGravity);
                if (drawerWithGravity2 != null) {
                    this.closeDrawer(drawerWithGravity2);
                    return;
                }
                break;
            }
        }
    }
    
    public void setDrawerLockMode(final int n, final View view) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a " + "drawer with appropriate layout_gravity");
        }
        this.setDrawerLockMode(n, ((DrawerLayout$LayoutParams)view.getLayoutParams()).gravity);
    }
    
    public void setDrawerShadow(final int n, final int n2) {
        this.setDrawerShadow(ContextCompat.getDrawable(this.getContext(), n), n2);
    }
    
    public void setDrawerShadow(final Drawable drawable, final int n) {
        if (!DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION) {
            if ((n & 0x800003) == 0x800003) {
                this.mShadowStart = drawable;
            }
            else if ((n & 0x800005) == 0x800005) {
                this.mShadowEnd = drawable;
            }
            else if ((n & 0x3) == 0x3) {
                this.mShadowLeft = drawable;
            }
            else {
                if ((n & 0x5) != 0x5) {
                    return;
                }
                this.mShadowRight = drawable;
            }
            this.resolveShadowDrawables();
            this.invalidate();
        }
    }
    
    public void setDrawerTitle(int absoluteGravity, final CharSequence charSequence) {
        absoluteGravity = GravityCompat.getAbsoluteGravity(absoluteGravity, ViewCompat.getLayoutDirection((View)this));
        if (absoluteGravity == 3) {
            this.mTitleLeft = charSequence;
        }
        else if (absoluteGravity == 5) {
            this.mTitleRight = charSequence;
        }
    }
    
    void setDrawerViewOffset(final View view, final float onScreen) {
        final DrawerLayout$LayoutParams drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)view.getLayoutParams();
        if (onScreen == drawerLayout$LayoutParams.onScreen) {
            return;
        }
        this.dispatchOnDrawerSlide(view, drawerLayout$LayoutParams.onScreen = onScreen);
    }
    
    public void setScrimColor(final int mScrimColor) {
        this.mScrimColor = mScrimColor;
        this.invalidate();
    }
    
    public void setStatusBarBackground(final int n) {
        Drawable drawable;
        if (n != 0) {
            drawable = ContextCompat.getDrawable(this.getContext(), n);
        }
        else {
            drawable = null;
        }
        this.mStatusBarBackground = drawable;
        this.invalidate();
    }
    
    public void setStatusBarBackground(final Drawable mStatusBarBackground) {
        this.mStatusBarBackground = mStatusBarBackground;
        this.invalidate();
    }
    
    public void setStatusBarBackgroundColor(final int n) {
        this.mStatusBarBackground = (Drawable)new ColorDrawable(n);
        this.invalidate();
    }
    
    void updateDrawerState(int viewDragState, int i, final View view) {
        viewDragState = this.mLeftDragger.getViewDragState();
        final int viewDragState2 = this.mRightDragger.getViewDragState();
        if (viewDragState == 1 || viewDragState2 == 1) {
            viewDragState = 1;
        }
        else if (viewDragState == 2 || viewDragState2 == 2) {
            viewDragState = 2;
        }
        else {
            viewDragState = 0;
        }
        if (view != null && i == 0) {
            final DrawerLayout$LayoutParams drawerLayout$LayoutParams = (DrawerLayout$LayoutParams)view.getLayoutParams();
            if (drawerLayout$LayoutParams.onScreen == 0.0f) {
                this.dispatchOnDrawerClosed(view);
            }
            else if (drawerLayout$LayoutParams.onScreen == 1.0f) {
                this.dispatchOnDrawerOpened(view);
            }
        }
        if (viewDragState != this.mDrawerState) {
            this.mDrawerState = viewDragState;
            if (this.mListeners != null) {
                for (i = this.mListeners.size() - 1; i >= 0; --i) {
                    this.mListeners.get(i).onDrawerStateChanged(viewDragState);
                }
            }
        }
    }
}
