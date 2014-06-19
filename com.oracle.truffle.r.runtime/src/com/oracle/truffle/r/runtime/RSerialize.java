/*
 * This material is distributed under the GNU General Public License
 * Version 2. You may review the terms of this license at
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Copyright (c) 1995-2012, The R Core Team
 * Copyright (c) 2003, The R Foundation
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates
 *
 * All rights reserved.
 */
package com.oracle.truffle.r.runtime;

import java.io.*;
import java.util.*;

import com.oracle.truffle.r.runtime.data.*;

// Code loosely transcribed from GnuR serialize.c.

/**
 * Serialize/unserialize.
 *
 */
public final class RSerialize {

    private static enum SEXPTYPE {
        NILSXP(0), /* nil ()NULL */
        SYMSXP(1), /* symbols */
        LISTSXP(2), /* lists of dotted pairs */
        CLOSXP(3), /* closures */
        ENVSXP(4), /* environments */
        PROMSXP(5), /* promises: [un]evaluated closure arguments */
        LANGSXP(6), /* language constructs (special lists) */
        SPECIALSXP(7), /* special forms */
        BUILTINSXP(8), /* builtin non-special forms */
        CHARSXP(9), /* "scalar" string type (internal only) */
        LGLSXP(10), /* logical vectors */
        INTSXP(13), /* integer vectors */
        REALSXP(14), /* real variables */
        CPLXSXP(15), /* complex variables */
        STRSXP(16), /* string vectors */
        DOTSXP(17), /* dot-dot-dot object */
        ANYSXP(18), /* make "any" args work */
        VECSXP(19), /* generic vectors */
        EXPRSXP(20), /* expressions vectors */
        BCODESXP(21), /* byte code */
        EXTPTRSXP(22), /* external pointer */
        WEAKREFSXP(23), /* weak reference */
        RAWSXP(24), /* raw bytes */
        S4SXP(25), /* S4 non-vector */

        NEWSXP(30), /* fresh node creaed in new page */
        FREESXP(31), /* node released by GC */

        FUNSXP(99), /* Closure or Builtin */

        REFSXP(255),
        NILVALUE_SXP(254),
        GLOBALENV_SXP(253),
        UNBOUNDVALUE_SXP(252),
        MISSINGARG_SXP(251),
        BASENAMESPACE_SXP(250),
        NAMESPACESXP(249),
        PACKAGESXP(248),
        PERSISTSXP(247);

        final int code;

        static Map<Integer, SEXPTYPE> codeMap = new HashMap<>();

        SEXPTYPE(int code) {
            this.code = code;
        }
    }

    static {
        for (SEXPTYPE type : SEXPTYPE.values()) {
            SEXPTYPE.codeMap.put(type.code, type);
        }
    }

    private static class Flags {
        static final int IS_OBJECT_BIT_MASK = 1 << 8;
        static final int HAS_ATTR_BIT_MASK = 1 << 9;
        static final int HAS_TAG_BIT_MASK = 1 << 10;
        static final int TYPE_MASK = 255;
        static final int LEVELS_SHIFT = 12;

        int value;
        int ptype;
        @SuppressWarnings("unused") int plevs;
        @SuppressWarnings("unused") boolean isObj;
        boolean hasAttr;
        boolean hasTag;

        static void decodeFlags(Flags flags, int flagsValue) {
            flags.value = flagsValue;
            flags.ptype = flagsValue & Flags.TYPE_MASK;
            flags.plevs = flagsValue >> Flags.LEVELS_SHIFT;
            flags.isObj = (flagsValue & IS_OBJECT_BIT_MASK) != 0;
            flags.hasAttr = (flagsValue & HAS_ATTR_BIT_MASK) != 0;
            flags.hasTag = (flagsValue & HAS_TAG_BIT_MASK) != 0;
        }
    }

    @SuppressWarnings("unused") private static final int MAX_PACKED_INDEX = Integer.MAX_VALUE >> 8;

    @SuppressWarnings("unused")
    private static int packRefIndex(int i) {
        return (i << 8) | SEXPTYPE.REFSXP.code;
    }

    private static int unpackRefIndex(int i) {
        return i >> 8;
    }

    private PStream stream;
    private Object[] refTable = new Object[128];
    private int refTableIndex;

