package com.tonmatsu.gl.quadtree.utils;

import org.lwjgl.*;

import java.io.*;
import java.nio.*;

public final class AssetUtils {
    private static final ClassLoader CL = AssetUtils.class.getClassLoader();

    private AssetUtils() {
    }

    public static InputStream getStream(String name) {
        return CL.getResourceAsStream(name);
    }

    public static BufferedReader getReader(String name) {
        return new BufferedReader(new InputStreamReader(getStream(name)));
    }

    public static String getString(String name) {
        try (final var reader = getReader(name)) {
            final var sb = new StringBuilder();
            while (true) {
                final var line = reader.readLine();
                if (line == null)
                    break;
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteBuffer getBuffer(String name) {
        try (final var stream = new BufferedInputStream(getStream(name))) {
            final var baos = new ByteArrayOutputStream();
            final var b = new byte[8192];
            while (true) {
                final var len = stream.read(b);
                if (len < 0)
                    break;
                baos.write(b, 0, len);
            }
            final var buffer = BufferUtils.createByteBuffer(baos.size());
            buffer.put(baos.toByteArray());
            buffer.flip();
            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
