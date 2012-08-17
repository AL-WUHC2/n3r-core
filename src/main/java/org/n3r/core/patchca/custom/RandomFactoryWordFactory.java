package org.n3r.core.patchca.custom;

import java.util.List;

import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;
import org.n3r.core.text.RRand;

public class RandomFactoryWordFactory implements WordFactory {
    private List<WordFactory> factories;
    private static ThreadLocal<WordFactory> wordFactory = new ThreadLocal<WordFactory>();

    public RandomFactoryWordFactory(List<WordFactory> factories) {
        this.factories = factories;
    }

    @Override
    public WordBean getNextWord() {
        WordFactory value = factories.get(RRand.randInt(factories.size()));
        wordFactory.set(value);
        return value.getNextWord();
    }

    @Override
    public String[] getSupportedFontFamilies() {
        WordFactory wf = wordFactory.get();
        return wf.getSupportedFontFamilies();
    }


}
