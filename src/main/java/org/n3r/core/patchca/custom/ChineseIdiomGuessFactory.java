package org.n3r.core.patchca.custom;

import java.util.Random;

import org.n3r.core.patchca.word.WordBean;

public class ChineseIdiomGuessFactory extends ChineseIdiomFactory {

    public WordBean getNextWord() {
        WordBean wordBean = super.getNextWord();
        Random random = new Random();
        String word = wordBean.getWord();
        int pos = random.nextInt(word.length());

        return new WordBean(word.substring(0, pos) + "?" + word.substring(pos + 1),
                word.substring(pos, pos + 1));

    }

}
