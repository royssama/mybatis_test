package com.example.mybatistest.dataset;

import java.util.Map;

public interface OnlineContextAdapter {

    void setAttribute(String name, Object value);

    Object getAttribute(String name);

    Map<String, Object> getAttributes();

    void setDataSet(DataSetAdapter dataSet);

    DataSetAdapter getDataSet();
}
