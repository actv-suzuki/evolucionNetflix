// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.wearable.internal;

import android.os.Bundle;
import android.net.Uri;
import com.google.android.gms.common.internal.safeparcel.a;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.b;
import android.os.Parcel;
import android.os.Parcelable$Creator;

public class n implements Parcelable$Creator<m>
{
    static void a(final m m, final Parcel parcel, final int n) {
        final int d = b.D(parcel);
        b.c(parcel, 1, m.BR);
        b.a(parcel, 2, (Parcelable)m.getUri(), n, false);
        b.a(parcel, 4, m.pR(), false);
        b.a(parcel, 5, m.getData(), false);
        b.H(parcel, d);
    }
    
    public m dU(final Parcel parcel) {
        byte[] r = null;
        final int c = a.C(parcel);
        int g = 0;
        Bundle bundle = null;
        Uri uri = null;
        while (parcel.dataPosition() < c) {
            final int b = a.B(parcel);
            Uri uri2 = null;
            Bundle bundle3 = null;
            switch (a.aD(b)) {
                default: {
                    a.b(parcel, b);
                    final Bundle bundle2 = bundle;
                    uri2 = uri;
                    bundle3 = bundle2;
                    break;
                }
                case 1: {
                    g = a.g(parcel, b);
                    final Uri uri3 = uri;
                    bundle3 = bundle;
                    uri2 = uri3;
                    break;
                }
                case 2: {
                    final Uri uri4 = a.a(parcel, b, (android.os.Parcelable$Creator<Uri>)Uri.CREATOR);
                    bundle3 = bundle;
                    uri2 = uri4;
                    break;
                }
                case 4: {
                    final Bundle q = a.q(parcel, b);
                    uri2 = uri;
                    bundle3 = q;
                    break;
                }
                case 5: {
                    r = a.r(parcel, b);
                    final Uri uri5 = uri;
                    bundle3 = bundle;
                    uri2 = uri5;
                    break;
                }
            }
            final Uri uri6 = uri2;
            bundle = bundle3;
            uri = uri6;
        }
        if (parcel.dataPosition() != c) {
            throw new a.a("Overread allowed size end=" + c, parcel);
        }
        return new m(g, uri, bundle, r);
    }
    
    public m[] fW(final int n) {
        return new m[n];
    }
}