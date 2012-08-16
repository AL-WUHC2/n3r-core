package org.n3r.core.patchca.custom;

import java.util.List;
import java.util.Random;

import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;

public class RandomFactoryWordFactory implements WordFactory {
    private static Random random = new Random();
    private List<WordFactory> factories;

    public RandomFactoryWordFactory(List<WordFactory> factories) {
        this.factories = factories;
    }

    @Override
    public WordBean getNextWord() {
        WordFactory wordFactory = factories.get(random.nextInt(factories.size()));
        return wordFactory.getNextWord();
    }

}
