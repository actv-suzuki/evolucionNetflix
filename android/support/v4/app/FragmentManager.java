// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.app;

import java.util.List;
import android.os.Bundle;
import java.io.PrintWriter;
import java.io.FileDescriptor;

public abstract class FragmentManager
{
    public static final int POP_BACK_STACK_INCLUSIVE = 1;
    
    public static void enableDebugLogging(final boolean debug) {
        FragmentManagerImpl.DEBUG = debug;
    }
    
    public abstract void addOnBackStackChangedListener(final FragmentManager$OnBackStackChangedListener p0);
    
    public abstract FragmentTransaction beginTransaction();
    
    public abstract void dump(final String p0, final FileDescriptor p1, final PrintWriter p2, final String[] p3);
    
    public abstract boolean executePendingTransactions();
    
    public abstract Fragment findFragmentById(final int p0);
    
    public abstract Fragment findFragmentByTag(final String p0);
    
    public abstract FragmentManager$BackStackEntry getBackStackEntryAt(final int p0);
    
    public abstract int getBackStackEntryCount();
    
    public abstract Fragment getFragment(final Bundle p0, final String p1);
    
    public abstract List<Fragment> getFragments();
    
    public abstract boolean isDestroyed();
    
    @Deprecated
    public FragmentTransaction openTransaction() {
        return this.beginTransaction();
    }
    
    public abstract void popBackStack();
    
    public abstract void popBackStack(final int p0, final int p1);
    
    public abstract void popBackStack(final String p0, final int p1);
    
    public abstract boolean popBackStackImmediate();
    
    public abstract boolean popBackStackImmediate(final int p0, final int p1);
    
    public abstract boolean popBackStackImmediate(final String p0, final int p1);
    
    public abstract void putFragment(final Bundle p0, final String p1, final Fragment p2);
    
    public abstract void registerFragmentLifecycleCallbacks(final FragmentManager$FragmentLifecycleCallbacks p0, final boolean p1);
    
    public abstract void removeOnBackStackChangedListener(final FragmentManager$OnBackStackChangedListener p0);
    
    public abstract Fragment$SavedState saveFragmentInstanceState(final Fragment p0);
    
    public abstract void unregisterFragmentLifecycleCallbacks(final FragmentManager$FragmentLifecycleCallbacks p0);
}
