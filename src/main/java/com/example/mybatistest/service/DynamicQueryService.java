package com.example.mybatistest.service;

import com.example.mybatistest.dto.DynamicQueryRequest;
import com.example.mybatistest.mapper.DynamicQueryMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DynamicQueryService {

    private final DynamicQueryMapper dynamicQueryMapper;

    public DynamicQueryService(DynamicQueryMapper dynamicQueryMapper) {
        this.dynamicQueryMapper = dynamicQueryMapper;
    }

    public Map<String, Object> selectTestData(boolean active, boolean includeScore) {
        DynamicQueryRequest request = new DynamicQueryRequest();
        request.setActive(active);
        request.setIncludeTraceColumn(true);
        request.setColumns(buildTestColumns(includeScore));

        return dynamicQueryMapper.selectDynamicColumns(request);
    }

    private List<Map<String, Object>> buildTestColumns(boolean includeScore) {
        List<Map<String, Object>> columns = new ArrayList<>();
        columns.add(column("userId", "'U001'"));
        columns.add(column("userName", "'Test User'"));
        columns.add(column("statusName", "CASE WHEN #{active} THEN 'ACTIVE' ELSE 'INACTIVE' END"));

        if (includeScore) {
            columns.add(column("score", "100"));
        }

        return columns;
    }

    private Map<String, Object> column(String alias, String expression) {
        Map<String, Object> column = new LinkedHashMap<>();
        column.put("alias", alias);
        column.put("expression", expression);
        return column;
    }
}
