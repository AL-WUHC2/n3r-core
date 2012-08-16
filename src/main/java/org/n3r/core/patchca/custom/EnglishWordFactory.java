package org.n3r.core.patchca.custom;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

public class EnglishWordFactory implements WordFactory {
    private static String[] idioms;

    static {
        InputStream is = ChineseIdiomGuessFactory.class.getResourceAsStream("/org/n3r/core/patchca/englishwords.txt");
        try {
            String str = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
            Pattern pattern = Pattern.compile("\\b\\w{5,8}\\b");
            Matcher matcher = pattern.matcher(str);
            ArrayList<String> words = new ArrayList<String>();
            while (matcher.find()) {
                words.add(matcher.group());
            }

            idioms = words.toArray(new String[0]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Closeables.closeQuietly(is);
        }
    }

    @Override
    public WordBean getNextWord() {
        int nextInt = new Random().nextInt(idioms.length);
        String answer = idioms[nextInt];
        return new WordBean(answer, answer);
    }

}
