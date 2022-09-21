package io.github.transfusion.deployapp.storagemanagementservice.db.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class JsonStringListConverter implements AttributeConverter<List<String>, JsonNode> {

    Logger logger = LoggerFactory.getLogger(JsonStringListConverter.class);

    @Autowired
    private Jackson2ObjectMapperBuilder mapperBuilder;

    /* private final TypeReference<List<String>> listTypeRef = new TypeReference<>() {
    }; */

    @Override
    public JsonNode convertToDatabaseColumn(List<String> data) {
        ObjectMapper mapper = mapperBuilder.build();
        if (null == data) {
            return null;
        }
        return mapper.valueToTree(data);
    }

    @Override
    public List<String> convertToEntityAttribute(JsonNode node) {
        ObjectMapper mapper = mapperBuilder.build();
        if (null == node || node.isEmpty()) {
            return Collections.emptyList();
        }
        return mapper.convertValue(node, List.class);
    }
}
