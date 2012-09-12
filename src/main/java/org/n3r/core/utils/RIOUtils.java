package org.n3r.core.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.n3r.core.lang.RMethod;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class RIOUtils {

    public static void closeQuietly(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                continue;
            }

            if (obj instanceof Closeable) {
                try {
                    ((Closeable) obj).close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Method method = RMethod.getMethod(obj.getClass(), "close");
            if (method != null) {
                RMethod.invokeQuietly(method, obj);
            }
        }
    }

    public static InputStream readFileToStream(String fileName) {
        Resource res = new ClassPathResource(fileName);
        try {
            return res.getInputStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    /**
     * 读取文件内容, 以List形式返回。
     * @param inStream 输入流
     * @return 列表实例
     */

    public static List<String> readLines(InputStream inStream) {
        try {
            return IOUtils.readLines(inStream, "UTF-8");
        }
        catch (IOException e) {
            return new ArrayList<String>();
        }
        finally {
            RIOUtils.closeQuietly(inStream);
        }
    }

    /**
     * 读取文件内容, 以List形式返回。
     * @param <T> 列表类型
     * @param fileName 文件名称
     * @return 列表实例
     * @throws IOException 
     */
    public static List<String> readLines(String fileName) {
        InputStream inStream = readFileToStream(fileName);
        return readLines(inStream);
    }

}
