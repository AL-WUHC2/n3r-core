package org.n3r.core.patchca.custom;

import java.util.Random;

import org.n3r.core.patchca.word.WordBean;
import org.n3r.core.patchca.word.WordFactory;

public class MathArithmeticFactory implements WordFactory {
    private static String[] chineseNumber = new String[] { "零", "一壹㈠①⑴", "二㈡贰②⑵", "三㈢叁③⑶",
            "四㈣肆④⑷", "五㈤伍⑤⑸", "六㈥陆⑥⑹", "七㈦柒⑦⑺",
            "八㈧捌⑧⑻", "九㈨玖⑨⑼", "十㈩拾⑩⑽", "百佰", "千仟", "万" };

    private static String[] operations = new String[] { "加+", "减", "乘X", "除÷" };
    private static Random random = new Random();

    public static String rand(int n) {
        if (n > 9) return "" + n;

        String s1 = chineseNumber[n];
        char c1 = s1.charAt(random.nextInt(s1.length()));
        return "" + c1;
    }

    @Override
    public WordBean getNextWord() {
        int n1 = random.nextInt(9) + 1;
        int n2 = random.nextInt(9) + 1;
        String rand1 = rand(n1);
        String rand2 = rand(n2);
        String randMin = rand(Math.min(n1, n2));
        String randMax = rand(Math.max(n1, n2));
        String rand12 = rand(n1 * n2);

        int op = random.nextInt(4);
        String opStr = operations[op];
        char opCh = opStr.charAt(random.nextInt(opStr.length()));

        int expected = 0;
        String result = "";
        int askPos = random.nextInt(3);
        if (op == 0) { // + 
            if (askPos == 0) {
                expected = Math.abs(n1 - n2);
                result = "?" + opCh + randMin + '=' + randMax;
            }
            else if (askPos == 1) {
                expected = Math.abs(n1 - n2);
                result = randMin + opCh + "?=" + randMax;
            }
            else if (askPos == 2) {
                expected = n1 + n2;
                result = rand1 + opCh + rand2 + "=?";
            }
        }
        else if (op == 1) { // -
            if (askPos == 0) {
                expected = n1 + n2;
                result = "?" + opCh + rand1 + '=' + rand2;
            }
            else if (askPos == 1) {
                expected = Math.abs(n1 - n2);
                result = randMax + opCh + "?=" + randMin;
            }
            else if (askPos == 2) {
                expected = Math.abs(n1 - n2);
                result = randMax + opCh + randMin + "=?";
            }
        }
        else if (op == 2) { // *
            if (askPos == 0) {
                expected = n2;
                result = "?" + opCh + rand1 + '=' + rand12;
            }
            else if (askPos == 1) {
                expected = n2;
                result = rand1 + opCh + "?=" + rand12;
            }
            else if (askPos == 2) {
                expected = n2 * n1;
                result = rand1 + opCh + rand2 + "=?";
            }
        }
        else if (op == 3) { // /
            if (askPos == 0) {
                expected = n2 * n1;
                result = "?" + opCh + rand1 + '=' + rand2;
            }
            else if (askPos == 1) {
                expected = n2;
                result = rand12 + opCh + "?=" + rand1;
            }
            else if (askPos == 2) {
                expected = n2;
                result = rand12 + opCh + rand1 + "=?";
            }
        }

        return new WordBean(result, "" + expected, "请输入图片中?代表的数字");
    }

    @Override
    public String[] getSupportedFontFamilies() {
        return new String[]{"宋体"};
    }
}
