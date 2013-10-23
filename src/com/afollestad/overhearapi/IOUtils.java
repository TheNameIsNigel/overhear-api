package com.afollestad.overhearapi;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    /**
     * The default buffer size to use for
     * {@link #copyLarge(InputStream, OutputStream)}
     */
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Unconditionally close an <code>InputStream</code>.
     * <p/>
     * Equivalent to {@link InputStream#close()}, except any exceptions will
     * be ignored. This is typically used in finally blocks.
     * <p/>
     * Example code:
     * <p/>
     * <pre>
     * byte[] data = new byte[1024];
     * InputStream in = null;
     * try {
     *     in = new FileInputStream(&quot;foo.txt&quot;);
     *     in.read(data);
     *     in.close(); // close errors are handled
     * } catch (Exception e) {
     *     // error handling
     * } finally {
     *     IOUtils.closeQuietly(in);
     * }
     * </pre>
     *
     * @param input the InputStream to close, may be null or already closed
     */
    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    /**
     * Unconditionally close an <code>OutputStream</code>.
     * <p/>
     * Equivalent to {@link OutputStream#close()}, except any exceptions
     * will be ignored. This is typically used in finally blocks.
     * <p/>
     * Example code:
     * <p/>
     * <pre>
     * byte[] data = &quot;Hello, World&quot;.getBytes();
     *
     * OutputStream out = null;
     * try {
     *     out = new FileOutputStream(&quot;foo.txt&quot;);
     *     out.write(data);
     *     out.close(); // close errors are handled
     * } catch (IOException e) {
     *     // error handling
     * } finally {
     *     IOUtils.closeQuietly(out);
     * }
     * </pre>
     *
     * @param output the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable) output);
    }

    /**
     * Unconditionally close a <code>Closeable</code>.
     * <p/>
     * Equivalent to {@link Closeable#close()}, except any exceptions will
     * be ignored. This is typically used in finally blocks.
     * <p/>
     * Example code:
     * <p/>
     * <pre>
     * Closeable closeable = null;
     * try {
     *     closeable = new FileReader(&quot;foo.txt&quot;);
     *     // process closeable
     *     closeable.close();
     * } catch (Exception e) {
     *     // error handling
     * } finally {
     *     IOUtils.closeQuietly(closeable);
     * }
     * </pre>
     *
     * @param closeable the object to close, may be null or already closed
     * @since Commons IO 2.0
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    // copy from InputStream
    // -----------------------------------------------------------------------

    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use
     * a <code>BufferedInputStream</code>.
     * <p/>
     * Large streams (over 2GB) will return a bytes copied value of
     * <code>-1</code> after the copy has completed since the correct number
     * of bytes cannot be returned as an int. For large streams use the
     * <code>copyLarge(InputStream, OutputStream)</code> method.
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.1
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use
     * a <code>BufferedInputStream</code>.
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException          if an I/O error occurs
     * @since Commons IO 1.3
     */
    private static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
