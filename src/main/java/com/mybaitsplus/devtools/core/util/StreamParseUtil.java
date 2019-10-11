package com.mybaitsplus.devtools.core.util;

/**
 * @Author: Yi.Zhao
 * @Description:
 * @Date: 2019/7/13 13:12
 * @Version: 1.0
 */
import java.io.*;

public class StreamParseUtil {
    /**
     * inputStream转outputStream
     */
    public static ByteArrayOutputStream parse(final InputStream in) throws Exception {
        final ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            swapStream.write(ch);
        }
        return swapStream;
    }
    /**
     * outputStream转inputStream
     */
    public static ByteArrayInputStream parse(final OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        final ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }
    /**
     * inputStream转String
     */
    public static String parseString(final InputStream in) throws Exception {
        final ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            swapStream.write(ch);
        }
        return swapStream.toString();
    }
    /**
     * OutputStream 转String
     */
    public static String parseString(final OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        final ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream.toString();
    }
    /**
     * String转inputStream
     */
    public static ByteArrayInputStream parseInputStream(final String in) throws Exception {
        final ByteArrayInputStream input = new ByteArrayInputStream(in.getBytes());
        return input;
    }
    /**
     * String 转outputStream
     */
    public static ByteArrayOutputStream parseOutputStream(final String in) throws Exception {
        return parse(parseInputStream(in));
    }
    /**
     * byte[] 转inputStream
     */
    public static final InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }
    /**
     * inputStream转byte[]
     */
    public static final byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }
}
