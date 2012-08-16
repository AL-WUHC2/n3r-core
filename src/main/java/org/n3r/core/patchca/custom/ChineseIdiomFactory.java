package org.n3r.core.patchca.custom;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

public class ChineseIdiomFactory implements WordFactory {

    protected static String[] idioms;

    static {
        InputStream is = ChineseIdiomGuessFactory.class
                .getResourceAsStream("/org/n3r/core/patchca/chineseidioms.txt");
        try {
            String str = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
            Iterable<String> split = Splitter.onPattern("\\s+").omitEmptyStrings().split(str);
            idioms = Iterables.toArray(split, String.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Closeables.closeQuietly(is);
        }
    }

    public WordBean getNextWord() {
        Random random = new Random();
        int nextInt = random.nextInt(idioms.length);
        String answer = idioms[nextInt];

        return new WordBean(answer, answer);
    }

}
