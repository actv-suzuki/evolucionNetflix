// 
// Decompiled by Procyon v0.5.30
// 

package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public final class fo
{
    public static a e(final Object o) {
        return new a(o);
    }
    
    public static boolean equal(final Object o, final Object o2) {
        return o == o2 || (o != null && o.equals(o2));
    }
    
    public static int hashCode(final Object... array) {
        return Arrays.hashCode(array);
    }
    
    public static final class a
    {
        private final List<String> DI;
        private final Object DJ;
        
        private a(final Object o) {
            this.DJ = fq.f(o);
            this.DI = new ArrayList<String>();
        }
        
        public a a(final String s, final Object o) {
            this.DI.add(fq.f(s) + "=" + String.valueOf(o));
            return this;
        }
        
        @Override
        public String toString() {
            final StringBuilder append = new StringBuilder(100).append(this.DJ.getClass().getSimpleName()).append('{');
            for (int size = this.DI.size(), i = 0; i < size; ++i) {
                append.append(this.DI.get(i));
                if (i < size - 1) {
                    append.append(", ");
                }
            }
            return append.append('}').toString();
        }
    }
}
