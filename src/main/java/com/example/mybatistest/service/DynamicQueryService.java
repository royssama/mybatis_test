package com.example.mybatistest.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.mybatistest.dto.BasicDtoRequest;
import com.example.mybatistest.dataset.DataSetAdapter;
import com.example.mybatistest.dataset.MapDataSetAdapter;
import com.example.mybatistest.dataset.MapOnlineContextAdapter;
import com.example.mybatistest.dataset.MapRecordSetAdapter;
import com.example.mybatistest.dataset.OnlineContextAdapter;
import com.example.mybatistest.dataset.RecordSetAdapter;
import com.example.mybatistest.dto.DatasetDtoRequest;
import com.example.mybatistest.dto.DataSetAdapterRequest;
import com.example.mybatistest.dto.DataSetAdapterResponse;
import com.example.mybatistest.dto.DynamicQueryRequest;
import com.example.mybatistest.dto.IDataSetDtoRequest;
import com.example.mybatistest.dto.IDataSetDtoResponse;
import com.example.mybatistest.mapper.DynamicQueryMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.OnlineContext;
import nexcore.framework.core.data.RecordSet;
import org.springframework.stereotype.Service;

@Service
public class DynamicQueryService {

    private final DynamicQueryMapper dynamicQueryMapper;
    private final ObjectMapper objectMapper;

    public DynamicQueryService(DynamicQueryMapper dynamicQueryMapper, ObjectMapper objectMapper) {
        this.dynamicQueryMapper = dynamicQueryMapper;
        this.objectMapper = objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Map<String, Object> selectTestData(boolean active, boolean includeScore) {
        DynamicQueryRequest request = new DynamicQueryRequest();
        request.setActive(active);
        request.setIncludeTraceColumn(true);
        request.setColumns(buildTestColumns(includeScore));

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

    public IDataSetDtoResponse selectIDataSetColumns(IDataSetDtoRequest dto) {
        IOnlineContext onlineContext = toNexcoreOnlineContext(dto);
        IDataSet requestDataSet = onlineContext.getDataSet();

        // nexcore-framework.jar 사용 시 기존 ServiceImpl에서 흔히 보이는 4개 타입 흐름 예제:
        // IOnlineContext -> IDataSet(DataSet 구현체) -> IRecordSet -> IDataSet 결과.
        IDataSet processedDataSet = runLegacyIDataSetLogic(onlineContext, requestDataSet);

        DatasetDtoRequest mapperRequest = toDatasetDtoRequest(processedDataSet);
        Map<String, Object> row = dynamicQueryMapper.selectDatasetColumns(mapperRequest);

        return toIDataSetDtoResponse(processedDataSet, row);
    }

    public DataSetAdapterResponse selectAdapterDataSetColumns(DataSetAdapterRequest dto) {
        OnlineContextAdapter onlineContext = toAdapterOnlineContext(dto);
        DataSetAdapter requestDataSet = onlineContext.getDataSet();

        // nexcore-framework.jar 없이도 DataSet/IDataSet/IOnlineContext/IRecordSet 개념을 자체 타입으로 대체한다.
        DataSetAdapter processedDataSet = runAdapterDataSetLogic(onlineContext, requestDataSet);

        DatasetDtoRequest mapperRequest = toDatasetDtoRequest(processedDataSet);
        Map<String, Object> row = dynamicQueryMapper.selectDatasetColumns(mapperRequest);

        return toDataSetAdapterResponse(processedDataSet, row);
    }

    public List<Map<String, Object>> getList(IDataSetDtoRequest dto) {
        IDataSet req = toDataSetAdapter(dto);

        // TO-BE에서는 Mapper가 DTO로 조회한 List<Map<String, Object>>를 반환한다고 가정한다.
        List<Map<String, Object>> sList = List.of(
                Map.of("test001", req.getField("TEST01"), "test002", "ROW-001"),
                Map.of("test001", req.getField("TEST02"), "test002", "ROW-002")
        );

        IDataSet record = toRecordDataSet(sList);
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < record.getRecordCount(); i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.putAll(record.getRecord(i));
            result.add(row);
        }
        return result;
    }

    public IDataSet toDataSetAdapter(Object dto) {
        Map<String, Object> result = objectMapper.convertValue(dto, new TypeReference<>() {
        });

        IDataSet dataSet = new DataSet();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            String replaceKey = convertPropertyNameToUnderscoreName(entry.getKey()).toUpperCase();
            dataSet.putField(replaceKey, entry.getValue());
        }
        return dataSet;
    }

