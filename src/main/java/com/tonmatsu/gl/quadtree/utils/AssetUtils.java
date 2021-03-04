package com.tonmatsu.gl.quadtree.utils;

import java.io.*;

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
}
