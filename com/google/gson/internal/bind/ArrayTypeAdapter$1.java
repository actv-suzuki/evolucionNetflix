// 
// Decompiled by Procyon v0.5.30
// 

package com.google.gson.internal.bind;

import java.lang.reflect.Type;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.GenericArrayType;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;

final class ArrayTypeAdapter$1 implements TypeAdapterFactory
{
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        if (!(type instanceof GenericArrayType) && (!(type instanceof Class) || !((Class)type).isArray())) {
            return null;
        }
        final Type arrayComponentType = $Gson$Types.getArrayComponentType(type);
        return (TypeAdapter<T>)new ArrayTypeAdapter(gson, (TypeAdapter<Object>)gson.getAdapter(TypeToken.get(arrayComponentType)), (Class<Object>)$Gson$Types.getRawType(arrayComponentType));
    }
}
