package org.n3r.core.utils;

/**
 * 行转换器接口。
 *
 */
public interface LineConverter {
    /**
     * 转换。
     * @param fields 字段数组
     */
    void convert(String[] fields);
}
