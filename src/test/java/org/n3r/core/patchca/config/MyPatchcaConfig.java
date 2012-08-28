package org.n3r.core.patchca.config;

import java.util.ArrayList;
import java.util.List;

import org.n3r.core.patchca.custom.CaptchaWordFactoryConfig;
import org.n3r.core.patchca.custom.ChineseIdiomFactory;
import org.n3r.core.patchca.custom.ChineseIdiomGuessFactory;
import org.n3r.core.patchca.custom.EnglishWordFactory;
import org.n3r.core.patchca.custom.KnowledgeWordFactory;
import org.n3r.core.patchca.custom.MathArithmeticFactory;
import org.n3r.core.patchca.custom.MathExprFactory;
import org.n3r.core.patchca.custom.SymbolDiffFactory;
import org.n3r.core.patchca.word.AdaptiveRandomWordFactory;
import org.n3r.core.patchca.word.RandomWordFactory;
import org.n3r.core.patchca.word.WordFactory;

@CaptchaWordFactoryConfig
public class MyPatchcaConfig {
    public List<WordFactory> configMathExprFactories() {
        // 这里演示，把所有已经定义的工厂实例都加入。
        // 实际业务，可以从配置中读取相关配置，加入相关工厂实例。
        ArrayList<WordFactory> factories = new ArrayList<WordFactory>();
        factories.add(new MathExprFactory()); // 三个单数运算
        factories.add(new MathArithmeticFactory()); // 四则运算
        factories.add(new ChineseIdiomFactory()); // 成语
        factories.add(new ChineseIdiomGuessFactory()); // 成语猜字
        factories.add(new EnglishWordFactory()); // 常见2000英语单词
        factories.add(new AdaptiveRandomWordFactory()); // 宽字符只会有一个的随机
        factories.add(new RandomWordFactory()); // 随机
        factories.add(new SymbolDiffFactory()); // 符号找不同
        factories.add(new KnowledgeWordFactory()); // 地理知识

        return factories;
    }
}
