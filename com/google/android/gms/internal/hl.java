// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.Bundle;
import android.net.Uri;
import android.os.IInterface;

public interface hl extends IInterface
{
    void a(final hk p0, final Uri p1, final Bundle p2, final boolean p3) throws RemoteException;
    
    public abstract static class a extends Binder implements hl
    {
        public static hl as(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.gms.panorama.internal.IPanoramaService");
            if (queryLocalInterface != null && queryLocalInterface instanceof hl) {
                return (hl)queryLocalInterface;
            }
            return new hl.a.a(binder);
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.android.gms.panorama.internal.IPanoramaService");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.android.gms.panorama.internal.IPanoramaService");
                    final hk ar = hk.a.ar(parcel.readStrongBinder());
                    Uri uri;
                    if (parcel.readInt() != 0) {
                        uri = (Uri)Uri.CREATOR.createFromParcel(parcel);
                    }
                    else {
                        uri = null;
                    }
                    Bundle bundle;
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                    }
                    else {
                        bundle = null;
                    }
                    this.a(ar, uri, bundle, parcel.readInt() != 0);
                    return true;
                }
            }
        }
        
        private static class a implements hl
        {
            private IBinder dU;
            
            a(final IBinder du) {
                this.dU = du;
            }
            
            @Override
            public void a(final hk hk, final Uri uri, final Bundle bundle, final boolean b) throws RemoteException {
                while (true) {
                    IBinder binder = null;
                    int n = 1;
                    final Parcel obtain = Parcel.obtain();
                    while (true) {
                        Label_0134: {
                            Label_0119: {
                                try {
                                    obtain.writeInterfaceToken("com.google.android.gms.panorama.internal.IPanoramaService");
                                    if (hk != null) {
                                        binder = hk.asBinder();
                                    }
                                    obtain.writeStrongBinder(binder);
                                    if (uri != null) {
                                        obtain.writeInt(1);
                                        uri.writeToParcel(obtain, 0);
                                    }
                                    else {
                                        obtain.writeInt(0);
                                    }
                                    if (bundle != null) {
                                        obtain.writeInt(1);
                                        bundle.writeToParcel(obtain, 0);
                                        break Label_0134;
                                    }
                                    break Label_0119;
                                    obtain.writeInt(n);
                                    this.dU.transact(1, obtain, (Parcel)null, 1);
                                    return;
                                }
                                finally {
                                    obtain.recycle();
                                }
                            }
                            obtain.writeInt(0);
                        }
                        if (b) {
                            continue;
                        }
                        n = 0;
                        continue;
                    }
                }
            }
            
            public IBinder asBinder() {
                return this.dU;
            }
        }
    }
}
