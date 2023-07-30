package dev.cybo.orbitalbans.database;

import java.util.HashMap;
import java.util.Map;

public class DatabaseRow {

    private final Map<String, Object> columns = new HashMap<>();

    public void addColumn(String columnName, Object value) {
        columns.put(columnName, value);
    }

    public Object getColumnValue(String columnName) {
        return columns.get(columnName);
    }

    public String getString(String columnName) {
        return (String) columns.get(columnName);
    }

    public Integer getInt(String columnName) {
        return (Integer) columns.get(columnName);
    }

    public Long getLong(String columnName) {
        return (Long) columns.get(columnName);
    }

    public Boolean getBoolean(String columnName) {
        return (Boolean) columns.get(columnName);
    }

    public Double getDouble(String columnName) {
        return (Double) columns.get(columnName);
    }

    public Float getFloat(String columnName) {
        return (Float) columns.get(columnName);
    }

    public byte[] getBlob(String columnName) {
        return (byte[]) columns.get(columnName);
    }

}
