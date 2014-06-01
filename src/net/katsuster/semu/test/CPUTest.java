package net.katsuster.semu.test;

import static org.junit.Assert.*;

import net.katsuster.semu.*;

public class CPUTest {
    @org.junit.Test
    public void testReadMasked() throws Exception {
        String msg1 = "CPUTest.readMasked() failed.";
        CPU a = new CPU();

        //bus:16bits, data:8bits
        assertEquals(msg1, (byte)0x54, (byte) a.readMasked(0x0, 0xfe54, 16, 8));
        assertEquals(msg1, (byte)0xfe, (byte) a.readMasked(0x1, 0xfe54, 16, 8));

        assertEquals(msg1, (byte)0x54, (byte) a.readMasked(0xfffffffffffffff0L, 0xfe54, 16, 8));
        assertEquals(msg1, (byte)0xfe, (byte) a.readMasked(0xfffffffffffffff1L, 0xfe54, 16, 8));

        //bus:32bits, data:8, 16bits
        assertEquals(msg1, (byte)0x32, (byte)a.readMasked(0x10, 0xfedc5432, 32, 8));
        assertEquals(msg1, (byte)0x54, (byte)a.readMasked(0x11, 0xfedc5432, 32, 8));
        assertEquals(msg1, (byte)0xdc, (byte)a.readMasked(0x12, 0xfedc5432, 32, 8));
        assertEquals(msg1, (byte)0xfe, (byte)a.readMasked(0x13, 0xfedc5432, 32, 8));

        assertEquals(msg1, (short)0x5432, (short)a.readMasked(0x20, 0xfedc5432, 32, 16));
        assertEquals(msg1, (short)0x5432, (short)a.readMasked(0x21, 0xfedc5432, 32, 16));
        assertEquals(msg1, (short)0xfedc, (short)a.readMasked(0x22, 0xfedc5432, 32, 16));
        assertEquals(msg1, (short)0xfedc, (short)a.readMasked(0x23, 0xfedc5432, 32, 16));

        //bus:64bits, data:8, 16, 32bits
        assertEquals(msg1, (byte)0x10, (byte)a.readMasked(0x30, 0xfedcba9876543210L, 64, 8));
        assertEquals(msg1, (byte)0x32, (byte)a.readMasked(0x31, 0xfedcba9876543210L, 64, 8));
        assertEquals(msg1, (byte)0x54, (byte)a.readMasked(0x32, 0xfedcba9876543210L, 64, 8));
        assertEquals(msg1, (byte)0x76, (byte)a.readMasked(0x33, 0xfedcba9876543210L, 64, 8));
        assertEquals(msg1, (byte)0x98, (byte)a.readMasked(0x34, 0xfedcba9876543210L, 64, 8));
        assertEquals(msg1, (byte)0xba, (byte)a.readMasked(0x35, 0xfedcba9876543210L, 64, 8));
        assertEquals(msg1, (byte)0xdc, (byte)a.readMasked(0x36, 0xfedcba9876543210L, 64, 8));
        assertEquals(msg1, (byte)0xfe, (byte)a.readMasked(0x37, 0xfedcba9876543210L, 64, 8));

        assertEquals(msg1, (short)0x3210, (short)a.readMasked(0x40, 0xfedcba9876543210L, 64, 16));
        assertEquals(msg1, (short)0x3210, (short)a.readMasked(0x41, 0xfedcba9876543210L, 64, 16));
        assertEquals(msg1, (short)0x7654, (short)a.readMasked(0x42, 0xfedcba9876543210L, 64, 16));
        assertEquals(msg1, (short)0x7654, (short)a.readMasked(0x43, 0xfedcba9876543210L, 64, 16));
        assertEquals(msg1, (short)0xba98, (short)a.readMasked(0x44, 0xfedcba9876543210L, 64, 16));
        assertEquals(msg1, (short)0xba98, (short)a.readMasked(0x45, 0xfedcba9876543210L, 64, 16));
        assertEquals(msg1, (short)0xfedc, (short)a.readMasked(0x46, 0xfedcba9876543210L, 64, 16));
        assertEquals(msg1, (short)0xfedc, (short)a.readMasked(0x47, 0xfedcba9876543210L, 64, 16));

        assertEquals(msg1, (int)0x76543210, (int)a.readMasked(0x50, 0xfedcba9876543210L, 64, 32));
        assertEquals(msg1, (int)0x76543210, (int)a.readMasked(0x51, 0xfedcba9876543210L, 64, 32));
        assertEquals(msg1, (int)0x76543210, (int)a.readMasked(0x52, 0xfedcba9876543210L, 64, 32));
        assertEquals(msg1, (int)0x76543210, (int)a.readMasked(0x53, 0xfedcba9876543210L, 64, 32));
        assertEquals(msg1, (int)0xfedcba98, (int)a.readMasked(0x54, 0xfedcba9876543210L, 64, 32));
        assertEquals(msg1, (int)0xfedcba98, (int)a.readMasked(0x55, 0xfedcba9876543210L, 64, 32));
        assertEquals(msg1, (int)0xfedcba98, (int)a.readMasked(0x56, 0xfedcba9876543210L, 64, 32));
        assertEquals(msg1, (int)0xfedcba98, (int)a.readMasked(0x57, 0xfedcba9876543210L, 64, 32));
    }

