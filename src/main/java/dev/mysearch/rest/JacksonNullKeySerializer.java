package dev.mysearch.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

class JacksonNullKeySerializer extends StdSerializer<Object> {
    public JacksonNullKeySerializer() {
        this(null);
    }

    public JacksonNullKeySerializer(Class<Object> t) {
        super(t);
    }
    
    @Override
    public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused) 
      throws IOException, JsonProcessingException {
        jsonGenerator.writeFieldName("");
    }
}