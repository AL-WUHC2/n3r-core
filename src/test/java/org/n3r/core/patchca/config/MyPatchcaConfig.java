package org.n3r.core.patchca.config;

import java.util.ArrayList;
import java.util.List;

import org.n3r.core.patchca.custom.CaptchaWordFactoryConfig;
import org.n3r.core.patchca.custom.ChineseIdiomFactory;
import org.n3r.core.patchca.custom.ChineseIdiomGuessFactory;
import org.n3r.core.patchca.custom.EnglishWordFactory;
import org.n3r.core.patchca.custom.MathArithmeticFactory;
import org.n3r.core.patchca.custom.MathExprFactory;
import org.n3r.core.patchca.word.AdaptiveRandomWordFactory;
import org.n3r.core.patchca.word.RandomWordFactory;
import org.n3r.core.patchca.word.WordFactory;

@CaptchaWordFactoryConfig
public class MyPatchcaConfig {
    public List<WordFactory> configMathExprFactories() {
        ArrayList<WordFactory> factories = new ArrayList<WordFactory>();
        factories.add(new MathExprFactory());
        factories.add(new MathArithmeticFactory());
        factories.add(new ChineseIdiomFactory());
        factories.add(new ChineseIdiomGuessFactory());
        factories.add(new EnglishWordFactory());
        factories.add(new AdaptiveRandomWordFactory());
        factories.add(new RandomWordFactory());

        return factories;
    }
}
