package org.n3r.core.patchca.custom;

import java.util.Random;

import org.n3r.core.lang.RClassPath;
import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;

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
        Random random = new Random();
        int nextInt = random.nextInt(idioms.length);
        String answer = idioms[nextInt];

        return new WordBean(answer, answer);
    }

}
