// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.maps.internal;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import com.google.android.gms.maps.model.CameraPosition;
import android.os.IInterface;

public interface e extends IInterface
{
    void onCameraChange(final CameraPosition p0) throws RemoteException;
    
    public abstract static class a extends Binder implements e
    {
        public a() {
            this.attachInterface((IInterface)this, "com.google.android.gms.maps.internal.IOnCameraChangeListener");
        }
        
        public static e ah(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.gms.maps.internal.IOnCameraChangeListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof e) {
                return (e)queryLocalInterface;
            }
            return new e.a.a(binder);
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
                    parcel2.writeString("com.google.android.gms.maps.internal.IOnCameraChangeListener");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.android.gms.maps.internal.IOnCameraChangeListener");
                    CameraPosition fromParcel;
                    if (parcel.readInt() != 0) {
                        fromParcel = CameraPosition.CREATOR.createFromParcel(parcel);
                    }
                    else {
                        fromParcel = null;
                    }
                    this.onCameraChange(fromParcel);
                    parcel2.writeNoException();
                    return true;
                }
            }
        }
        
        private static class a implements e
        {
            private IBinder kn;
            
            a(final IBinder kn) {
                this.kn = kn;
            }
            
            public IBinder asBinder() {
                return this.kn;
            }
            
            @Override
            public void onCameraChange(final CameraPosition cameraPosition) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.gms.maps.internal.IOnCameraChangeListener");
                    if (cameraPosition != null) {
                        obtain.writeInt(1);
                        cameraPosition.writeToParcel(obtain, 0);
                    }
                    else {
                        obtain.writeInt(0);
                    }
                    this.kn.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
