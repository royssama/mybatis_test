package com.example.mybatistest.dto;

import java.util.List;
import java.util.Map;

public class DynamicQueryRequest {

    private List<Map<String, Object>> columns;
    private boolean active;
    private boolean includeTraceColumn;

    public List<Map<String, Object>> getColumns() {
        return columns;
    }

    public void setColumns(List<Map<String, Object>> columns) {
        this.columns = columns;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isIncludeTraceColumn() {
        return includeTraceColumn;
    }

    public void setIncludeTraceColumn(boolean includeTraceColumn) {
        this.includeTraceColumn = includeTraceColumn;
    }
}
