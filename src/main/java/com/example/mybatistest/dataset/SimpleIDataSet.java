package com.example.mybatistest.dataset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import nexcore.framework.core.data.IDataSet;

public class SimpleIDataSet implements IDataSet {

    private final Map<String, Object> fields = new LinkedHashMap<>();
    private final Map<String, List<Map<String, Object>>> recordSets = new LinkedHashMap<>();

    @Override
    public void putField(String name, Object value) {
        fields.put(name, value);
    }

    @Override
    public Object getField(String name) {
        return fields.get(name);
    }

    @Override
    public Map<String, Object> getFields() {
        return fields;
    }

    @Override
    public void addRow(String recordSetName, Map<String, Object> row) {
        recordSets.computeIfAbsent(recordSetName, key -> new ArrayList<>()).add(row);
    }

    @Override
    public List<Map<String, Object>> getRows(String recordSetName) {
        return recordSets.getOrDefault(recordSetName, List.of());
    }

    @Override
    public Map<String, List<Map<String, Object>>> getRecordSets() {
        return recordSets;
    }
}
