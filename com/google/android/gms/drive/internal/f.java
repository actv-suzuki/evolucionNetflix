// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.drive.internal;

import com.google.android.gms.common.internal.safeparcel.a;
import com.google.android.gms.common.internal.safeparcel.b;
import android.os.Parcel;
import android.os.Parcelable$Creator;

public class f implements Parcelable$Creator<CreateContentsRequest>
{
    static void a(final CreateContentsRequest createContentsRequest, final Parcel parcel, int p3) {
        p3 = b.p(parcel);
        b.c(parcel, 1, createContentsRequest.xH);
        b.F(parcel, p3);
    }
    
    public CreateContentsRequest G(final Parcel parcel) {
        final int o = a.o(parcel);
        int g = 0;
        while (parcel.dataPosition() < o) {
            final int n = a.n(parcel);
            switch (a.R(n)) {
                default: {
                    a.b(parcel, n);
                    continue;
                }
                case 1: {
                    g = a.g(parcel, n);
                    continue;
                }
            }
        }
        if (parcel.dataPosition() != o) {
            throw new a.a("Overread allowed size end=" + o, parcel);
        }
        return new CreateContentsRequest(g);
    }
    
    public CreateContentsRequest[] ak(final int n) {
        return new CreateContentsRequest[n];
    }
}