    private RSerialize(RConnection conn) throws IOException {
        byte[] buf = new byte[2];
        InputStream is = conn.getInputStream();
        is.read(buf);
        switch (buf[0]) {
            case 'A':
                break;
            case 'B':
                break;
            case 'X':
                stream = new XdrFormat(is);
                break;
            case '\n':
            default:
        }

    }

    private int inRefIndex(Flags flags) {
        int i = unpackRefIndex(flags.value);
        if (i == 0) {
            return stream.readInt();
        } else {
            return i;
        }
    }

    private void addReadRef(Object item) {
        if (refTableIndex >= refTable.length) {
            Object[] newRefTable = new Object[2 * refTable.length];
            System.arraycopy(refTable, 0, newRefTable, 0, refTable.length);
            refTable = newRefTable;
        }
        refTable[refTableIndex++] = item;
    }

    private Object getReadRef(int index) {
        return refTable[index - 1];
    }

    public static Object unserialize(RConnection conn) throws IOException {
        RSerialize instance = new RSerialize(conn);
        int version = instance.stream.readInt();
        @SuppressWarnings("unused")
        int writerVersion = instance.stream.readInt();
        @SuppressWarnings("unused")
        int releaseVersion = instance.stream.readInt();
        assert version == 2; // TODO proper error message
        return instance.readItem();
    }

    private Object readItem() {
        Flags flags = new Flags();
        Flags.decodeFlags(flags, stream.readInt());
        Object result = null;

        SEXPTYPE type = SEXPTYPE.codeMap.get(flags.ptype);
        switch (type) {
            case NILVALUE_SXP:
                return RNull.instance;

            case REFSXP: {
                return getReadRef(inRefIndex(flags));
            }

            case VECSXP: {
                int len = stream.readInt();
                // TODO long vector support?
                assert len >= 0;
                Object[] data = new Object[len];
                for (int i = 0; i < len; i++) {
                    Object elem = readItem();
                    data[i] = elem;
                }
                // this is a list
                result = RDataFactory.createList(data);
                break;
            }

            case STRSXP: {
                int len = stream.readInt();
                String[] data = new String[len];
                boolean complete = RDataFactory.COMPLETE_VECTOR; // optimistic
                for (int i = 0; i < len; i++) {
                    String item = (String) readItem();
                    if (RRuntime.isNA(item)) {
                        complete = RDataFactory.INCOMPLETE_VECTOR;
                    }
                    data[i] = item;
                }
                result = RDataFactory.createStringVector(data, complete);
                break;
            }

            case INTSXP: {
                int len = stream.readInt();
                int[] data = new int[len];
                boolean complete = RDataFactory.COMPLETE_VECTOR; // really?
                for (int i = 0; i < len; i++) {
                    data[i] = stream.readInt();
                }
                result = RDataFactory.createIntVector(data, complete);
                break;
            }

            case LGLSXP: {
                int len = stream.readInt();
                byte[] data = new byte[len];
                boolean complete = RDataFactory.COMPLETE_VECTOR; // really?
                for (int i = 0; i < len; i++) {
                    data[i] = (byte) stream.readInt();
                }
                result = RDataFactory.createLogicalVector(data, complete);
                break;
            }

            case REALSXP: {
                int len = stream.readInt();
                double[] data = new double[len];
                boolean complete = RDataFactory.COMPLETE_VECTOR; // really?
                for (int i = 0; i < len; i++) {
                    data[i] = stream.readInt();
                }
                result = RDataFactory.createDoubleVector(data, complete);
                break;
            }

            case CHARSXP: {
                int len = stream.readInt();
                if (len == -1) {
                    return RRuntime.STRING_NA;
                } else {
                    result = stream.readString(len);
                }
                break;
            }

            case LISTSXP:
            case LANGSXP:
            case CLOSXP:
            case PROMSXP:
            case DOTSXP: {
                Object attrItem = null;
                String tagItem = null;
                if (flags.hasAttr) {
                    attrItem = readItem();

                }
                if (flags.hasTag) {
                    tagItem = (String) readItem();
                }
                Object carItem = readItem();
                Object cdrItem = readItem();
                result = new RPairList(carItem, cdrItem, tagItem);
                if (attrItem != null) {
                    // result.setAttr(attrItem);
                }
                break;
            }

            case SYMSXP: {
                result = readItem();
                addReadRef(result);
                break;
            }

            default:
                assert false;
        }
        // TODO SETLEVELS
        if (type == SEXPTYPE.CHARSXP) {
            /*
             * With the CHARSXP cache maintained through the ATTRIB field that field has already
             * been filled in by the mkChar/mkCharCE call above, so we need to leave it alone. If
             * there is an attribute (as there might be if the serialized data was created by an
             * older version) we read and ignore the value.
             */
            if (flags.hasAttr) {
                readItem();
            }
        } else {
            if (flags.hasAttr) {
                Object attr = readItem();
                // Eventually we can use RAttributable
                RVector vec = (RVector) result;
                /*
                 * GnuR uses a pairlist to represent attributes, whereas FastR uses the abstract
                 * RAttributes class.
                 */
                RPairList pl = (RPairList) attr;
                while (true) {
                    String tag = pl.getTag();
                    Object car = pl.getCar();
                    // Eventually we can just use the generic setAttr
                    if (tag.equals(RRuntime.NAMES_ATTR_KEY)) {
                        vec.setNames(car);
                    } else if (tag.equals(RRuntime.DIMNAMES_ATTR_KEY)) {
                        vec.setDimNames((RList) car);
                    } else if (tag.equals(RRuntime.ROWNAMES_ATTR_KEY)) {
                        vec.setRowNames(car);
                    } else if (tag.equals(RRuntime.CLASS_ATTR_KEY)) {
                        RVector.setClassAttr(vec, (RStringVector) car, null);
                    } else {
                        vec.setAttr(tag, car);
                    }
                    Object cdr = pl.getCdr();
                    if (cdr instanceof RNull) {
                        break;
                    } else {
                        pl = (RPairList) cdr;
                    }
                }
            }
        }

        return result;
    }

