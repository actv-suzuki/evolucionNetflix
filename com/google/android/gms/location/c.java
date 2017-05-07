// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.location;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.location.Location;
import android.os.IInterface;

public interface c extends IInterface
{
    void onLocationChanged(final Location p0) throws RemoteException;
    
    public abstract static class a extends Binder implements c
    {
        public a() {
            this.attachInterface((IInterface)this, "com.google.android.gms.location.ILocationListener");
        }
        
        public static c I(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.gms.location.ILocationListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof c) {
                return (c)queryLocalInterface;
            }
            return new c.a.a(binder);
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
                    parcel2.writeString("com.google.android.gms.location.ILocationListener");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.android.gms.location.ILocationListener");
                    Location location;
                    if (parcel.readInt() != 0) {
                        location = (Location)Location.CREATOR.createFromParcel(parcel);
                    }
                    else {
                        location = null;
                    }
                    this.onLocationChanged(location);
                    return true;
                }
            }
        }
        
        private static class a implements c
        {
            private IBinder dU;
            
            a(final IBinder du) {
                this.dU = du;
            }
            
            public IBinder asBinder() {
                return this.dU;
            }
            
            @Override
            public void onLocationChanged(final Location location) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.location.ILocationListener");
                    if (location != null) {
                        obtain.writeInt(1);
                        location.writeToParcel(obtain, 0);
                    }
                    else {
                        obtain.writeInt(0);
                    }
                    this.dU.transact(1, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}