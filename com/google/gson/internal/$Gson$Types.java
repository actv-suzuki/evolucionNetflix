// 
// Decompiled by Procyon v0.5.30
// 

package com.google.gson.internal;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Properties;
import java.util.Collection;
import java.util.Arrays;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public final class $Gson$Types
{
    static final Type[] EMPTY_TYPE_ARRAY;
    
    static {
        EMPTY_TYPE_ARRAY = new Type[0];
    }
    
    public static GenericArrayType arrayOf(final Type type) {
        return new GenericArrayTypeImpl(type);
    }
    
    public static Type canonicalize(final Type type) {
        Object o;
        if (type instanceof Class) {
            final Class clazz = (Class)(o = type);
            if (clazz.isArray()) {
                o = new GenericArrayTypeImpl(canonicalize(clazz.getComponentType()));
            }
        }
        else {
            if (type instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType)type;
                return new ParameterizedTypeImpl(parameterizedType.getOwnerType(), parameterizedType.getRawType(), parameterizedType.getActualTypeArguments());
            }
            if (type instanceof GenericArrayType) {
                return new GenericArrayTypeImpl(((GenericArrayType)type).getGenericComponentType());
            }
            o = type;
            if (type instanceof WildcardType) {
                final WildcardType wildcardType = (WildcardType)type;
                return new WildcardTypeImpl(wildcardType.getUpperBounds(), wildcardType.getLowerBounds());
            }
        }
        return (Type)o;
    }
    
    private static void checkNotPrimitive(final Type type) {
        $Gson$Preconditions.checkArgument(!(type instanceof Class) || !((Class)type).isPrimitive());
    }
    
    private static Class<?> declaringClassOf(final TypeVariable<?> typeVariable) {
        final Object genericDeclaration = typeVariable.getGenericDeclaration();
        if (genericDeclaration instanceof Class) {
            return (Class<?>)genericDeclaration;
        }
        return null;
    }
    
    static boolean equal(final Object o, final Object o2) {
        return o == o2 || (o != null && o.equals(o2));
    }
    
    public static boolean equals(final Type type, final Type type2) {
        final boolean b = true;
        final boolean b2 = true;
        final boolean b3 = true;
        final boolean b4 = false;
        boolean b5;
        if (type == type2) {
            b5 = true;
        }
        else {
            if (type instanceof Class) {
                return type.equals(type2);
            }
            if (type instanceof ParameterizedType) {
                b5 = b4;
                if (type2 instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = (ParameterizedType)type;
                    final ParameterizedType parameterizedType2 = (ParameterizedType)type2;
                    return equal(parameterizedType.getOwnerType(), parameterizedType2.getOwnerType()) && parameterizedType.getRawType().equals(parameterizedType2.getRawType()) && Arrays.equals(parameterizedType.getActualTypeArguments(), parameterizedType2.getActualTypeArguments()) && b3;
                }
            }
            else if (type instanceof GenericArrayType) {
                b5 = b4;
                if (type2 instanceof GenericArrayType) {
                    return equals(((GenericArrayType)type).getGenericComponentType(), ((GenericArrayType)type2).getGenericComponentType());
                }
            }
            else if (type instanceof WildcardType) {
                b5 = b4;
                if (type2 instanceof WildcardType) {
                    final WildcardType wildcardType = (WildcardType)type;
                    final WildcardType wildcardType2 = (WildcardType)type2;
                    return Arrays.equals(wildcardType.getUpperBounds(), wildcardType2.getUpperBounds()) && Arrays.equals(wildcardType.getLowerBounds(), wildcardType2.getLowerBounds()) && b;
                }
            }
            else {
                b5 = b4;
                if (type instanceof TypeVariable) {
                    b5 = b4;
                    if (type2 instanceof TypeVariable) {
                        final TypeVariable typeVariable = (TypeVariable)type;
                        final TypeVariable typeVariable2 = (TypeVariable)type2;
                        return typeVariable.getGenericDeclaration() == typeVariable2.getGenericDeclaration() && typeVariable.getName().equals(typeVariable2.getName()) && b2;
                    }
                }
            }
        }
        return b5;
    }
    
    public static Type getArrayComponentType(final Type type) {
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType)type).getGenericComponentType();
        }
        return ((Class)type).getComponentType();
    }
    
    public static Type getCollectionElementType(Type supertype, final Class<?> clazz) {
        final Type type = supertype = getSupertype(supertype, clazz, Collection.class);
        if (type instanceof WildcardType) {
            supertype = ((WildcardType)type).getUpperBounds()[0];
        }
        if (supertype instanceof ParameterizedType) {
            return ((ParameterizedType)supertype).getActualTypeArguments()[0];
        }
        return Object.class;
    }
    
    static Type getGenericSupertype(final Type type, Class<?> clazz, final Class<?> clazz2) {
        if (clazz2 == clazz) {
            return type;
        }
        if (clazz2.isInterface()) {
            final Class<?>[] interfaces = clazz.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                if (interfaces[i] == clazz2) {
                    return clazz.getGenericInterfaces()[i];
                }
                if (clazz2.isAssignableFrom(interfaces[i])) {
                    return getGenericSupertype(clazz.getGenericInterfaces()[i], interfaces[i], clazz2);
                }
            }
        }
        if (!clazz.isInterface()) {
            while (clazz != Object.class) {
                final Class<? super Object> superclass = clazz.getSuperclass();
                if (superclass == clazz2) {
                    return clazz.getGenericSuperclass();
                }
                if (clazz2.isAssignableFrom(superclass)) {
                    return getGenericSupertype(clazz.getGenericSuperclass(), superclass, clazz2);
                }
                clazz = (Class<Object>)superclass;
            }
        }
        return clazz2;
    }
    
    public static Type[] getMapKeyAndValueTypes(Type supertype, final Class<?> clazz) {
        if (supertype == Properties.class) {
            return new Type[] { String.class, String.class };
        }
        supertype = getSupertype(supertype, clazz, Map.class);
        if (supertype instanceof ParameterizedType) {
            return ((ParameterizedType)supertype).getActualTypeArguments();
        }
        return new Type[] { Object.class, Object.class };
    }
    
    public static Class<?> getRawType(Type rawType) {
        if (rawType instanceof Class) {
            return (Class<?>)rawType;
        }
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType)rawType).getRawType();
            $Gson$Preconditions.checkArgument(rawType instanceof Class);
            return (Class<?>)rawType;
        }
        if (rawType instanceof GenericArrayType) {
            return Array.newInstance(getRawType(((GenericArrayType)rawType).getGenericComponentType()), 0).getClass();
        }
        if (rawType instanceof TypeVariable) {
            return Object.class;
        }
        if (rawType instanceof WildcardType) {
            return getRawType(((WildcardType)rawType).getUpperBounds()[0]);
        }
        String name;
        if (rawType == null) {
            name = "null";
        }
        else {
            name = rawType.getClass().getName();
        }
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + rawType + "> is of type " + name);
    }
    
    static Type getSupertype(final Type type, final Class<?> clazz, final Class<?> clazz2) {
        $Gson$Preconditions.checkArgument(clazz2.isAssignableFrom(clazz));
        return resolve(type, clazz, getGenericSupertype(type, clazz, clazz2));
    }
    
    private static int hashCodeOrZero(final Object o) {
        if (o != null) {
            return o.hashCode();
        }
        return 0;
    }
    
    private static int indexOf(final Object[] array, final Object o) {
        for (int i = 0; i < array.length; ++i) {
            if (o.equals(array[i])) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }
    
    public static ParameterizedType newParameterizedTypeWithOwner(final Type type, final Type type2, final Type... array) {
        return new ParameterizedTypeImpl(type, type2, array);
    }
    
    public static Type resolve(Type type, final Class<?> clazz, Type type2) {
        Type type3;
        for (type3 = type2; type3 instanceof TypeVariable; type3 = type2) {
            final TypeVariable<?> typeVariable = (TypeVariable<?>)type3;
            type2 = resolveTypeVariable(type, clazz, typeVariable);
            if (type2 == typeVariable) {
                return type2;
            }
        }
        if (type3 instanceof Class && ((Class)type3).isArray()) {
            type2 = type3;
            final Class<?> componentType = ((Class)type2).getComponentType();
            type = resolve(type, clazz, componentType);
            if (componentType != type) {
                return arrayOf(type);
            }
            return type2;
        }
        else if (type3 instanceof GenericArrayType) {
            type2 = type3;
            final Type genericComponentType = ((GenericArrayType)type2).getGenericComponentType();
            type = resolve(type, clazz, genericComponentType);
            if (genericComponentType != type) {
                return arrayOf(type);
            }
            return type2;
        }
        else if (type3 instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type3;
            type2 = parameterizedType.getOwnerType();
            final Type resolve = resolve(type, clazz, type2);
            int n;
            if (resolve != type2) {
                n = 1;
            }
            else {
                n = 0;
            }
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Type[] array;
            int n2;
            for (int length = actualTypeArguments.length, i = 0; i < length; ++i, actualTypeArguments = array, n = n2) {
                final Type resolve2 = resolve(type, clazz, actualTypeArguments[i]);
                array = actualTypeArguments;
                n2 = n;
                if (resolve2 != actualTypeArguments[i]) {
                    array = actualTypeArguments;
                    if ((n2 = n) == 0) {
                        array = actualTypeArguments.clone();
                        n2 = 1;
                    }
                    array[i] = resolve2;
                }
            }
            type2 = parameterizedType;
            if (n != 0) {
                return newParameterizedTypeWithOwner(resolve, parameterizedType.getRawType(), actualTypeArguments);
            }
            return type2;
        }
        else {
            type2 = type3;
            if (!(type3 instanceof WildcardType)) {
                return type2;
            }
            final WildcardType wildcardType = (WildcardType)type3;
            final Type[] lowerBounds = wildcardType.getLowerBounds();
            final Type[] upperBounds = wildcardType.getUpperBounds();
            if (lowerBounds.length == 1) {
                type = resolve(type, clazz, lowerBounds[0]);
                type2 = wildcardType;
                if (type != lowerBounds[0]) {
                    return supertypeOf(type);
                }
                return type2;
            }
            else {
                type2 = wildcardType;
                if (upperBounds.length != 1) {
                    return type2;
                }
                type = resolve(type, clazz, upperBounds[0]);
                type2 = wildcardType;
                if (type != upperBounds[0]) {
                    return subtypeOf(type);
                }
                return type2;
            }
        }
    }
    
    static Type resolveTypeVariable(Type genericSupertype, final Class<?> clazz, final TypeVariable<?> typeVariable) {
        final Class<?> declaringClass = declaringClassOf(typeVariable);
        if (declaringClass != null) {
            genericSupertype = getGenericSupertype(genericSupertype, clazz, declaringClass);
            if (genericSupertype instanceof ParameterizedType) {
                return ((ParameterizedType)genericSupertype).getActualTypeArguments()[indexOf(declaringClass.getTypeParameters(), typeVariable)];
            }
        }
        return typeVariable;
    }
    
    public static WildcardType subtypeOf(final Type type) {
        return new WildcardTypeImpl(new Type[] { type }, $Gson$Types.EMPTY_TYPE_ARRAY);
    }
    
    public static WildcardType supertypeOf(final Type type) {
        return new WildcardTypeImpl(new Type[] { Object.class }, new Type[] { type });
    }
    
    public static String typeToString(final Type type) {
        if (type instanceof Class) {
            return ((Class)type).getName();
        }
        return type.toString();
    }
    
    private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Type componentType;
        
        public GenericArrayTypeImpl(final Type type) {
            this.componentType = $Gson$Types.canonicalize(type);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof GenericArrayType && $Gson$Types.equals(this, (Type)o);
        }
        
        @Override
        public Type getGenericComponentType() {
            return this.componentType;
        }
        
        @Override
        public int hashCode() {
            return this.componentType.hashCode();
        }
        
        @Override
        public String toString() {
            return $Gson$Types.typeToString(this.componentType) + "[]";
        }
    }
    
    private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;
        
        public ParameterizedTypeImpl(Type canonicalize, final Type type, final Type... array) {
            final boolean b = true;
            int i = 0;
            if (type instanceof Class) {
                final Class clazz = (Class)type;
                $Gson$Preconditions.checkArgument(canonicalize != null || clazz.getEnclosingClass() == null);
                boolean b2 = b;
                if (canonicalize != null) {
                    b2 = (clazz.getEnclosingClass() != null && b);
                }
                $Gson$Preconditions.checkArgument(b2);
            }
            if (canonicalize == null) {
                canonicalize = null;
            }
            else {
                canonicalize = $Gson$Types.canonicalize(canonicalize);
            }
            this.ownerType = canonicalize;
            this.rawType = $Gson$Types.canonicalize(type);
            this.typeArguments = array.clone();
            while (i < this.typeArguments.length) {
                $Gson$Preconditions.checkNotNull(this.typeArguments[i]);
                checkNotPrimitive(this.typeArguments[i]);
                this.typeArguments[i] = $Gson$Types.canonicalize(this.typeArguments[i]);
                ++i;
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof ParameterizedType && $Gson$Types.equals(this, (Type)o);
        }
        
        @Override
        public Type[] getActualTypeArguments() {
            return this.typeArguments.clone();
        }
        
        @Override
        public Type getOwnerType() {
            return this.ownerType;
        }
        
        @Override
        public Type getRawType() {
            return this.rawType;
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ hashCodeOrZero(this.ownerType);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder((this.typeArguments.length + 1) * 30);
            sb.append($Gson$Types.typeToString(this.rawType));
            if (this.typeArguments.length == 0) {
                return sb.toString();
            }
            sb.append("<").append($Gson$Types.typeToString(this.typeArguments[0]));
            for (int i = 1; i < this.typeArguments.length; ++i) {
                sb.append(", ").append($Gson$Types.typeToString(this.typeArguments[i]));
            }
            return sb.append(">").toString();
        }
    }
    
    private static final class WildcardTypeImpl implements WildcardType, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Type lowerBound;
        private final Type upperBound;
        
        public WildcardTypeImpl(final Type[] array, final Type[] array2) {
            final boolean b = true;
            $Gson$Preconditions.checkArgument(array2.length <= 1);
            $Gson$Preconditions.checkArgument(array.length == 1);
            if (array2.length == 1) {
                $Gson$Preconditions.checkNotNull(array2[0]);
                checkNotPrimitive(array2[0]);
                $Gson$Preconditions.checkArgument(array[0] == Object.class && b);
                this.lowerBound = $Gson$Types.canonicalize(array2[0]);
                this.upperBound = Object.class;
                return;
            }
            $Gson$Preconditions.checkNotNull(array[0]);
            checkNotPrimitive(array[0]);
            this.lowerBound = null;
            this.upperBound = $Gson$Types.canonicalize(array[0]);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof WildcardType && $Gson$Types.equals(this, (Type)o);
        }
        
        @Override
        public Type[] getLowerBounds() {
            if (this.lowerBound != null) {
                return new Type[] { this.lowerBound };
            }
            return $Gson$Types.EMPTY_TYPE_ARRAY;
        }
        
        @Override
        public Type[] getUpperBounds() {
            return new Type[] { this.upperBound };
        }
        
        @Override
        public int hashCode() {
            int n;
            if (this.lowerBound != null) {
                n = this.lowerBound.hashCode() + 31;
            }
            else {
                n = 1;
            }
            return n ^ this.upperBound.hashCode() + 31;
        }
        
        @Override
        public String toString() {
            if (this.lowerBound != null) {
                return "? super " + $Gson$Types.typeToString(this.lowerBound);
            }
            if (this.upperBound == Object.class) {
                return "?";
            }
            return "? extends " + $Gson$Types.typeToString(this.upperBound);
        }
    }
}
