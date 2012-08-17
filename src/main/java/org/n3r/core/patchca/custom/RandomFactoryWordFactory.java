package org.n3r.core.patchca.custom;

import java.util.List;
import java.util.Random;

import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;

public class RandomFactoryWordFactory implements WordFactory {
    private static Random random = new Random();
    private List<WordFactory> factories;
    private static ThreadLocal<WordFactory> wordFactory = new ThreadLocal<WordFactory>();

    public RandomFactoryWordFactory(List<WordFactory> factories) {
        this.factories = factories;
    }

    @Override
    public WordBean getNextWord() {
        WordFactory value = factories.get(random.nextInt(factories.size()));
        wordFactory.set(value);
        return value.getNextWord();
    }

    @Override
    public String[] getSupportedFontFamilies() {
        WordFactory wf = wordFactory.get();
        return wf.getSupportedFontFamilies();
    }


}
