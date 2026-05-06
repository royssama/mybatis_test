package com.example.mybatistest.dataset;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapOnlineContextAdapter implements OnlineContextAdapter {

    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private DataSetAdapter dataSet;

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public void setDataSet(DataSetAdapter dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public DataSetAdapter getDataSet() {
        return dataSet;
    }
}
