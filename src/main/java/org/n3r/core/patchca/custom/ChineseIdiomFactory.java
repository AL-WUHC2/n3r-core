package org.n3r.core.patchca.custom;

import org.n3r.core.lang.RClassPath;
import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;
import org.n3r.core.text.RRand;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class ChineseIdiomFactory implements WordFactory {

    protected static String[] idioms;

    static {
        String str = RClassPath.toStr("/org/n3r/core/patchca/chineseidioms.txt");
        Iterable<String> split = Splitter.onPattern("\\s+").omitEmptyStrings().split(str);
        idioms = Iterables.toArray(split, String.class);
    }

    public WordBean getNextWord() {
        int nextInt = RRand.randInt(idioms.length);
        String answer = idioms[nextInt];

        return new WordBean(answer, answer, "请输入图片中的文字");
    }

    @Override
    public String[] getSupportedFontFamilies() {
        //        families.add("方正舒体");
        //        families.add("方正姚体");
        //        families.add("仿宋");
        //        families.add("黑体");
        //        families.add("华文彩云");
        //        families.add("华文仿宋");
        //        families.add("华文行楷");
        //        families.add("华文琥珀");
        //        families.add("华文楷体");
        //        families.add("华文隶书");
        //        families.add("华文宋体");
        //        families.add("华文细黑");
        //        families.add("华文新魏");
        //        families.add("华文中宋");
        //        families.add("楷体");
        //        families.add("隶书");
        //        families.add("宋体"); //可以
        //        families.add("微软雅黑"); // 不支持㈦
        //        families.add("新宋体");
        //        families.add("幼圆");
        return new String[]{"宋体"};
    }

}
