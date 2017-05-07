// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface bb extends IInterface
{
    bc l(final String p0) throws RemoteException;
    
    public abstract static class a extends Binder implements bb
    {
        public a() {
            this.attachInterface((IInterface)this, "com.google.android.gms.ads.internal.mediation.client.IAdapterCreator");
        }
        
        public static bb i(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.gms.ads.internal.mediation.client.IAdapterCreator");
            if (queryLocalInterface != null && queryLocalInterface instanceof bb) {
                return (bb)queryLocalInterface;
            }
            return new bb.a.a(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.android.gms.ads.internal.mediation.client.IAdapterCreator");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IAdapterCreator");
                    final bc l = this.l(parcel.readString());
                    parcel2.writeNoException();
                    IBinder binder;
                    if (l != null) {
                        binder = l.asBinder();
                    }
                    else {
                        binder = null;
                    }
                    parcel2.writeStrongBinder(binder);
                    return true;
                }
            }
        }
        
        private static class a implements bb
        {
            private IBinder dU;
            
            a(final IBinder du) {
                this.dU = du;
            }
            
            public IBinder asBinder() {
                return this.dU;
            }
            
            @Override
            public bc l(final String s) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.ads.internal.mediation.client.IAdapterCreator");
                    obtain.writeString(s);
                    this.dU.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return bc.a.j(obtain2.readStrongBinder());
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}