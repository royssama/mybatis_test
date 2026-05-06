package com.example.mybatistest.dataset;

import java.util.List;
import java.util.Map;

public interface RecordSetAdapter {

    String getName();

    void addRow(Map<String, Object> row);

    List<Map<String, Object>> getRows();

    int getRowCount();
}
