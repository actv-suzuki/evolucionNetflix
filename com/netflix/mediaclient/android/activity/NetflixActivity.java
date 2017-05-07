// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.android.activity;

import com.netflix.mediaclient.ui.LaunchActivity;
import com.netflix.mediaclient.util.DeviceUtils;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.IntentFilter;
import com.netflix.mediaclient.service.logging.client.model.UIError;
import com.netflix.mediaclient.util.LogUtils;
import java.util.Iterator;
import android.support.v4.content.LocalBroadcastManager;
import com.netflix.mediaclient.ui.settings.SettingsActivity;
import android.os.Bundle;
import com.netflix.mediaclient.ui.ServiceErrorsHandler;
import com.netflix.mediaclient.ui.login.LogoutActivity;
import com.netflix.mediaclient.servicemgr.UserActionLogging;
import com.netflix.mediaclient.NetflixApplication;
import android.app.DialogFragment;
import com.netflix.mediaclient.service.logging.client.model.DataContext;
import com.netflix.mediaclient.servicemgr.IClientLogging;
import com.netflix.mediaclient.servicemgr.ApplicationPerformanceMetricsLogging;
import java.io.Serializable;
import com.netflix.mediaclient.android.widget.AlertDialogFactory;
import com.netflix.mediaclient.android.widget.UpdateDialog;
import android.app.AlertDialog;
import android.app.AlertDialog$Builder;
import android.view.MotionEvent;
import android.view.KeyEvent;
import com.netflix.mediaclient.servicemgr.ManagerStatusListener;
import com.netflix.mediaclient.util.gfx.ImageLoader;
import com.netflix.mediaclient.ui.home.HomeActivity;
import com.netflix.mediaclient.android.widget.AccessibilityRunnable;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.widget.Toast;
import android.os.Debug;
import com.netflix.mediaclient.service.user.UserAgentBroadcastIntents;
import com.netflix.mediaclient.service.NetflixService;
import com.netflix.mediaclient.ui.mdx.MdxReceiver;
import android.view.MenuItem;
import android.view.MenuItem$OnMenuItemClickListener;
import android.view.Menu;
import android.view.View;
import com.netflix.mediaclient.util.AndroidUtils;
import com.netflix.mediaclient.Log;
import android.content.Intent;
import android.content.Context;
import java.util.HashSet;
import android.app.Dialog;
import android.app.ActionBar;
import com.netflix.mediaclient.servicemgr.ServiceManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.netflix.mediaclient.android.widget.NetflixActionBar;
import com.netflix.mediaclient.ui.mdx.MdxMiniPlayerFrag;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import android.os.Handler;
import android.content.BroadcastReceiver;
import java.util.Set;
import com.netflix.mediaclient.ui.mdx.ShowMessageDialogFrag;
import com.netflix.mediaclient.android.app.LoadingStatus;
import com.netflix.mediaclient.ui.details.EpisodeRowView;
import android.app.Activity;

public abstract class NetflixActivity extends Activity implements EpisodeRowListenerProvider, LoadingStatus, MessageResponseProvider
{
    private static final long ACTION_BAR_VISIBILITY_CHECK_DELAY_MS = 1000L;
    private static final String ACTION_FINISH_ALL_ACTIVITIES = "com.netflix.mediaclient.ui.login.ACTION_FINISH_ALL_ACTIVITIES";
    public static final long EXPAND_MINI_PLAYER_DELAY_MS = 400L;
    private static final String EXTRA_SHOULD_EXPAND_MINI_PLAYER = "mini_player_expanded";
    private static final String FRAG_DIALOG_TAG = "frag_dialog";
    private static final String INSTANCE_STATE_SAVED_TAG = "NetflixActivity_instanceState";
    public static final String INTENT_CATEGORY_UI = "LocalIntentNflxUi";
    private static final boolean PRINT_LOADING_STATUS = false;
    private static final String TAG = "NetflixActivity";
    private int actionBarHeight;
    private final Set<BroadcastReceiver> autoUnregisterLocalBroadcastReceivers;
    private final Set<BroadcastReceiver> autoUnregisterReceivers;
    private final BroadcastReceiver autokillReceiver;
    private final BroadcastReceiver expandMdxMiniPlayerReceiver;
    protected Handler handler;
    protected AtomicBoolean instanceStateSaved;
    private boolean isDestroyed;
    private boolean isVisible;
    protected AtomicLong mDialogCount;
    protected LoadingStatusCallback mLoadingStatusCallback;
    private MdxMiniPlayerFrag mdxFrag;
    private NetflixActionBar netflixActionBar;
    private final SlidingUpPanelLayout.PanelSlideListener panelSlideListener;
    private final Runnable printLoadingStatusRunnable;
    private ServiceManager serviceManager;
    private boolean shouldExpandMiniPlayer;
    private SlidingUpPanelLayout slidingPanel;
    private ActionBar systemActionBar;
    private final Runnable updateActionBarVisibilityRunnable;
    private final BroadcastReceiver userAgentUpdateReceiver;
    protected Dialog visibleDialog;
    protected Object visibleDialogLock;
    
