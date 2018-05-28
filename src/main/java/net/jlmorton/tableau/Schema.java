package net.jlmorton.tableau;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableausoftware.common.Type;

public class Schema {
    private String _schemaName;
    private Map<String, Type> _schemaDefinition = new LinkedHashMap<>();

    private Schema() {
    }

    static Schema newInstance() {
        return new Schema();
    }

    Schema addColumn(String columnName, Type type) {
        _schemaDefinition.put(columnName, type);
        return this;
    }

    public Map<String, Type> getSchema() {
        return _schemaDefinition;
    }

    static Schema fromJson(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File schemaFile = new File(fileName);
        if (!schemaFile.exists()) {
            System.err.println("Error: Schema does not exist: " + fileName);
        }

        return objectMapper.readValue(schemaFile, Schema.class);
    }

    @JsonSetter
    public void setSchemaName(String schemaName) {
        _schemaName = schemaName;
    }

    public String getName() {
        return _schemaName;
    }
}
