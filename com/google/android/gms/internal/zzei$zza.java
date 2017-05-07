// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.internal;

import com.google.android.gms.dynamic.zzd;
import java.util.List;
import com.google.android.gms.ads.internal.formats.NativeAdOptionsParcel;
import com.google.android.gms.ads.internal.reward.mediation.client.zza$zza;
import com.google.android.gms.ads.internal.client.AdRequestParcel;
import com.google.android.gms.ads.internal.client.AdSizeParcel;
import com.google.android.gms.dynamic.zzd$zza;
import android.os.Parcel;
import android.os.IInterface;
import android.os.IBinder;
import android.os.Binder;

public abstract class zzei$zza extends Binder implements zzei
{
    public static zzei zzF(final IBinder binder) {
        if (binder == null) {
            return null;
        }
        final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
        if (queryLocalInterface != null && queryLocalInterface instanceof zzei) {
            return (zzei)queryLocalInterface;
        }
        return new zzei$zza$zza(binder);
    }
    
    public IBinder asBinder() {
        return (IBinder)this;
    }
    
    public boolean onTransact(int n, final Parcel parcel, final Parcel parcel2, final int n2) {
        final AdRequestParcel adRequestParcel = null;
        final AdRequestParcel adRequestParcel2 = null;
        final IBinder binder = null;
        final IBinder binder2 = null;
        final IBinder binder3 = null;
        switch (n) {
            default: {
                return super.onTransact(n, parcel, parcel2, n2);
            }
            case 1598968902: {
                parcel2.writeString("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                return true;
            }
            case 1: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final zzd zzbk = zzd$zza.zzbk(parcel.readStrongBinder());
                AdSizeParcel zzc;
                if (parcel.readInt() != 0) {
                    zzc = AdSizeParcel.CREATOR.zzc(parcel);
                }
                else {
                    zzc = null;
                }
                AdRequestParcel zzb;
                if (parcel.readInt() != 0) {
                    zzb = AdRequestParcel.CREATOR.zzb(parcel);
                }
                else {
                    zzb = null;
                }
                this.zza(zzbk, zzc, zzb, parcel.readString(), zzej$zza.zzG(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            }
            case 2: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final zzd view = this.getView();
                parcel2.writeNoException();
                IBinder binder4 = binder3;
                if (view != null) {
                    binder4 = view.asBinder();
                }
                parcel2.writeStrongBinder(binder4);
                return true;
            }
            case 3: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final zzd zzbk2 = zzd$zza.zzbk(parcel.readStrongBinder());
                AdRequestParcel zzb2 = adRequestParcel;
                if (parcel.readInt() != 0) {
                    zzb2 = AdRequestParcel.CREATOR.zzb(parcel);
                }
                this.zza(zzbk2, zzb2, parcel.readString(), zzej$zza.zzG(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            }
            case 4: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                this.showInterstitial();
                parcel2.writeNoException();
                return true;
            }
            case 5: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                this.destroy();
                parcel2.writeNoException();
                return true;
            }
            case 6: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final zzd zzbk3 = zzd$zza.zzbk(parcel.readStrongBinder());
                AdSizeParcel zzc2;
                if (parcel.readInt() != 0) {
                    zzc2 = AdSizeParcel.CREATOR.zzc(parcel);
                }
                else {
                    zzc2 = null;
                }
                AdRequestParcel zzb3;
                if (parcel.readInt() != 0) {
                    zzb3 = AdRequestParcel.CREATOR.zzb(parcel);
                }
                else {
                    zzb3 = null;
                }
                this.zza(zzbk3, zzc2, zzb3, parcel.readString(), parcel.readString(), zzej$zza.zzG(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            }
            case 7: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final zzd zzbk4 = zzd$zza.zzbk(parcel.readStrongBinder());
                AdRequestParcel zzb4;
                if (parcel.readInt() != 0) {
                    zzb4 = AdRequestParcel.CREATOR.zzb(parcel);
                }
                else {
                    zzb4 = null;
                }
                this.zza(zzbk4, zzb4, parcel.readString(), parcel.readString(), zzej$zza.zzG(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            }
            case 8: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                this.pause();
                parcel2.writeNoException();
                return true;
            }
            case 9: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                this.resume();
                parcel2.writeNoException();
                return true;
            }
            case 10: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final zzd zzbk5 = zzd$zza.zzbk(parcel.readStrongBinder());
                AdRequestParcel zzb5;
                if (parcel.readInt() != 0) {
                    zzb5 = AdRequestParcel.CREATOR.zzb(parcel);
                }
                else {
                    zzb5 = null;
                }
                this.zza(zzbk5, zzb5, parcel.readString(), zza$zza.zzad(parcel.readStrongBinder()), parcel.readString());
                parcel2.writeNoException();
                return true;
            }
            case 11: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                AdRequestParcel zzb6 = adRequestParcel2;
                if (parcel.readInt() != 0) {
                    zzb6 = AdRequestParcel.CREATOR.zzb(parcel);
                }
                this.zza(zzb6, parcel.readString());
                parcel2.writeNoException();
                return true;
            }
            case 12: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                this.showVideo();
                parcel2.writeNoException();
                return true;
            }
            case 13: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final boolean initialized = this.isInitialized();
                parcel2.writeNoException();
                if (initialized) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                parcel2.writeInt(n);
                return true;
            }
            case 14: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final zzd zzbk6 = zzd$zza.zzbk(parcel.readStrongBinder());
                AdRequestParcel zzb7;
                if (parcel.readInt() != 0) {
                    zzb7 = AdRequestParcel.CREATOR.zzb(parcel);
                }
                else {
                    zzb7 = null;
                }
                final String string = parcel.readString();
                final String string2 = parcel.readString();
                final zzej zzG = zzej$zza.zzG(parcel.readStrongBinder());
                NativeAdOptionsParcel zzf;
                if (parcel.readInt() != 0) {
                    zzf = NativeAdOptionsParcel.CREATOR.zzf(parcel);
                }
                else {
                    zzf = null;
                }
                this.zza(zzbk6, zzb7, string, string2, zzG, zzf, parcel.createStringArrayList());
                parcel2.writeNoException();
                return true;
            }
            case 15: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final zzek zzdS = this.zzdS();
                parcel2.writeNoException();
                IBinder binder5 = binder;
                if (zzdS != null) {
                    binder5 = zzdS.asBinder();
                }
                parcel2.writeStrongBinder(binder5);
                return true;
            }
            case 16: {
                parcel.enforceInterface("com.google.android.gms.ads.internal.mediation.client.IMediationAdapter");
                final zzel zzdT = this.zzdT();
                parcel2.writeNoException();
                IBinder binder6 = binder2;
                if (zzdT != null) {
                    binder6 = zzdT.asBinder();
                }
                parcel2.writeStrongBinder(binder6);
                return true;
            }
        }
    }
}