/*
 * Copyright (c) 2009 Piotr Piastucki
 * 
 * This file is part of Patchca CAPTCHA library.
 * 
 *  Patchca is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Patchca is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Patchca. If not, see <http://www.gnu.org/licenses/>.
 */
package org.n3r.core.patchca.custom;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.n3r.core.joor.Reflect;
import org.n3r.core.lang.RClassPath;
import org.n3r.core.lang.RMethod;
import org.n3r.core.lang.RType;
import org.n3r.core.patchca.background.SingleColorBackgroundFactory;
import org.n3r.core.patchca.color.SingleColorFactory;
import org.n3r.core.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.n3r.core.patchca.font.RandomFontFactory;
import org.n3r.core.patchca.service.AbstractCaptchaService;
import org.n3r.core.patchca.text.renderer.BestFitTextRenderer;
import org.n3r.core.patchca.word.AdaptiveRandomWordFactory;
import org.n3r.core.patchca.word.WordFactory;

public class ConfigurableCaptchaService extends AbstractCaptchaService {
    private static ArrayList<WordFactory> wordFactories = new ArrayList<WordFactory>();
    static {
        for (Class<?> class1 : RClassPath.getAnnotatedClasses("org.n3r.core.patchca.config", CaptchaWordFactoryConfig.class)) {
            Object configObject = null;
            Method[] methods = class1.getMethods();
            for (Method method : methods) {
                if (method.getParameterTypes().length != 0) continue;

                if (WordFactory.class.isAssignableFrom(method.getReturnType())) {
                    if (!Modifier.isStatic(method.getModifiers()) && configObject == null) {
                        configObject = Reflect.on(class1).create().get();
                    }

                    WordFactory wordFactory = RMethod.invoke(method, configObject, new Object[] {});
                    if (wordFactory != null) {
                        wordFactories.add(wordFactory);
                    }
                }
                else if (List.class.isAssignableFrom(method.getReturnType())
                        && WordFactory.class.isAssignableFrom(RType.getActualTypeArgument(method.getGenericReturnType()))) {
                    if (!Modifier.isStatic(method.getModifiers()) && configObject == null) {
                        configObject = Reflect.on(class1).create().get();
                    }

                    List<WordFactory> factories = RMethod.invoke(method, configObject, new Object[] {});
                    if (wordFactories != null) {
                        wordFactories.addAll(factories);
                    }
                }
            }
        }

        if (wordFactories.size() == 0) {
            wordFactories.add(new AdaptiveRandomWordFactory());
        }
    }

    public ConfigurableCaptchaService() {
        backgroundFactory = new SingleColorBackgroundFactory();
        wordFactory = new RandomFactoryWordFactory(wordFactories);
        fontFactory = new RandomFontFactory();
        fontFactory.setWordFactory(wordFactory);
        textRenderer = new BestFitTextRenderer();
        colorFactory = new SingleColorFactory();
        filterFactory = new CurvesRippleFilterFactory(colorFactory);
        textRenderer.setLeftMargin(10);
        textRenderer.setRightMargin(10);
        width = 180;
        height = 70;
    }

}
