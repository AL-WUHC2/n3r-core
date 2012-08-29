package org.n3r.core.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

public class RClassPath {
    public static String toStr(String resClasspath) {
        return toStr(resClasspath, Charsets.UTF_8);
    }

    public static List<String> toLines(String resClasspath) {
        return toLines(resClasspath, Charsets.UTF_8);
    }

    public static List<String> toLines(String resClasspath, Charset charset) {
        InputStream is = toInputStream(resClasspath);
        try {
            return CharStreams.readLines(new InputStreamReader(is, charset));
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
        finally {
            Closeables.closeQuietly(is);
        }
    }

    public static String toStr(String resClasspath, Charset charset) {
        InputStream is = toInputStream(resClasspath);
        try {
            return CharStreams.toString(new InputStreamReader(is, charset));
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
        finally {
            Closeables.closeQuietly(is);
        }
    }

    public static InputStream toInputStream(String resClasspath) {
        InputStream res = RClassPath.class.getResourceAsStream(resClasspath);
        if (res == null) throw new RuntimeException(resClasspath + " does not exist");

        return res;
    }

    public static Reader toReader(String resClasspath) {
        InputStream inputStream = toInputStream(resClasspath);
        return new InputStreamReader(inputStream, Charsets.UTF_8);
    }

    public static List<Class<?>> getClasses(String basePackage, Predicate<Class<?>> predicate) {
        return getClasses(basePackage, "**/*.class", predicate, null);
    }

    public static List<Class<?>> getClasses(String basePackage, String pattern, Predicate<Class<?>> classPredicate,
            Predicate<String> classNamePredicate) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metaFactory = new CachingMetadataReaderFactory(resolver);

        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + "/" + pattern;
            Resource[] resources = resolver.getResources(packageSearchPath);

            ArrayList<Class<?>> clazzArr = new ArrayList<Class<?>>();
            for (Resource res : resources) {
                if (!res.isReadable()) continue;

                MetadataReader metadataReader = metaFactory.getMetadataReader(res);
                String className = metadataReader.getClassMetadata().getClassName();
                if (classNamePredicate != null && !classNamePredicate.apply(className)) continue;
                Class<?> clazz = RClass.findClass(className);
                if (clazz != null && classPredicate.apply(clazz)) clazzArr.add(clazz);
            }
            return clazzArr;
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    public static List<Class<?>> getSubClasses(String basePackage, final Class<?> superClass) {
        return getSubClasses(basePackage, "**/*.class", superClass);
    }

    public static List<Class<?>> getSubClasses(String basePackage, String pattern, final Class<?> superClass) {
        return getClasses(basePackage, pattern, new Predicate<Class<?>>() {
            @Override
            public boolean apply(@Nullable Class<?> input) {
                return superClass.isAssignableFrom(input);
            }
        }, null);
    }

    public static List<Class<?>> getAnnotatedClasses(String basePackage, final Class<? extends Annotation> annClass,
             final Class<?>... excludesClasses) {
        return getAnnotatedClasses(basePackage, "**/*.class", annClass, excludesClasses);
    }

    public static List<Class<?>> getAnnotatedClasses(String basePackage, String pattern,
            final Class<? extends Annotation> annClass, final Class<?>... excludesClasses) {
        return getClasses(basePackage, pattern, new Predicate<Class<?>>() {

            @Override
            public boolean apply(@Nullable Class<?> input) {
                return input.isAnnotationPresent(annClass);
            }
        }, Predicates.not(new Predicate<String>() {

            @Override
            public boolean apply(String input) {
                for (Class<?> class1 : excludesClasses) {
                    if (input.equals(class1.getName()))
                        return true;
                    }
                    return false;
                }
        }));
    }

    public static Resource getResource(String classPathPattern) {
        return new ClassPathResource(classPathPattern);
    }

    public static Resource[] getResources(String basePackage, String classPathPattern) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + "/" + classPathPattern;
            return resolver.getResources(packageSearchPath);
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
