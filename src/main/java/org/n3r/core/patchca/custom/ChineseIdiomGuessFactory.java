package org.n3r.core.patchca.custom;

import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.text.RRand;

public class ChineseIdiomGuessFactory extends ChineseIdiomFactory {

    public WordBean getNextWord() {
        WordBean wordBean = super.getNextWord();
        String word = wordBean.getWord();
        int pos = RRand.randInt(word.length());

        return new WordBean(word.substring(0, pos) + "?" + word.substring(pos + 1),
                word.substring(pos, pos + 1), "请输入图片中?的代表的汉字");

    }

}
