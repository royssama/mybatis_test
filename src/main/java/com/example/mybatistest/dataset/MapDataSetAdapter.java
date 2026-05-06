package com.example.mybatistest.dataset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapDataSetAdapter implements DataSetAdapter {

    private final Map<String, Object> fields = new LinkedHashMap<>();
    private final Map<String, RecordSetAdapter> recordSets = new LinkedHashMap<>();

    @Override
    public void putField(String name, Object value) {
        fields.put(name, value);
    }

    @Override
    public <T> T getField(String name) {
        return (T) fields.get(name);
    }

    @Override
    public Map<String, Object> getFields() {
        return fields;
    }

    @Override
    public Map<String, Object> getFieldMap() {
        return fields;
    }

    @Override
    public void putFieldMap(Map<String, Object> fieldMap) {
        fields.clear();
        fields.putAll(fieldMap);
    }

    @Override
    public void putRecordSet(String recordSetName, RecordSetAdapter recordSet) {
        recordSets.put(recordSetName, recordSet);
    }

    @Override
    public RecordSetAdapter getRecordSet(String recordSetName) {
        return recordSets.get(recordSetName);
    }

    @Override
    public Map<String, RecordSetAdapter> getRecordSetMap() {
        return recordSets;
    }

    @Override
    public void addRow(String recordSetName, Map<String, String> row) {
        recordSets.computeIfAbsent(recordSetName, MapRecordSetAdapter::new).addRow(row);
    }

    @Override
    public List<Map<String, String>> getRows(String recordSetName) {
        RecordSetAdapter recordSet = recordSets.get(recordSetName);
        return recordSet == null ? List.of() : recordSet.getRows();
    }

    @Override
    public Map<String, List<Map<String, String>>> getRecordSets() {
        Map<String, List<Map<String, String>>> rowsByName = new LinkedHashMap<>();
        for (Map.Entry<String, RecordSetAdapter> entry : recordSets.entrySet()) {
            rowsByName.put(entry.getKey(), entry.getValue().getRows());
        }
        return rowsByName;
    }

    @Override
    public int getRecordCount() {
        RecordSetAdapter recordSet = firstRecordSet();
        return recordSet == null ? 0 : recordSet.getRowCount();
    }

    @Override
    public Map<String, String> getRecord(int index) {
        RecordSetAdapter recordSet = firstRecordSet();
        if (recordSet == null) {
            throw new IndexOutOfBoundsException("No record set exists");
        }
        return recordSet.getRows().get(index);
    }

    private RecordSetAdapter firstRecordSet() {
        return recordSets.values().stream().findFirst().orElse(null);
    }
}
