package org.n3r.core.patchca.custom;

import java.awt.Color;

import org.n3r.core.patchca.color.SingleColorFactory;
import org.n3r.core.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.n3r.core.patchca.filter.predefined.DiffuseRippleFilterFactory;
import org.n3r.core.patchca.filter.predefined.DoubleRippleFilterFactory;
import org.n3r.core.patchca.filter.predefined.MarbleRippleFilterFactory;
import org.n3r.core.patchca.filter.predefined.WobbleRippleFilterFactory;
import org.n3r.core.patchca.service.Captcha;
import org.n3r.core.text.RRand;

public class RCaptchca {
    private static ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
    static {
        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
    }

    public static Captcha createCaptchca() {
        switch (RRand.randInt(5)) {
        case 0:
            cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
            break;
        case 1:
            cs.setFilterFactory(new MarbleRippleFilterFactory());
            break;
        case 2:
            cs.setFilterFactory(new DoubleRippleFilterFactory());
            break;
        case 3:
            cs.setFilterFactory(new WobbleRippleFilterFactory());
            break;
        case 4:
            cs.setFilterFactory(new DiffuseRippleFilterFactory());
            break;
        }
        Captcha captcha = cs.getCaptcha();

        return captcha;
    }
}