    @org.junit.Test
    public void testWriteMasked() throws Exception {
        String msg1 = "CPUTest.writeMasked() failed.";
        CPU a = new CPU();

        //bus:16bits, data:8bits
        assertEquals(msg1, (short)0xfef0, (short)a.writeMasked(0x60, 0xfe54, 0x1f0, 16, 8));
        assertEquals(msg1, (short)0xf054, (short)a.writeMasked(0x61, 0xfe54, 0x2f0, 16, 8));

        assertEquals(msg1, (short)0xfe0f, (short)a.writeMasked(0xfffffffffffffff0L, 0xfe54, 0xe0f, 16, 8));
        assertEquals(msg1, (short)0x0f54, (short)a.writeMasked(0xfffffffffffffff1L, 0xfe54, 0xf0f, 16, 8));

        //bus:32bits, data:8, 16bits
        assertEquals(msg1, (int)0xcdef23f9, (int)a.writeMasked(0x70, 0xcdef2345, 0x1f9, 32, 8));
        assertEquals(msg1, (int)0xcdeff845, (int)a.writeMasked(0x71, 0xcdef2345, 0x2f8, 32, 8));
        assertEquals(msg1, (int)0xcdf72345, (int)a.writeMasked(0x72, 0xcdef2345, 0x3f7, 32, 8));
        assertEquals(msg1, (int)0xf6ef2345, (int)a.writeMasked(0x73, 0xcdef2345, 0x4f6, 32, 8));

        assertEquals(msg1, (int)0xcdefe123, (int)a.writeMasked(0x80, 0xcdef2345, 0x5e123, 32, 16));
        assertEquals(msg1, (int)0xcdefe124, (int)a.writeMasked(0x81, 0xcdef2345, 0x6e124, 32, 16));
        assertEquals(msg1, (int)0xe1252345, (int)a.writeMasked(0x82, 0xcdef2345, 0x7e125, 32, 16));
        assertEquals(msg1, (int)0xe1262345, (int)a.writeMasked(0x83, 0xcdef2345, 0x8e126, 32, 16));

        //bus:64bits, data:8, 16, 32bits
        assertEquals(msg1, 0x89abcdef234567f9L, a.writeMasked(0x90, 0x89abcdef23456789L, 0x11f9, 64, 8));
        assertEquals(msg1, 0x89abcdef2345f889L, a.writeMasked(0x91, 0x89abcdef23456789L, 0x21f8, 64, 8));
        assertEquals(msg1, 0x89abcdef23f76789L, a.writeMasked(0x92, 0x89abcdef23456789L, 0x31f7, 64, 8));
        assertEquals(msg1, 0x89abcdeff6456789L, a.writeMasked(0x93, 0x89abcdef23456789L, 0x41f6, 64, 8));
        assertEquals(msg1, 0x89abcdf523456789L, a.writeMasked(0x94, 0x89abcdef23456789L, 0x51f5, 64, 8));
        assertEquals(msg1, 0x89abf4ef23456789L, a.writeMasked(0x95, 0x89abcdef23456789L, 0x61f4, 64, 8));
        assertEquals(msg1, 0x89f3cdef23456789L, a.writeMasked(0x96, 0x89abcdef23456789L, 0x71f3, 64, 8));
        assertEquals(msg1, 0xf2abcdef23456789L, a.writeMasked(0x97, 0x89abcdef23456789L, 0x81f2, 64, 8));

        assertEquals(msg1, 0x89abcdef2345f9e8L, a.writeMasked(0xa0, 0x89abcdef23456789L, 0x12f9e8, 64, 16));
        assertEquals(msg1, 0x89abcdef2345f9e7L, a.writeMasked(0xa1, 0x89abcdef23456789L, 0x13f9e7, 64, 16));
        assertEquals(msg1, 0x89abcdeff9e66789L, a.writeMasked(0xa2, 0x89abcdef23456789L, 0x14f9e6, 64, 16));
        assertEquals(msg1, 0x89abcdeff9e56789L, a.writeMasked(0xa3, 0x89abcdef23456789L, 0x15f9e5, 64, 16));
        assertEquals(msg1, 0x89abf9e423456789L, a.writeMasked(0xa4, 0x89abcdef23456789L, 0x16f9e4, 64, 16));
        assertEquals(msg1, 0x89abf9e323456789L, a.writeMasked(0xa5, 0x89abcdef23456789L, 0x17f9e3, 64, 16));
        assertEquals(msg1, 0xf9e2cdef23456789L, a.writeMasked(0xa6, 0x89abcdef23456789L, 0x18f9e2, 64, 16));
        assertEquals(msg1, 0xf9e1cdef23456789L, a.writeMasked(0xa7, 0x89abcdef23456789L, 0x19f9e1, 64, 16));

        assertEquals(msg1, 0x89abcdef98765432L, a.writeMasked(0xb0, 0x89abcdef23456789L, 0x98765432, 64, 32));
        assertEquals(msg1, 0x89abcdef98765433L, a.writeMasked(0xb1, 0x89abcdef23456789L, 0x98765433, 64, 32));
        assertEquals(msg1, 0x89abcdef98765434L, a.writeMasked(0xb2, 0x89abcdef23456789L, 0x98765434, 64, 32));
        assertEquals(msg1, 0x89abcdef98765435L, a.writeMasked(0xb3, 0x89abcdef23456789L, 0x98765435, 64, 32));
        assertEquals(msg1, 0x9876543623456789L, a.writeMasked(0xb4, 0x89abcdef23456789L, 0x98765436, 64, 32));
        assertEquals(msg1, 0x9876543723456789L, a.writeMasked(0xb5, 0x89abcdef23456789L, 0x98765437, 64, 32));
        assertEquals(msg1, 0x9876543823456789L, a.writeMasked(0xb6, 0x89abcdef23456789L, 0x98765438, 64, 32));
        assertEquals(msg1, 0x9876543923456789L, a.writeMasked(0xb7, 0x89abcdef23456789L, 0x98765439, 64, 32));
    }
}
