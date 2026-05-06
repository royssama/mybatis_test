package com.example.mybatistest.dataset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;

public class SimpleIDataSet implements IDataSet {

    private final Map<String, Object> fields = new LinkedHashMap<>();
    private final Map<String, IRecordSet> recordSets = new LinkedHashMap<>();

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
    public void putRecordSet(String recordSetName, IRecordSet recordSet) {
        recordSets.put(recordSetName, recordSet);
    }

    @Override
    public IRecordSet getRecordSet(String recordSetName) {
        return recordSets.get(recordSetName);
    }

    @Override
    public Map<String, IRecordSet> getRecordSetMap() {
        return recordSets;
    }

    @Override
    public void addRow(String recordSetName, Map<String, Object> row) {
        recordSets.computeIfAbsent(recordSetName, RecordSet::new).addRow(row);
    }

    @Override
    public List<Map<String, Object>> getRows(String recordSetName) {
        IRecordSet recordSet = recordSets.get(recordSetName);
        return recordSet == null ? List.of() : recordSet.getRows();
    }

    @Override
    public Map<String, List<Map<String, Object>>> getRecordSets() {
        Map<String, List<Map<String, Object>>> rowsByName = new LinkedHashMap<>();
        for (Map.Entry<String, IRecordSet> entry : recordSets.entrySet()) {
            rowsByName.put(entry.getKey(), entry.getValue().getRows());
        }
        return rowsByName;
    }
}