    public IDataSet toRecordDataSet(List<Map<String, Object>> sList) {
        IDataSet record = new DataSet();
        for (Map<String, Object> sourceRow : sList) {
            Map<String, String> row = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : sourceRow.entrySet()) {
                row.put(entry.getKey(), entry.getValue() == null ? null : String.valueOf(entry.getValue()));
            }
            record.addRow("records", row);
        }
        return record;
    }

    public <T> T toDto(IDataSet req, Class<T> dtoType) {
        Map<String, Object> dtoMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : req.getFieldMap().entrySet()) {
            dtoMap.put(convertUnderscoreNameToPropertyName(entry.getKey()), entry.getValue());
        }
        return objectMapper.convertValue(dtoMap, dtoType);
    }

    private String convertPropertyNameToUnderscoreName(String propertyName) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < propertyName.length(); i++) {
            char current = propertyName.charAt(i);
            if (Character.isUpperCase(current) && i > 0) {
                builder.append('_');
            }
            builder.append(Character.toUpperCase(current));
        }
        return builder.toString();
    }

    private String convertUnderscoreNameToPropertyName(String underscoreName) {
        StringBuilder builder = new StringBuilder();
        boolean upperNext = false;
        for (char current : underscoreName.toLowerCase().toCharArray()) {
            if (current == '_') {
                upperNext = true;
                continue;
            }
            builder.append(upperNext ? Character.toUpperCase(current) : current);
            upperNext = false;
        }
        return builder.toString();
    }

    private IOnlineContext toNexcoreOnlineContext(IDataSetDtoRequest dto) {
        IDataSet dataSet = new DataSet();
        dataSet.putField("test01", dto.getTest01());
        dataSet.putField("test02", dto.getTest02());
        dataSet.putField("test03", dto.getTest03());
        dataSet.putField("active", dto.isActive());
        dataSet.putField("includeScore", dto.isIncludeScore());

        IOnlineContext onlineContext = new OnlineContext();
        onlineContext.setAttribute("transactionId", "sample.selectIDataSetColumns");
        onlineContext.setAttribute("clientType", "swagger");
        onlineContext.setDataSet(dataSet);
        return onlineContext;
    }

    private IDataSet runLegacyIDataSetLogic(IOnlineContext context, IDataSet source) {
        IDataSet target = new DataSet();
        Map<String, Object> paramMap = new LinkedHashMap<>(source.getFieldMap());
        paramMap.put("transactionId", context.getAttribute("transactionId"));
        target.putFieldMap(paramMap);

        boolean active = Boolean.TRUE.equals(paramMap.get("active"));
        boolean includeScore = Boolean.TRUE.equals(paramMap.get("includeScore"));
        IRecordSet columns = new RecordSet("columns");
        columns.addRow(dataSetColumn("userId", "'U001'"));
        columns.addRow(dataSetColumn("userName", "'Test User'"));
        columns.addRow(dataSetColumn("statusName", active ? "'ACTIVE'" : "'INACTIVE'"));

        if (includeScore) {
            columns.addRow(dataSetColumn("score", "100"));
        }

        target.putRecordSet("columns", columns);
        for (int i = 0; i < target.getRecordCount(); i++) {
            target.getRecord(i).put("recordIndex", String.valueOf(i));
        }
        return target;
    }

    private Map<String, String> dataSetColumn(String alias, String expression) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("alias", alias);
        row.put("expression", expression);
        return row;
    }

    private DatasetDtoRequest toDatasetDtoRequest(IDataSet dataSet) {
        DatasetDtoRequest dto = new DatasetDtoRequest();
        String test01 = dataSet.getField("test01");
        String test02 = dataSet.getField("test02");
        String test03 = dataSet.getField("test03");
        dto.setTest01(test01);
        dto.setTest02(test02);
        dto.setTest03(test03);

        List<Map<String, String>> columns = new ArrayList<>();
        for (int i = 0; i < dataSet.getRecordCount(); i++) {
            Map<String, String> row = dataSet.getRecord(i);
            columns.add(column(String.valueOf(row.get("alias")), String.valueOf(row.get("expression"))));
        }
        dto.setColumns(columns);
        return dto;
    }

    private IDataSetDtoResponse toIDataSetDtoResponse(IDataSet dataSet, Map<String, Object> row) {
        IDataSetDtoResponse response = new IDataSetDtoResponse();
        response.setFields(dataSet.getFields());
        response.setColumns(dataSet.getRows("columns"));
        response.setRow(row);
        return response;
    }

    private OnlineContextAdapter toAdapterOnlineContext(DataSetAdapterRequest dto) {
        DataSetAdapter dataSet = new MapDataSetAdapter();
        dataSet.putField("test01", dto.getTest01());
        dataSet.putField("test02", dto.getTest02());
        dataSet.putField("test03", dto.getTest03());
        dataSet.putField("active", dto.isActive());
        dataSet.putField("includeScore", dto.isIncludeScore());

        OnlineContextAdapter onlineContext = new MapOnlineContextAdapter();
        onlineContext.setAttribute("transactionId", "sample.selectAdapterDataSetColumns");
        onlineContext.setAttribute("clientType", "swagger");
        onlineContext.setDataSet(dataSet);
        return onlineContext;
    }

    private DataSetAdapter runAdapterDataSetLogic(OnlineContextAdapter context, DataSetAdapter source) {
        DataSetAdapter target = new MapDataSetAdapter();
        Map<String, Object> paramMap = new LinkedHashMap<>(source.getFieldMap());
        paramMap.put("transactionId", context.getAttribute("transactionId"));
        target.putFieldMap(paramMap);

        boolean active = Boolean.TRUE.equals(paramMap.get("active"));
        boolean includeScore = Boolean.TRUE.equals(paramMap.get("includeScore"));
        RecordSetAdapter columns = new MapRecordSetAdapter("columns");
        columns.addRow(dataSetColumn("userId", "'U001'"));
        columns.addRow(dataSetColumn("userName", "'Test User'"));
        columns.addRow(dataSetColumn("statusName", active ? "'ACTIVE'" : "'INACTIVE'"));

        if (includeScore) {
            columns.addRow(dataSetColumn("score", "100"));
        }

        target.putRecordSet("columns", columns);
        for (int i = 0; i < target.getRecordCount(); i++) {
            target.getRecord(i).put("recordIndex", String.valueOf(i));
        }
        return target;
    }

    private DatasetDtoRequest toDatasetDtoRequest(DataSetAdapter dataSet) {
        DatasetDtoRequest dto = new DatasetDtoRequest();
        String test01 = dataSet.getField("test01");
        String test02 = dataSet.getField("test02");
        String test03 = dataSet.getField("test03");
        dto.setTest01(test01);
        dto.setTest02(test02);
        dto.setTest03(test03);

        List<Map<String, String>> columns = new ArrayList<>();
        for (int i = 0; i < dataSet.getRecordCount(); i++) {
            Map<String, String> row = dataSet.getRecord(i);
            columns.add(column(String.valueOf(row.get("alias")), String.valueOf(row.get("expression"))));
        }
        dto.setColumns(columns);
        return dto;
    }

    private DataSetAdapterResponse toDataSetAdapterResponse(DataSetAdapter dataSet, Map<String, Object> row) {
        DataSetAdapterResponse response = new DataSetAdapterResponse();
        response.setFields(dataSet.getFields());
        response.setColumns(dataSet.getRows("columns"));
        response.setRow(row);
        return response;
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
