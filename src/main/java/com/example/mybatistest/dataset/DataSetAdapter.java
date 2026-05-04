package com.example.mybatistest.dataset;

import java.util.List;
import java.util.Map;

/**
 * Project-owned Dataset abstraction used when the NEXCORE framework jar is not available.
 */
public interface DataSetAdapter {

    void putField(String name, Object value);

    Object getField(String name);

    Map<String, Object> getFields();

    void addRow(String recordSetName, Map<String, Object> row);

    List<Map<String, Object>> getRows(String recordSetName);

    Map<String, List<Map<String, Object>>> getRecordSets();
}
