package com.example.mybatistest.dataset;

import java.util.List;
import java.util.Map;

/**
 * Project-owned Dataset abstraction used when the NEXCORE framework jar is not available.
 */
public interface DataSetAdapter {

    void putField(String name, Object value);

    <T> T getField(String name);

    Map<String, Object> getFields();

    Map<String, Object> getFieldMap();

    void putFieldMap(Map<String, Object> fieldMap);

    void putRecordSet(String recordSetName, RecordSetAdapter recordSet);

    RecordSetAdapter getRecordSet(String recordSetName);

    Map<String, RecordSetAdapter> getRecordSetMap();

    void addRow(String recordSetName, Map<String, String> row);

    List<Map<String, String>> getRows(String recordSetName);

    Map<String, List<Map<String, String>>> getRecordSets();

    int getRecordCount();

    Map<String, String> getRecord(int index);
}