    private abstract static class PStream {
        @SuppressWarnings("unused") protected InputStream is;

        PStream(InputStream is) {
            this.is = is;
        }

        int readInt() {
            notImpl();
            return -1;
        }

        String readString(@SuppressWarnings("unused") int len) {
            notImpl();
            return null;
        }

        @SuppressWarnings("unused")
        double readDouble() {
            notImpl();
            return 0.0;
        }

        private static void notImpl() {
            Utils.fail("not implemented");

        }

    }

    @SuppressWarnings("unused")
    private static class AsciiFormat extends PStream {
        AsciiFormat(InputStream is) {
            super(is);
        }
    }

    @SuppressWarnings("unused")
    private static class BinaryFormat extends PStream {
        BinaryFormat(InputStream is) {
            super(is);
        }
    }

    private static class XdrFormat extends PStream {
        /**
         * XDR buffer handling. Copied and modified from code in <a
         * href="https://java.net/projects/yanfs">YANFS</a>, developed at Sun Microsystems.
         */
        public static class Xdr {
            private byte[] buf;
            @SuppressWarnings("unused") private int size;
            private int offset;

            /**
             * Build a new Xdr object with a buffer of given size.
             *
             * @param size of the buffer in bytes
             */
            Xdr(int size) {
                this.buf = new byte[size];
                this.size = size;
                this.offset = 0;
            }

            /**
             * Get an integer from the buffer.
             *
             * @return integer
             */
            int getInt() {
                return ((buf[offset++] & 0xff) << 24 | (buf[offset++] & 0xff) << 16 | (buf[offset++] & 0xff) << 8 | (buf[offset++] & 0xff));
            }

            /**
             * Put an integer into the buffer.
             *
             * @param i Integer to store in XDR buffer.
             */
            void putInt(int i) {
                buf[offset++] = (byte) (i >>> 24);
                buf[offset++] = (byte) (i >> 16);
                buf[offset++] = (byte) (i >> 8);
                buf[offset++] = (byte) i;
            }

            /**
             * Get an unsigned integer from the buffer,
             *
             * <br>
             * Note that Java has no unsigned integer type so we must return it as a long.
             *
             * @return long
             */
            @SuppressWarnings("unused")
            long getUInt() {
                return ((buf[offset++] & 0xff) << 24 | (buf[offset++] & 0xff) << 16 | (buf[offset++] & 0xff) << 8 | (buf[offset++] & 0xff));
            }

