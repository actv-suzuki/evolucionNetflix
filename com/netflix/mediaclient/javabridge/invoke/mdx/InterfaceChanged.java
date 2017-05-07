// 
// Decompiled by Procyon v0.5.30
// 

package com.netflix.mediaclient.javabridge.invoke.mdx;

import android.net.wifi.WifiInfo;
import org.json.JSONException;
import com.netflix.mediaclient.Log;
import android.net.wifi.WifiManager;
import com.netflix.mediaclient.util.ConnectivityUtils;
import org.json.JSONObject;
import android.content.Context;
import com.netflix.mediaclient.javabridge.invoke.BaseInvoke;

public class InterfaceChanged extends BaseInvoke
{
    private static final String METHOD = "InterfaceChanged";
    private static final String PROPERTY_CONNECTED = "connected";
    private static final String PROPERTY_IPADDRESS = "ipaddress";
    private static final String PROPERTY_NEW_INTERFACE = "newInterface";
    private static final String PROPERTY_SSID = "ssid";
    private static final String TAG = "nf_invoke";
    private static final String TARGET = "mdx";
    
    public InterfaceChanged(final Context arguments) {
        super("mdx", "InterfaceChanged");
        if (arguments == null) {
            throw new IllegalArgumentException("Context is null!");
        }
        this.setArguments(arguments);
    }
    
    public InterfaceChanged(final boolean b, final boolean b2, final String s, final String s2) {
        super("mdx", "InterfaceChanged");
        this.setArguments(b, b2, s, s2);
    }
    
    private void setArguments(Context localIP4Address) {
    Label_0144_Outer:
        while (true) {
            while (true) {
                String ssid = null;
                Label_0243: {
                    while (true) {
                        try {
                            final JSONObject jsonObject = new JSONObject();
                            try {
                                final String networkType = ConnectivityUtils.getNetworkType((Context)localIP4Address);
                                jsonObject.put("newInterface", (Object)networkType);
                                jsonObject.put("connected", (Object)String.valueOf(ConnectivityUtils.isConnected((Context)localIP4Address)));
                                final String s = ssid = null;
                                if (!"WIFI".equals(networkType)) {
                                    break Label_0243;
                                }
                                final WifiManager wifiManager = (WifiManager)((Context)localIP4Address).getSystemService("wifi");
                                ssid = s;
                                if (wifiManager == null) {
                                    break Label_0243;
                                }
                                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                                ssid = s;
                                if (connectionInfo != null) {
                                    if (Log.isLoggable("nf_invoke", 3)) {
                                        Log.d("nf_invoke", connectionInfo.toString());
                                        Log.d("nf_invoke", "" + connectionInfo.getSSID());
                                    }
                                    ssid = connectionInfo.getSSID();
                                }
                                break Label_0243;
                                // iftrue(Label_0196:, !Log.isLoggable("nf_invoke", 3))
                                // iftrue(Label_0215:, localIP4Address == null)
                                final String s2;
                                Label_0205:Block_9_Outer:
                                while (true) {
                                    jsonObject.put("ipaddress", (Object)localIP4Address);
                                    break Label_0205;
                                    Label_0215: {
                                        jsonObject.put("ipaddress", (Object)"");
                                    }
                                    break Label_0205;
                                Block_10:
                                    while (true) {
                                        break Block_10;
                                        jsonObject.put("ssid", (Object)s2);
                                        localIP4Address = (JSONException)ConnectivityUtils.getLocalIP4Address((Context)localIP4Address);
                                        continue Label_0144_Outer;
                                    }
                                    Log.d("nf_invoke", "LocalIPAddress:" + (String)localIP4Address);
                                    continue Block_9_Outer;
                                }
                                this.arguments = jsonObject.toString();
                                return;
                            }
                            catch (JSONException ex) {}
                            Log.e("nf_invoke", "Failed to create JSON object", (Throwable)localIP4Address);
                            return;
                        }
                        catch (JSONException localIP4Address) {
                            continue;
                        }
                        break;
                    }
                }
                String s2;
                if ((s2 = ssid) == null) {
                    s2 = "";
                    continue;
                }
                continue;
            }
        }
    }
    
    private void setArguments(final boolean b, final boolean b2, final String ex, final String s) {
    Label_0040_Outer:
        while (true) {
            while (true) {
            Label_0124:
                while (true) {
                    try {
                        final JSONObject jsonObject = new JSONObject();
                        Label_0110: {
                            if (!b) {
                                break Label_0110;
                            }
                        Label_0100_Outer:
                            while (true) {
                                Label_0134: {
                                    while (true) {
                                        try {
                                            jsonObject.put("newInterface", (Object)"MOBILE");
                                            while (true) {
                                                if (b2) {
                                                    jsonObject.put("connected", (Object)"true");
                                                    break;
                                                }
                                                break Label_0134;
                                                jsonObject.put("newInterface", (Object)"WIFI");
                                                continue Label_0100_Outer;
                                            }
                                            this.arguments = jsonObject.toString();
                                            return;
                                            final Object o;
                                            jsonObject.put("ssid", o);
                                            // iftrue(Label_0147:, s == null)
                                            // iftrue(Label_0090:, !Log.isLoggable("nf_invoke", 3))
                                            Log.d("nf_invoke", "LocalIPAddress:" + s);
                                            Label_0090: {
                                                jsonObject.put("ipaddress", (Object)s);
                                            }
                                            continue Label_0040_Outer;
                                        }
                                        catch (JSONException ex2) {}
                                        break Label_0124;
                                        Label_0147: {
                                            jsonObject.put("ipaddress", (Object)"");
                                        }
                                        continue Label_0040_Outer;
                                    }
                                    Log.e("nf_invoke", "Failed to create JSON object", (Throwable)ex);
                                    return;
                                }
                                jsonObject.put("connected", (Object)"false");
                                break;
                            }
                        }
                    }
                    catch (JSONException ex) {
                        continue Label_0124;
                    }
                    break;
                }
                Object o = ex;
                if (ex == null) {
                    o = "";
                    continue;
                }
                continue;
            }
        }
    }
}
