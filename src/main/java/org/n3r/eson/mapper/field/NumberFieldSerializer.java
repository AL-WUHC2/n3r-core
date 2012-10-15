package org.n3r.eson.mapper.field;

import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;
import org.n3r.eson.parser.FieldInfo;

public class NumberFieldSerializer extends FieldSerializer {
    public NumberFieldSerializer(FieldInfo fieldInfo) {
        super(fieldInfo);
    }

    @Override
    public void writeProperty(EsonSerializer serializer, Object propertyValue) throws Exception {

        writePrefix(serializer);

        SerializeWriter out = serializer.getWriter();
        if (propertyValue == null) out.writeNull();
        else out.write(propertyValue.toString());
    }
}
