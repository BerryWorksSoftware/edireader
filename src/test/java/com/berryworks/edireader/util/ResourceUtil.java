package com.berryworks.edireader.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ResourceUtil {

    public static String getResourceAsString(String resourceName) throws IOException {
        InputStream resourceAsStream = ResourceUtil.class.getClassLoader().getResourceAsStream(resourceName);
        if (resourceAsStream == null) {
            throw new RuntimeException("Cannot load resource " + resourceName);
        }
        byte[] bytes = getBytes(resourceAsStream);
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes)).toString();
    }

    public static File getResourceAsFile(String resourceName) throws URISyntaxException {
        URL resource = ResourceUtil.class.getClassLoader().getResource(resourceName);
        if (resource == null) {
            throw new RuntimeException("Cannot load resource " + resourceName);
        }
        return new File(resource.toURI());
    }
    public static String cleanseCRLF(Object text) {
        return text.toString().replace("\r\n", "\n");
    }

    private static byte[] getBytes(InputStream is) throws IOException {

        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        return buf;
    }

}