    public NetflixActivity() {
        this.autoUnregisterReceivers = new HashSet<BroadcastReceiver>();
        this.autoUnregisterLocalBroadcastReceivers = new HashSet<BroadcastReceiver>();
        this.instanceStateSaved = new AtomicBoolean(false);
        this.visibleDialogLock = new Object();
        this.mDialogCount = new AtomicLong(1L);
        this.autokillReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                Log.v("NetflixActivity", "Finishing activity " + NetflixActivity.this.getClass().getSimpleName() + " from intent: " + intent.getAction());
                NetflixActivity.this.finish();
            }
        };
        this.expandMdxMiniPlayerReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (NetflixActivity.this.destroyed()) {
                    return;
                }
                if (intent == null || !"com.netflix.mediaclient.service.ACTION_EXPAND_MDX_MINI_PLAYER".equals(intent.getAction())) {
                    Log.v("NetflixActivity", "Invalid intent: ");
                    AndroidUtils.logIntent("NetflixActivity", intent);
                    return;
                }
                NetflixActivity.this.expandMiniPlayerIfVisible();
            }
        };
        this.printLoadingStatusRunnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        this.userAgentUpdateReceiver = new BroadcastReceiver() {
            private void logWithClassName(final String s) {
                if (Log.isLoggable("NetflixActivity", 3)) {
                    Log.d("NetflixActivity", NetflixActivity.this.getClass().getSimpleName() + ": " + s);
                }
            }
            
            public void onReceive(final Context context, final Intent intent) {
                if (intent == null) {
                    this.logWithClassName("Null intent");
                    return;
                }
                final String action = intent.getAction();
                if ("com.netflix.mediaclient.intent.action.NOTIFY_USER_PROFILE_ACTIVE".equals(action)) {
                    this.logWithClassName("User profile activated - restarting app");
                    NetflixActivity.this.handleProfileActivated();
                    return;
                }
                if ("com.netflix.mediaclient.intent.action.NOTIFY_USER_ACCOUNT_DEACTIVE".equals(action)) {
                    this.logWithClassName("Account deactivated - restarting app");
                    NetflixActivity.this.handleAccountDeactivated();
                    return;
                }
                if ("com.netflix.mediaclient.intent.action.NOTIFY_USER_PROFILE_READY_TO_SELECT".equals(action)) {
                    this.logWithClassName("Ready to select profile - calling children");
                    NetflixActivity.this.handleProfileReadyToSelect();
                    return;
                }
                if ("com.netflix.mediaclient.intent.action.NOTIFY_USER_PROFILE_SELECTION_RESULT".equals(action)) {
                    final int intExtra = intent.getIntExtra("com.netflix.mediaclient.intent.action.EXTRA_USER_PROFILE_SELECTION_RESULT_INT", 0);
                    final String stringExtra = intent.getStringExtra("com.netflix.mediaclient.intent.action.EXTRA_USER_PROFILE_SELECTION_RESULT_STRING");
                    this.logWithClassName("Profile selection status: " + intExtra);
                    NetflixActivity.this.handleProfileSelectionResult(intExtra, stringExtra);
                    return;
                }
                this.logWithClassName("No action taken for intent: " + action);
            }
        };
        this.panelSlideListener = new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelAnchored(final View view) {
                Log.v("NetflixActivity", "onPanelAnchored");
            }
            
            @Override
            public void onPanelCollapsed(final View view) {
                Log.v("NetflixActivity", "onPanelCollapsed");
                NetflixActivity.this.onSlidingPanelCollapsed(view);
                if (NetflixActivity.this.mdxFrag != null) {
                    NetflixActivity.this.mdxFrag.onPanelCollapsed();
                }
            }
            
            @Override
            public void onPanelExpanded(final View view) {
                Log.v("NetflixActivity", "onPanelExpanded");
                NetflixActivity.this.onSlidingPanelExpanded(view);
                if (NetflixActivity.this.mdxFrag != null) {
                    NetflixActivity.this.mdxFrag.onPanelExpanded();
                }
            }
            
            @Override
            public void onPanelSlide(final View view, final float n) {
                if (Log.isLoggable("NetflixActivity", 2)) {
                    Log.v("NetflixActivity", "onPanelSlide, offset: " + n);
                }
                if (NetflixActivity.this.mdxFrag != null) {
                    NetflixActivity.this.mdxFrag.onPanelSlide(n);
                }
                if (NetflixActivity.this.systemActionBar != null) {
                    if (n < 0.3f) {
                        if (NetflixActivity.this.systemActionBar.isShowing()) {
                            NetflixActivity.this.systemActionBar.hide();
                        }
                    }
                    else if (!NetflixActivity.this.systemActionBar.isShowing()) {
                        NetflixActivity.this.systemActionBar.show();
                    }
                }
            }
        };
        this.updateActionBarVisibilityRunnable = new Runnable() {
            @Override
            public void run() {
                if (NetflixActivity.this.isVisible && !NetflixActivity.this.destroyed() && NetflixActivity.this.mdxFrag != null && NetflixActivity.this.slidingPanel != null && NetflixActivity.this.systemActionBar != null) {
                    if (Log.isLoggable("NetflixActivity", 2)) {
                        Log.v("NetflixActivity", "Checking to see if action bar visibility is valid.  Frag showing: " + NetflixActivity.this.mdxFrag.isShowing() + ", panel expanded: " + NetflixActivity.this.slidingPanel.isExpanded() + ", system action bar showing: " + NetflixActivity.this.systemActionBar.isShowing());
                    }
                    if (NetflixActivity.this.mdxFrag.isShowing() && NetflixActivity.this.slidingPanel.isExpanded()) {
                        if (NetflixActivity.this.systemActionBar.isShowing()) {
                            Log.v("NetflixActivity", "Hiding action bar since it should not be shown");
                            NetflixActivity.this.systemActionBar.hide();
                        }
                    }
                    else if (!NetflixActivity.this.systemActionBar.isShowing()) {
                        Log.v("NetflixActivity", "Showing action bar since it should not be hidden");
                        NetflixActivity.this.systemActionBar.show();
                    }
                }
            }
        };
    }
    
    private void addFlushDataCacheItem(final Menu menu) {
        menu.add((CharSequence)"Flush Data Cache").setOnMenuItemClickListener((MenuItem$OnMenuItemClickListener)new MenuItem$OnMenuItemClickListener() {
            public boolean onMenuItemClick(final MenuItem menuItem) {
                NetflixActivity.this.serviceManager.flushCaches();
                return true;
            }
        });
    }
    
    private void addHprofDumpItem(final Menu menu) {
        menu.add((CharSequence)"Dump hprof profile").setOnMenuItemClickListener((MenuItem$OnMenuItemClickListener)new MenuItem$OnMenuItemClickListener() {
            public boolean onMenuItemClick(final MenuItem menuItem) {
                AndroidUtils.dumpHprofToDisk();
                return true;
            }
        });
    }
    
    private void addMdxReceiver() {
        if (!this.showMdxInMenu()) {
            Log.d("NetflixActivity", "Activity does not required MDX, skipping add of MDX receiver.");
            return;
        }
        Log.d("NetflixActivity", "Listen to updated from MDX service, add");
        final MdxReceiver mdxReceiver = new MdxReceiver(this);
        this.registerReceiverWithAutoUnregister(mdxReceiver, mdxReceiver.getFilter());
        Log.d("NetflixActivity", "Listen to updated from MDX service, added");
    }
    
    private void addToggleFetchErrorsItem(final Menu menu) {
        menu.add((CharSequence)"Toggle Fetch Errors").setOnMenuItemClickListener((MenuItem$OnMenuItemClickListener)new MenuItem$OnMenuItemClickListener() {
            public boolean onMenuItemClick(final MenuItem menuItem) {
                NetflixService.toggleFetchErrorsEnabled();
                NetflixActivity.this.showFetchErrorsToast();
                return true;
            }
        });
    }
    
    private void addTraceviewItem(final Menu menu) {
        menu.add((CharSequence)"5s Traceview").setOnMenuItemClickListener((MenuItem$OnMenuItemClickListener)new MenuItem$OnMenuItemClickListener() {
            public boolean onMenuItemClick(final MenuItem menuItem) {
                NetflixActivity.this.beginTraceview();
                return true;
            }
        });
    }
    
    private void addUserAgentUpdateReceiver() {
        this.registerBroadcastReceiverLocallyWithAutoUnregister(this.userAgentUpdateReceiver, UserAgentBroadcastIntents.getNotificationIntentFilter());
    }
    
    private void beginTraceview() {
        Log.i("NetflixActivity", "Starting method trace...");
        Debug.startMethodTracing("nflx");
        new Handler().postDelayed((Runnable)new Runnable() {
            @Override
            public void run() {
                Debug.stopMethodTracing();
                Log.i("NetflixActivity", "Trace complete.  Get with: adb pull /sdcard/nflx.trace");
                Toast.makeText((Context)NetflixActivity.this, (CharSequence)"Trace: /sdcard/nflx.trace", 1).show();
            }
        }, 5000L);
    }
    
    private void collapseSlidingPanel() {
        boolean collapsePane = false;
        if (this.slidingPanel != null) {
            collapsePane = collapsePane;
            if (this.slidingPanel.isExpanded()) {
                Log.v("NetflixActivity", "Collapsing sliding panel...");
                collapsePane = this.slidingPanel.collapsePane();
            }
        }
        if (!collapsePane && this.mdxFrag != null) {
            this.mdxFrag.onPanelCollapsed();
        }
    }
    
    private int computeActionBarHeight() {
        final TypedArray obtainStyledAttributes = this.obtainStyledAttributes(new TypedValue().data, new int[] { 16843499 });
        final int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(0, -1);
        obtainStyledAttributes.recycle();
        return dimensionPixelSize;
    }
    
    static AccessibilityRunnable createDefaultUpActionRunnable(final Activity activity) {
        return new AccessibilityRunnable(new Runnable() {
            @Override
            public void run() {
                activity.startActivity(HomeActivity.createStartIntent((Context)activity));
            }
        }, activity.getString(2131296563));
    }
    
    public static void finishAllActivities(final Context context) {
        context.sendBroadcast(new Intent("com.netflix.mediaclient.ui.login.ACTION_FINISH_ALL_ACTIVITIES"));
    }
    
    public static ImageLoader getImageLoader(final Context context) {
        return ((NetflixActivity)context).serviceManager.getImageLoader();
    }
    
    private void postActionBarUpdate() {
        this.handler.removeCallbacks(this.updateActionBarVisibilityRunnable);
        this.handler.postDelayed(this.updateActionBarVisibilityRunnable, 1000L);
    }
    
    private void setInstanceStateSaved(final boolean b) {
        Log.v("NetflixActivity_instanceState", this.getClass().getSimpleName() + " instanceStateSaved: " + b);
        synchronized (this.instanceStateSaved) {
            this.instanceStateSaved.set(b);
        }
    }
    
    protected NetflixActionBar createActionBar(final ActionBar actionBar) {
        return new NetflixActionBar(this, actionBar, this.createUpActionRunnable());
    }
    
    protected ManagerStatusListener createManagerStatusListener() {
        return null;
    }
    
    public AccessibilityRunnable createUpActionRunnable() {
        return createDefaultUpActionRunnable(this);
    }
    
    public boolean destroyed() {
        return this.isDestroyed;
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        this.getNetflixApplication().getUserInput().updateUserInteraction();
        return (this.mdxFrag != null && this.mdxFrag.dispatchKeyEvent(keyEvent)) || super.dispatchKeyEvent(keyEvent);
    }
    
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        this.getNetflixApplication().getUserInput().updateUserInteraction();
        return super.dispatchTouchEvent(motionEvent);
    }
    
    public boolean dispatchTrackballEvent(final MotionEvent motionEvent) {
        this.getNetflixApplication().getUserInput().updateUserInteraction();
        return super.dispatchTrackballEvent(motionEvent);
    }
    
    public Dialog displayDialog(final AlertDialog$Builder alertDialog$Builder) {
        if (alertDialog$Builder == null) {
            return null;
        }
        synchronized (this.visibleDialogLock) {
            final AlertDialog create = alertDialog$Builder.create();
            this.displayDialog((Dialog)create);
            return (Dialog)create;
        }
    }
    
    public Dialog displayDialog(final UpdateDialog.Builder builder) {
        if (builder == null || this.destroyed()) {
            return null;
        }
        synchronized (this.visibleDialogLock) {
            final UpdateDialog create = builder.create();
            this.displayDialog((Dialog)create);
            return (Dialog)create;
        }
    }
    
    public void displayDialog(final Dialog dialog) {
        if (dialog == null || this.destroyed()) {
            return;
        }
        synchronized (this.visibleDialogLock) {
            if (this.destroyed()) {
                return;
            }
        }
        if (this.visibleDialog != null) {
            this.visibleDialog.dismiss();
        }
        final Dialog visibleDialog;
        visibleDialog.show();
        this.visibleDialog = visibleDialog;
    }
    // monitorexit(o)
    
    protected void displayUserAgentDialog(final String s, Runnable visibleDialogLock, final boolean b) {
        final UpdateDialog.Builder dialog = AlertDialogFactory.createDialog((Context)this, this.handler, new AlertDialogFactory.AlertDialogDescriptor(null, s, this.getString(17039370), visibleDialogLock));
        if (this.destroyed()) {
            return;
        }
        visibleDialogLock = (Runnable)this.visibleDialogLock;
        // monitorenter(visibleDialogLock)
        Label_0098: {
            if (!b) {
                break Label_0098;
            }
            while (true) {
                try {
                    if (Log.isLoggable("NetflixActivity", 3)) {
                        Log.d("NetflixActivity", "displayUserAgentDialog " + s + " isCritical");
                    }
                    this.displayDialog(dialog);
                    return;
                Label_0150_Outer:
                    while (true) {
                        while (true) {
                            Block_10: {
                                break Block_10;
                                this.displayDialog(dialog);
                                return;
                            }
                            Log.d("NetflixActivity", "displayUserAgentDialog " + s);
                            continue;
                        }
                        continue Label_0150_Outer;
                    }
                }
                // iftrue(Label_0150:, !Log.isLoggable("NetflixActivity", 3))
                // iftrue(Label_0165:, this.getVisibleDialog() == null || this.getVisibleDialog().isShowing())
                finally {
                }
                // monitorexit(visibleDialogLock)
                final String s2;
                Label_0165: {
                    if (this.getVisibleDialog() == null) {
                        if (Log.isLoggable("NetflixActivity", 3)) {
                            Log.d("NetflixActivity", "displayUserAgentDialog, no dialog  " + s2);
                        }
                        this.displayDialog(dialog);
                        return;
                    }
                }
                if (Log.isLoggable("NetflixActivity", 3)) {
                    Log.e("NetflixActivity", "displayUserAgentDialog, Dialog visible, skipping  " + s2);
                }
            }
        }
    }
    
    protected void expandMiniPlayerIfVisible() {
        if (this.isVisible) {
            Log.v("NetflixActivity", "Activity is visible, checking for MDX mini player to see if it can be expanded...");
            final StringBuilder append = new StringBuilder().append("MDX frag showing: ");
            Serializable value;
            if (this.mdxFrag == null) {
                value = "null";
            }
            else {
                value = this.mdxFrag.isShowing();
            }
            Log.v("NetflixActivity", append.append(value).toString());
            if (this.mdxFrag != null && this.mdxFrag.isShowing() && this.slidingPanel != null) {
                Log.v("NetflixActivity", "Expanding mini player");
                String s;
                if (this.slidingPanel.expandPane()) {
                    s = "Panel is expanding";
                }
                else {
                    s = "Panel is NOT expanding";
                }
                Log.v("NetflixActivity", s);
            }
        }
    }
    
    public void finish() {
        if (Log.isLoggable("NetflixActivity", 3)) {
            Log.d("NetflixActivity", this.getClass().getSimpleName() + ": finish has been called");
        }
        super.finish();
    }
    
    protected ApplicationPerformanceMetricsLogging getApmSafely() {
        final ServiceManager serviceManager = this.getServiceManager();
        if (serviceManager != null) {
            final IClientLogging clientLogging = serviceManager.getClientLogging();
            if (clientLogging != null) {
                return clientLogging.getApplicationPerformanceMetricsLogging();
            }
        }
        return null;
    }
    
    protected DataContext getDataContext() {
        return null;
    }
    
    public DialogFragment getDialogFragment() {
        return (DialogFragment)this.getFragmentManager().findFragmentByTag("frag_dialog");
    }
    
    public EpisodeRowListener getEpisodeRowListener() {
        return this.mdxFrag;
    }
    
    public MdxMiniPlayerFrag getMdxMiniPlayerFrag() {
        return this.mdxFrag;
    }
    
    public NetflixActionBar getNetflixActionBar() {
        return this.netflixActionBar;
    }
    
    public NetflixApplication getNetflixApplication() {
        return (NetflixApplication)this.getApplication();
    }
    
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }
    
    public abstract IClientLogging.ModalView getUiScreen();
    
    protected UserActionLogging getUserActionLoggingSafely() {
        final ServiceManager serviceManager = this.getServiceManager();
        if (serviceManager != null) {
            final IClientLogging clientLogging = serviceManager.getClientLogging();
            if (clientLogging != null) {
                return clientLogging.getUserActionLogging();
            }
        }
        return null;
    }
    
    public Dialog getVisibleDialog() {
        return this.visibleDialog;
    }
    
    protected void handleAccountDeactivated() {
        this.finish();
        if (this.isVisible && !(this instanceof LogoutActivity)) {
            this.startActivity(LogoutActivity.create((Context)this));
        }
    }
    
    protected boolean handleBackPressed() {
        return false;
    }
    
    protected void handleNetwotkErrorDialog() {
        this.finish();
    }
    
    protected void handleProfileActivated() {
        this.finish();
    }
    
    protected void handleProfileReadyToSelect() {
    }
    
    protected void handleProfileSelectionResult(final int n, final String s) {
    }
    
    protected void handleUserAgentErrors(final Activity activity, final int n, String s) {
        if (s == null) {
            s = "";
        }
        switch (n) {
            default: {
                this.displayUserAgentDialog(String.format("%s ( %d )", this.getString(2131296644), n), new Runnable() {
                    @Override
                    public void run() {
                        NetflixActivity.this.finish();
                    }
                }, true);
            }
            case -202: {
                this.displayUserAgentDialog(s, null, false);
            }
            case -207:
            case -203: {
                this.displayUserAgentDialog(String.format("%s ( %d )", this.getString(2131296648), n), new Runnable() {
                    @Override
                    public void run() {
                        Log.d("NetflixActivity", "Restarting app, time: " + System.nanoTime());
                        NetflixActivity.this.finish();
                        NetflixActivity.this.startActivity(LogoutActivity.create((Context)NetflixActivity.this));
                    }
                }, true);
            }
            case -208: {
                ServiceErrorsHandler.handleManagerResponse(this, -5);
            }
            case -211:
            case -210:
            case -209:
            case -206:
            case -205:
            case -204:
            case -201:
            case -200: {
                this.displayUserAgentDialog(String.format("%s ( %d )", this.getString(2131296640), n), null, false);
            }
            case -3: {
                this.displayUserAgentDialog(this.getString(2131296644) + " (" + n + ")", new Runnable() {
                    @Override
                    public void run() {
                        NetflixActivity.this.handleNetwotkErrorDialog();
                    }
                }, true);
            }
        }
    }
    
    public void hideMdxMiniPlayer() {
        if (this.mdxFrag != null) {
            this.mdxFrag.hide();
        }
    }
    
    public boolean isDialogFragmentVisible() {
        return this.getDialogFragment() != null;
    }
    
    public boolean isPanelExpanded() {
        return this.slidingPanel != null && this.slidingPanel.isExpanded();
    }
    
    public void notifyMdxEndOfPlayback() {
        Log.v("NetflixActivity", "MDX end of playback");
        this.collapseSlidingPanel();
        this.postActionBarUpdate();
    }
    
    public void notifyMdxMiniPlayerHidden() {
        Log.v("NetflixActivity", "MDX frag hidden");
        this.collapseSlidingPanel();
        this.postActionBarUpdate();
    }
    
    public void notifyMdxMiniPlayerShown() {
        Log.v("NetflixActivity", "MDX frag shown");
        this.postActionBarUpdate();
    }
    
    public void notifyMdxShowDetailsRequest() {
        this.handler.postDelayed((Runnable)new Runnable() {
            @Override
            public void run() {
                NetflixActivity.this.collapseSlidingPanel();
            }
        }, 250L);
    }
    
    public void onBackPressed() {
        Log.v("NetflixActivity", this.getClass().getSimpleName() + ": back button pressed");
        if (this.slidingPanel != null && this.mdxFrag != null && this.slidingPanel.isExpanded() && this.mdxFrag.isVisible()) {
            Log.v("NetflixActivity", "MDX mini player sliding panel was expanded, collapsing...");
            this.slidingPanel.collapsePane();
        }
        else if (!this.handleBackPressed()) {
            super.onBackPressed();
        }
    }
    
    protected void onCreate(final Bundle bundle) {
        final boolean b = false;
        super.onCreate(bundle);
        this.setInstanceStateSaved(false);
        if (Log.isLoggable("NetflixActivity", 2)) {
            Log.v("NetflixActivity", "Creating activity: " + this.getClass().getSimpleName() + ", hash: " + this.hashCode());
        }
        boolean shouldExpandMiniPlayer = b;
        if (bundle != null) {
            shouldExpandMiniPlayer = b;
            if (bundle.getBoolean("mini_player_expanded", false)) {
                shouldExpandMiniPlayer = true;
            }
        }
        this.shouldExpandMiniPlayer = shouldExpandMiniPlayer;
        Log.v("NetflixActivity", "Should expand mini player: " + this.shouldExpandMiniPlayer);
        this.registerFinishReceiverWithAutoUnregister("com.netflix.mediaclient.ui.login.ACTION_FINISH_ALL_ACTIVITIES");
        this.registerReceiverWithAutoUnregister(this.expandMdxMiniPlayerReceiver, "com.netflix.mediaclient.service.ACTION_EXPAND_MDX_MINI_PLAYER");
        this.actionBarHeight = this.computeActionBarHeight();
        this.systemActionBar = super.getActionBar();
        if (this.systemActionBar != null) {
            this.netflixActionBar = this.createActionBar(this.systemActionBar);
        }
        this.serviceManager = new ServiceManager((Context)this, new DefaultManagerStatusListener(this.createManagerStatusListener()));
        this.handler = new Handler();
    }
    
    protected void onCreateOptionsMenu(final Menu menu, final Menu menu2) {
        if (menu2 != null) {
            this.addHprofDumpItem(menu2);
            this.addTraceviewItem(menu2);
            this.addToggleFetchErrorsItem(menu2);
            this.addFlushDataCacheItem(menu2);
        }
        if (this.showSettingsInMenu()) {
            menu.add(2131296510).setIcon(2130837713).setIntent(SettingsActivity.createStartIntent(this));
        }
        if (this.showSignOutInMenu()) {
            menu.add(2131296559).setOnMenuItemClickListener((MenuItem$OnMenuItemClickListener)new MenuItem$OnMenuItemClickListener() {
                public boolean onMenuItemClick(final MenuItem menuItem) {
                    LogoutActivity.showLogoutDialog(NetflixActivity.this);
                    return true;
                }
            });
        }
    }
    
    public boolean onCreateOptionsMenu(final Menu menu) {
        Log.v("NetflixActivity", "onCreateOptionsMenu");
        this.onCreateOptionsMenu(menu, null);
        return super.onCreateOptionsMenu(menu);
    }
    
    protected void onDestroy() {
        ((NetflixApplication)this.getApplication()).releaseCurrentActivity(this);
        if (Log.isLoggable("NetflixActivity", 2)) {
            Log.v("NetflixActivity", "Destroying activity: " + this.getClass().getSimpleName() + ", hash: " + this.hashCode());
        }
        this.isDestroyed = true;
        final Iterator<BroadcastReceiver> iterator = this.autoUnregisterReceivers.iterator();
        while (iterator.hasNext()) {
            this.unregisterReceiver((BroadcastReceiver)iterator.next());
        }
        final Iterator<BroadcastReceiver> iterator2 = this.autoUnregisterLocalBroadcastReceivers.iterator();
        while (iterator2.hasNext()) {
            LocalBroadcastManager.getInstance((Context)this).unregisterReceiver(iterator2.next());
        }
        if (this.serviceManager != null) {
            this.serviceManager.release();
        }
        super.onDestroy();
    }
    
    protected void onLoaded(final int n) {
        if (this.mLoadingStatusCallback != null) {
            this.mLoadingStatusCallback.onDataLoaded(n);
        }
    }
    
    public boolean onMenuItemSelected(final int n, final MenuItem menuItem) {
        Log.v("NetflixActivity", "onMenuItemSelected: " + menuItem.getItemId());
        return (menuItem != null && this.netflixActionBar != null && this.netflixActionBar.handleHomeButtonSelected(menuItem)) || super.onMenuItemSelected(n, menuItem);
    }
    
    protected void onPause() {
        super.onPause();
        ((NetflixApplication)this.getApplication()).releaseCurrentActivity(this);
        this.isVisible = false;
        this.handler.removeCallbacks(this.printLoadingStatusRunnable);
    }
    
    protected void onPostCreate(final Bundle bundle) {
        super.onPostCreate(bundle);
        Log.v("NetflixActivity", this.getClass().getSimpleName() + ": onPostCreate");
    }
    
    protected void onPostResume() {
        super.onPostResume();
        if (this.mdxFrag != null) {
            this.mdxFrag.onResumeFragments();
        }
    }
    
    public void onResponse(final String s) {
        if (Log.isLoggable("NetflixActivity", 3)) {
            Log.d("NetflixActivity", "onResponse: User selected: " + s);
        }
        if (this.mdxFrag != null) {
            this.mdxFrag.sendDialogResponse(s);
        }
    }
    
    protected void onResume() {
        super.onResume();
        this.setInstanceStateSaved(false);
        ((NetflixApplication)this.getApplication()).setCurrentActivity(this);
        this.isVisible = true;
        this.handler.post(this.printLoadingStatusRunnable);
    }
    
    protected void onSaveInstanceState(final Bundle bundle) {
        boolean b = true;
        this.setInstanceStateSaved(true);
        super.onSaveInstanceState(bundle);
        if (this.mdxFrag == null || !this.mdxFrag.isShowing() || this.slidingPanel == null || !this.slidingPanel.isExpanded()) {
            b = false;
        }
        bundle.putBoolean("mini_player_expanded", b);
    }
    
    protected void onSlidingPanelCollapsed(final View view) {
    }
    
    protected void onSlidingPanelExpanded(final View view) {
    }
    
    protected void onStart() {
        super.onStart();
        LogUtils.reportNavigationActionStarted((Context)this, null, this.getUiScreen());
        this.mdxFrag = (MdxMiniPlayerFrag)this.getFragmentManager().findFragmentById(2131230933);
        this.slidingPanel = (SlidingUpPanelLayout)this.findViewById(2131230881);
        if (this.slidingPanel != null) {
            this.slidingPanel.setDragView(this.mdxFrag.getSlidingPanelDragView());
            this.slidingPanel.setPanelHeight(this.getResources().getDimensionPixelSize(2131492913));
            this.slidingPanel.setShadowDrawable(this.getResources().getDrawable(2130837828));
            this.slidingPanel.setPanelSlideListener(this.panelSlideListener);
            final View child = this.slidingPanel.getChildAt(0);
            child.setPadding(child.getPaddingLeft(), this.actionBarHeight, child.getPaddingRight(), child.getPaddingBottom());
        }
    }
    
    protected void onStop() {
        LogUtils.reportNavigationActionEnded((Context)this, this.getUiScreen(), IClientLogging.CompletionReason.success, null);
        synchronized (this.visibleDialogLock) {
            Label_0040: {
                if (this.visibleDialog == null) {
                    break Label_0040;
                }
                try {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                    // monitorexit(this.visibleDialogLock)
                    super.onStop();
                }
                catch (Throwable t) {
                    Log.e("NetflixActivity", "Failed to dismiss dialog!", t);
                }
            }
        }
    }
    
    public void registerBroadcastReceiverLocallyWithAutoUnregister(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter) {
        LocalBroadcastManager.getInstance((Context)this).registerReceiver(broadcastReceiver, intentFilter);
        this.autoUnregisterLocalBroadcastReceivers.add(broadcastReceiver);
    }
    
    protected void registerFinishReceiverWithAutoUnregister(final String s) {
        this.registerReceiverWithAutoUnregister(this.autokillReceiver, s);
    }
    
    public void registerReceiverWithAutoUnregister(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter) {
        super.registerReceiver(broadcastReceiver, intentFilter);
        this.autoUnregisterReceivers.add(broadcastReceiver);
    }
    
    public void registerReceiverWithAutoUnregister(final BroadcastReceiver broadcastReceiver, final String s) {
        this.registerReceiverWithAutoUnregister(broadcastReceiver, new IntentFilter(s));
    }
    
    public void removeDialogFrag() {
        final FragmentTransaction beginTransaction = this.getFragmentManager().beginTransaction();
        final DialogFragment dialogFragment = this.getDialogFragment();
        if (dialogFragment != null) {
            if (dialogFragment instanceof DialogFragment) {
                dialogFragment.dismiss();
            }
            beginTransaction.remove((Fragment)dialogFragment);
        }
        beginTransaction.commitAllowingStateLoss();
    }
    
    public void removeVisibleDialog() {
        synchronized (this.visibleDialogLock) {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        }
    }
    
    public void reportUiModelessViewSessionEnded(final IClientLogging.ModalView modalView, final String s) {
        final ApplicationPerformanceMetricsLogging apmSafely = this.getApmSafely();
        if (apmSafely != null) {
            final boolean portrait = DeviceUtils.isPortrait((Context)this);
            if (Log.isLoggable("NetflixActivity", 3)) {
                Log.d("NetflixActivity", "Report UI modeless view session ended for  " + modalView + " in portrait: " + portrait + ". Dialog id: " + s);
            }
            apmSafely.endUiModelessViewSession(s);
        }
    }
    
    public String reportUiModelessViewSessionStart(final IClientLogging.ModalView modalView) {
        final ApplicationPerformanceMetricsLogging apmSafely = this.getApmSafely();
        if (apmSafely != null && modalView != null) {
            final boolean portrait = DeviceUtils.isPortrait((Context)this);
            final String value = String.valueOf(this.mDialogCount.getAndIncrement());
            if (Log.isLoggable("NetflixActivity", 3)) {
                Log.d("NetflixActivity", "Report UI modeless view session started for  " + modalView + " in portrait: " + portrait + ". Dialog id: " + value);
            }
            apmSafely.startUiModelessViewSession(portrait, modalView, value);
            return value;
        }
        return null;
    }
    
    protected void reportUiViewChanged(final IClientLogging.ModalView modalView) {
        final ApplicationPerformanceMetricsLogging apmSafely = this.getApmSafely();
        if (apmSafely != null && modalView != null) {
            final boolean portrait = DeviceUtils.isPortrait((Context)this);
            if (Log.isLoggable("NetflixActivity", 3)) {
                Log.d("NetflixActivity", "Report UI modeless view session started for  " + modalView + " in portrait: " + portrait);
            }
            apmSafely.uiViewChanged(portrait, modalView);
        }
    }
    
    public void runInUiThread(final Runnable runnable) {
        if (runnable == null || this.destroyed()) {
            return;
        }
        this.runOnUiThread(runnable);
    }
    
    public void setLoadingStatusCallback(final LoadingStatusCallback mLoadingStatusCallback) {
        if (!this.isLoadingData() && mLoadingStatusCallback != null) {
            mLoadingStatusCallback.onDataLoaded(0);
            return;
        }
        this.mLoadingStatusCallback = mLoadingStatusCallback;
    }
    
    public boolean shouldAddMdxToMenu() {
        if (!this.showMdxInMenu()) {
            Log.d("NetflixActivity", "Activity does not required MDX.");
            return false;
        }
        if (this.serviceManager == null || !this.serviceManager.isReady() || this.serviceManager.getMdx() == null) {
            Log.w("NetflixActivity", "Service manager or mdx are null or service manager is not ready.");
            return false;
        }
        if (!this.serviceManager.isUserLoggedIn()) {
            Log.d("NetflixActivity", "User is not logged in, not adding MDX icon");
            return false;
        }
        return true;
    }
    
    protected boolean shouldFinishOnManagerError() {
        return true;
    }
    
    public void showDialog(final DialogFragment dialogFragment) {
        if (dialogFragment == null || this.isDestroyed || this.instanceStateSaved.get()) {
            return;
        }
        synchronized (this.instanceStateSaved) {
            if (this.instanceStateSaved.get()) {
                return;
            }
        }
        final FragmentTransaction beginTransaction = this.getFragmentManager().beginTransaction();
        final DialogFragment dialogFragment2 = this.getDialogFragment();
        if (dialogFragment2 != null) {
            if (dialogFragment2 instanceof DialogFragment) {
                Log.v("NetflixActivity", "Dismissing previous dialog");
                dialogFragment2.dismiss();
            }
            Log.v("NetflixActivity", "Removing previous dialog");
            beginTransaction.remove((Fragment)dialogFragment2);
        }
        beginTransaction.addToBackStack((String)null);
        Log.v("NetflixActivity", "Showing dialog");
        final DialogFragment dialogFragment3;
        dialogFragment3.show(beginTransaction, "frag_dialog");
    }
    // monitorexit(atomicBoolean)
    
    protected void showFetchErrorsToast() {
    }
    
    protected boolean showMdxInMenu() {
        return true;
    }
    
    protected boolean showSettingsInMenu() {
        return true;
    }
    
    protected boolean showSignOutInMenu() {
        return true;
    }
    
    protected void startLaunchActivityIfVisible() {
        if (this.isVisible && !(this instanceof LaunchActivity)) {
            Log.i("NetflixActivity", "Activity is visible, starting launch activity");
            this.startActivity(LaunchActivity.createStartIntent(this, "startLaunchActivityIfVisible()").addFlags(131072));
            return;
        }
        Log.v("NetflixActivity", "Activity is not visible, skipping launch of new activity");
    }
    
    public void updateVisibleDialog(final Dialog visibleDialog) {
        if (visibleDialog == null) {
            return;
        }
        synchronized (this.visibleDialogLock) {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
            this.visibleDialog = visibleDialog;
        }
    }
    
    private class DefaultManagerStatusListener implements ManagerStatusListener
    {
        private final ManagerStatusListener listener;
        
        public DefaultManagerStatusListener(final ManagerStatusListener listener) {
            this.listener = listener;
        }
        
        @Override
        public void onManagerReady(final ServiceManager serviceManager, final int n) {
            Log.d("NetflixActivity", "onManagerReady, status: " + n);
            if (!NetflixService.isServiceReady(n)) {
                NetflixActivity.this.startLaunchActivityIfVisible();
            }
            ((NetflixApplication)NetflixActivity.this.getApplication()).refreshLocale(serviceManager.getCurrentAppLocale());
            if (NetflixActivity.this.mdxFrag != null) {
                NetflixActivity.this.mdxFrag.onManagerReady(serviceManager, n);
                if (NetflixActivity.this.shouldExpandMiniPlayer) {
                    NetflixActivity.this.shouldExpandMiniPlayer = false;
                    NetflixActivity.this.handler.postDelayed((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            NetflixActivity.this.expandMiniPlayerIfVisible();
                        }
                    }, 400L);
                }
            }
            final DialogFragment dialogFragment = NetflixActivity.this.getDialogFragment();
            if (dialogFragment instanceof ManagerStatusListener) {
                ((ManagerStatusListener)dialogFragment).onManagerReady(serviceManager, n);
            }
            NetflixActivity.this.addMdxReceiver();
            NetflixActivity.this.addUserAgentUpdateReceiver();
            if (NetflixActivity.this.showMdxInMenu()) {
                NetflixActivity.this.invalidateOptionsMenu();
            }
            if (this.listener != null) {
                this.listener.onManagerReady(serviceManager, n);
            }
            if (!(NetflixActivity.this instanceof LaunchActivity)) {
                serviceManager.getClientLogging().getApplicationPerformanceMetricsLogging().endUiStartupSession(true, null);
            }
            serviceManager.getClientLogging().setDataContext(NetflixActivity.this.getDataContext());
            NetflixActivity.this.reportUiViewChanged(NetflixActivity.this.getUiScreen());
        }
        
        @Override
        public void onManagerUnavailable(final ServiceManager serviceManager, final int n) {
            Log.d("NetflixActivity", "onManagerUnavailable, status: " + n);
            if (NetflixActivity.this.mdxFrag != null) {
                NetflixActivity.this.mdxFrag.onManagerUnavailable(serviceManager, n);
            }
            final DialogFragment dialogFragment = NetflixActivity.this.getDialogFragment();
            if (dialogFragment instanceof ManagerStatusListener) {
                ((ManagerStatusListener)dialogFragment).onManagerUnavailable(serviceManager, n);
            }
            if (this.listener != null) {
                this.listener.onManagerUnavailable(serviceManager, n);
            }
            NetflixActivity.this.startLaunchActivityIfVisible();
            if (NetflixActivity.this.shouldFinishOnManagerError()) {
                Log.d("NetflixActivity", NetflixActivity.this.getClass().getSimpleName() + ": Finishing activity because manager error occured...");
                NetflixActivity.this.finish();
            }
        }
    }
}
