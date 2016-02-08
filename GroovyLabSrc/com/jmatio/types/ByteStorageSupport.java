package com.jmatio.types;

import java.nio.ByteBuffer;

public interface ByteStorageSupport<T extends Number>
{
    int getBytesAllocated();
    T buldFromBytes( byte[] bytes );
    byte[] getByteArray ( T value );
    Class getStorageClazz();

}
