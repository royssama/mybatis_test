package com.example.mybatistest.service;

import com.example.mybatistest.dto.BasicDtoRequest;
import com.example.mybatistest.dto.DatasetDtoRequest;
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

        List<Map<String, String>> columns = new ArrayList<>();
        Map<String, String> row = new LinkedHashMap<>();
        row.put("alias", "userId");
        row.put("expression", "'U001'");
        columns.add(row);
        request.setColumns(columns);

        return dynamicQueryMapper.selectDynamicColumns(request);
    }


    public Map<String, Object> selectBasicColumns(BasicDtoRequest dto) {

        List<Map<String, String>> columns = new ArrayList<>();
        columns.add(column("userId", "'U001'"));
        columns.add(column("userName", "'Test User'"));
        columns.add(column("statusName", "'aaaa'"));


        dto.setColumns(columns);

        return dynamicQueryMapper.selectBasicColumns(dto);
    }









    private List<Map<String, String>> buildTestColumns(boolean includeScore) {
        List<Map<String, String>> columns = new ArrayList<>();
        columns.add(column("userId", "'U001'"));
        columns.add(column("userName", "'Test User'"));
        columns.add(column("statusName", "CASE WHEN #{active} THEN 'ACTIVE' ELSE 'INACTIVE' END"));

        if (includeScore) {
            columns.add(column("score", "100"));
        }

        return columns;
    }

    private Map<String, String> column(String alias, String expression) {
        Map<String, String> column = new LinkedHashMap<>();
        column.put("alias", alias);
        column.put("expression", expression);
        return column;
    }






    public Map<String, Object> selectDatasetColumns(DatasetDtoRequest dto) {
        List<Map<String, String>> columns = new ArrayList<>();
        columns.add(column("userId", "'U001'"));
        columns.add(column("userName", "'Test User'"));
        columns.add(column("statusName", "'aaaa'"));


        dto.setColumns(columns);


        // 1) DTO -> Legacy Dataset 형태로 변환
        LegacyDataset dataset = toLegacyDataset(dto);

        // 2) (예시) 기존 ServiceImpl에서 Dataset 기반 가공 로직 실행
        LegacyDataset normalizedDataset = normalizeDatasetForLegacy(dataset);

        // 3) 기존 Mapper가 DTO를 받으므로 Dataset -> DTO로 다시 변환해 재사용
        DatasetDtoRequest mappedDto = toDatasetDtoRequest(normalizedDataset);
        return dynamicQueryMapper.selectDatasetColumns(mappedDto);
    }

    private LegacyDataset toLegacyDataset(DatasetDtoRequest dto) {
        LegacyDataset dataset = new LegacyDataset();
        dataset.putVariable("test01", dto.getTest01());
        dataset.putVariable("test02", dto.getTest02());
        dataset.putVariable("test03", dto.getTest03());

        for (Map<String, String> column : dto.getColumns()) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("alias", column.get("alias"));
            row.put("expression", column.get("expression"));
            dataset.addRow(row);
        }
        return dataset;
    }

    private LegacyDataset normalizeDatasetForLegacy(LegacyDataset source) {
        LegacyDataset normalized = new LegacyDataset();
        normalized.getVariables().putAll(source.getVariables());

        for (Map<String, String> row : source.getRows()) {
            Map<String, String> copiedRow = new LinkedHashMap<>();
            copiedRow.put("alias", row.get("alias"));
            copiedRow.put("expression", row.get("expression"));
            normalized.addRow(copiedRow);
        }
        return normalized;
    }

    private DatasetDtoRequest toDatasetDtoRequest(LegacyDataset dataset) {
        DatasetDtoRequest dto = new DatasetDtoRequest();
        dto.setTest01(dataset.getVariables().get("test01"));
        dto.setTest02(dataset.getVariables().get("test02"));
        dto.setTest03(dataset.getVariables().get("test03"));
        dto.setColumns(dataset.getRows());
        return dto;
    }

    // Nexacro Dataset 전환 시뮬레이션용 간단 모델
    private static class LegacyDataset {
        private final Map<String, String> variables = new LinkedHashMap<>();
        private final List<Map<String, String>> rows = new ArrayList<>();

        void putVariable(String key, String value) {
            variables.put(key, value);
        }

        void addRow(Map<String, String> row) {
            rows.add(row);
        }

        Map<String, String> getVariables() {
            return variables;
        }

        List<Map<String, String>> getRows() {
            return rows;
        }
    }
}
