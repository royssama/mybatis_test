package com.example.mybatistest.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapRecordSetAdapter implements RecordSetAdapter {

    private final String name;
    private final List<Map<String, String>> rows = new ArrayList<>();

    public MapRecordSetAdapter(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addRow(Map<String, String> row) {
        rows.add(row);
    }

    @Override
    public List<Map<String, String>> getRows() {
        return rows;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }
}
