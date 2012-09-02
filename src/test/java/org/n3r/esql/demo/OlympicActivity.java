package org.n3r.esql.demo;

import org.n3r.esql.Esql;
import org.n3r.esql.demo.OlympicActivityTest.RandomAward;

public class OlympicActivity {
    public RandomAward getRandomAwardType() {
        RandomAward randomAward = new Esql().select("GetRandomAwardType")
                .returnType(RandomAward.class)
                .execute();

        return randomAward;
    }
}