            /**
             * Put an unsigned integer into the buffer
             *
             * Note that Java has no unsigned integer type so we must submit it as a long.
             *
             * @param i unsigned integer to store in XDR buffer.
             */
            @SuppressWarnings("unused")
            void putUInt(long i) {
                buf[offset++] = (byte) (i >>> 24 & 0xff);
                buf[offset++] = (byte) (i >> 16);
                buf[offset++] = (byte) (i >> 8);
                buf[offset++] = (byte) i;
            }

            /**
             * Get a long from the buffer.
             *
             * @return long
             */
            long getHyper() {
                return ((long) (buf[offset++] & 0xff) << 56 | (long) (buf[offset++] & 0xff) << 48 | (long) (buf[offset++] & 0xff) << 40 | (long) (buf[offset++] & 0xff) << 32 |
                                (long) (buf[offset++] & 0xff) << 24 | (long) (buf[offset++] & 0xff) << 16 | (long) (buf[offset++] & 0xff) << 8 | buf[offset++] & 0xff);
            }

            /**
             * Put a long into the buffer.
             *
             * @param i long to store in XDR buffer
             */
            void putHyper(long i) {
                buf[offset++] = (byte) (i >>> 56);
                buf[offset++] = (byte) ((i >> 48) & 0xff);
                buf[offset++] = (byte) ((i >> 40) & 0xff);
                buf[offset++] = (byte) ((i >> 32) & 0xff);
                buf[offset++] = (byte) ((i >> 24) & 0xff);
                buf[offset++] = (byte) ((i >> 16) & 0xff);
                buf[offset++] = (byte) ((i >> 8) & 0xff);
                buf[offset++] = (byte) (i & 0xff);
            }

            /**
             * Get a floating point number from the buffer.
             *
             * @return float
             */
            double getDouble() {
                return (Double.longBitsToDouble(getHyper()));
            }

            /**
             * Put a floating point number into the buffer.
             *
             * @param f float
             */
            @SuppressWarnings("unused")
            void putDouble(double f) {
                putHyper(Double.doubleToLongBits(f));
            }

            String string(int len) {
                String s = new String(buf, offset, len);
                offset += len;
                return s;
            }

            /**
             * Put a counted array of bytes into the buffer.
             *
             * @param b byte array
             * @param len number of bytes to encode
             */
            @SuppressWarnings("unused")
            void putBytes(byte[] b, int len) {
                putBytes(b, 0, len);
            }

            /**
             * Put a counted array of bytes into the buffer.
             *
             * @param b byte array
             * @param boff offset into byte array
             * @param len number of bytes to encode
             */
            void putBytes(byte[] b, int boff, int len) {
                putInt(len);
                System.arraycopy(b, boff, buf, offset, len);
                offset += len;
            }

            /**
             * Put a counted array of bytes into the buffer. The length is not encoded.
             *
             * @param b byte array
             * @param boff offset into byte array
             * @param len number of bytes to encode
             */
            void putRawBytes(byte[] b, int boff, int len) {
                System.arraycopy(b, boff, buf, offset, len);
                offset += len;
            }

            void ensureCanAdd(int n) {
                if (offset + n > buf.length) {
                    byte[] b = new byte[offset + n];
                    System.arraycopy(buf, 0, b, 0, buf.length);
                    buf = b;
                }
            }
        }

        private Xdr xdr;

        XdrFormat(InputStream is) throws IOException {
            super(is);
            byte[] isbuf = new byte[4096];
            xdr = new Xdr(0);
            int count = 0;
            // read entire stream
            while (true) {
                int nr = is.read(isbuf, 0, isbuf.length);
                if (nr == -1) {
                    break;
                }
                xdr.ensureCanAdd(nr);
                xdr.putRawBytes(isbuf, 0, nr);
                count += nr;
            }
            xdr.size = count;
            xdr.offset = 0;
        }

        @Override
        int readInt() {
            return xdr.getInt();
        }

        @Override
        String readString(int len) {
            String result = xdr.string(len);
            return result;
        }

        @Override
        double readDouble() {
            return xdr.getDouble();
        }
    }

}