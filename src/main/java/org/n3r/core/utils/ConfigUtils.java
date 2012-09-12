package org.n3r.core.utils;

import java.io.InputStream;
import java.util.List;

/**
 * 配置小函数。
 * @author BingooHuang
 *
 */
public class ConfigUtils {

    /**
     * 读取配置文件，忽略空行，注释，以及少于最少字段数目的行。
     * @param is 输入流
     * @param atLeastNumOfCommons 最少字段数目
     * @param lineConverter 行转换器
     */
    public static void loadConfig(InputStream is, int atLeastNumOfCommons, LineConverter lineConverter) {
        List<String> lines = RIOUtils.readLines(is);

        for (String line : lines) {
            line = line.trim();

            if (line.length() == 0) { // 忽略空行
                continue;
            }

            if (line.startsWith("#")) { // 忽略注释
                continue;
            }

            String[] fields = line.split(",");
            // 一行以逗号分隔的字段数目小于atLeastNumOfCommons，忽略该行
            if (fields.length < atLeastNumOfCommons) {
                continue;
            }

            lineConverter.convert(fields);
        }
    }
}
