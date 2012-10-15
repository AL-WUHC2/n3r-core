package org.n3r.eson;

import java.util.ArrayList;

import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;
import org.n3r.eson.mapper.SerializerMapping;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.parser.ParserMapping;

/**
 * Easy JSON parser and serializer implementation base on fastjson source code.
 * @author Bingoo
 *
 */
@SuppressWarnings("unchecked")
public class Eson {
    private EsonFeature feature = new EsonFeature();
    // source for serialize or desecrialize
    private Object source;
    private Class<?> targetClass;
    private SerializerMapping serializerMapping = SerializerMapping.instance;

    public Eson() {}

    public Eson keyQuote(char quote) {
        feature.setKeyQuote(quote);
        return this;
    }

    public Eson valueQuote(char quote) {
        feature.setValQuote(quote);
        return this;
    }

    public String toString(Object source) {
        setSource(source);
        return toString();
    }

    @Override
    public String toString() {
        SerializeWriter out = new SerializeWriter(feature);

        try {
            EsonSerializer serializer = new EsonSerializer(out, serializerMapping);
            serializer.write(source);

            return out.toString();
        } finally {
            out.close();
        }
    }

    public Eson targetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        return this;
    }

    public <T> T parse(Class<T> targetClass) {
        this.targetClass = targetClass;
        return parse();
    }

    public <T> T parse() {
        return (T) parse((String) source);
    }

    public <T> T parse(String text, Class<?> targetClass) {
        this.targetClass = targetClass;
        return parse(text);
    }

    public <T> T parse(String text) {
        if (text == null) return null;

        JSONParser parser = new JSONParser(text, ParserMapping.getGlobalInstance());
        JSONScanner scanner = parser.getLexer();

        Object value = null;

        if (scanner.token() == JSONToken.LBRACKET) {
            if (targetClass != null) {
                ArrayList<T> list = new ArrayList<T>();
                parser.parseArray(targetClass, list);
                value = list;
            }
            else {
                JSONArray array = new JSONArray();
                parser.parseArray(array);
                value = array;
            }
        }
        else {
            if (targetClass != null) value = parser.parseObject(targetClass);
            else value = parser.parseObject();
        }
        parser.close();

        return (T) value;
    }

    public Eson sortKey(boolean sortKey) {
        feature.config(EsonFeature.SortKey, sortKey);
        return this;
    }

    public Eson prettyFormat(boolean prettyFormat) {
        feature.config(EsonFeature.PrettyFormat, prettyFormat);
        return this;
    }

    public Eson on(Object source) {
        this.source = source;
        return this;
    }

    public EsonFeature getFeature() {
        return feature;
    }

    public void setFeature(EsonFeature feature) {
        this.feature = feature;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Eson mapping(SerializerMapping serializerMapping) {
        this.serializerMapping = serializerMapping;
        return this;
    }

    public Eson std() {
        return keyQuote('"').valueQuote('"');
    }

}
