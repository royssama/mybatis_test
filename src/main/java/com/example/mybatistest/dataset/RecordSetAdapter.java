package com.example.mybatistest.dataset;

import java.util.List;
import java.util.Map;

public interface RecordSetAdapter {

    String getName();

    void addRow(Map<String, String> row);

    List<Map<String, String>> getRows();

    int getRowCount();
}
