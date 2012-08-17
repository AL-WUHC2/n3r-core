package org.n3r.core.patchca.custom;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.n3r.core.lang.RClassPath;
import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;

public class EnglishWordFactory implements WordFactory {
    private static String[] idioms;

    static {
        String str = RClassPath.toStr("/org/n3r/core/patchca/englishwords.txt");
        Pattern pattern = Pattern.compile("\\b\\w{5,8}\\b");
        Matcher matcher = pattern.matcher(str);
        ArrayList<String> words = new ArrayList<String>();
        while (matcher.find()) {
            words.add(matcher.group());
        }

        idioms = words.toArray(new String[0]);
    }

    @Override
    public WordBean getNextWord() {
        int nextInt = new Random().nextInt(idioms.length);
        String answer = idioms[nextInt];
        return new WordBean(answer, answer,"请输入图片中的英文单词");
    }

    @Override
    public String[] getSupportedFontFamilies() {
        return null;
    }

}
