package com.example.mybatistest;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.mybatistest.dto.DataSetAdapterRequest;
import com.example.mybatistest.dto.DataSetAdapterResponse;
import com.example.mybatistest.dto.IDataSetDtoRequest;
import com.example.mybatistest.dto.IDataSetDtoResponse;
import com.example.mybatistest.service.DynamicQueryService;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IRecordSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DynamicQueryServiceTest {

    @Autowired
    private DynamicQueryService dynamicQueryService;

    @Test
    void selectTestDataUsesDynamicColumnsFromMapperXml() {
        Map<String, Object> result = dynamicQueryService.selectTestData(true, true);

        assertThat(result).containsEntry("USERID", "U001");
        assertThat(result).containsEntry("USERNAME", "Test User");
        assertThat(result).containsEntry("STATUSNAME", "ACTIVE");
        assertThat(result).containsEntry("SCORE", 100);
        assertThat(result).containsEntry("QUERYSOURCE", "MYBATIS_XML");
    }

    @Test
    void selectTestDataCanSkipOptionalDynamicColumn() {
        Map<String, Object> result = dynamicQueryService.selectTestData(false, false);

        assertThat(result).containsEntry("STATUSNAME", "INACTIVE");
        assertThat(result).doesNotContainKey("SCORE");
    }

    @Test
    void selectIDataSetColumnsConvertsDtoThroughDataSetFlow() {
        IDataSetDtoRequest request = new IDataSetDtoRequest();
        request.setTest01("A");
        request.setTest02("B");
        request.setTest03("C");
        request.setActive(true);
        request.setIncludeScore(true);

        IDataSetDtoResponse response = dynamicQueryService.selectIDataSetColumns(request);

        assertThat(response.getFields()).containsEntry("test01", "A");
        assertThat(response.getColumns()).hasSize(4);
        assertThat(response.getColumns().get(0)).containsEntry("recordIndex", "0");
        assertThat(response.getRow()).containsEntry("USERID", "U001");
        assertThat(response.getRow()).containsEntry("STATUSNAME", "ACTIVE");
        assertThat(response.getRow()).containsEntry("SCORE", 100);
    }

    @Test
    void selectAdapterDataSetColumnsUsesProjectOwnedDataSetWithoutNexcoreJar() {
        DataSetAdapterRequest request = new DataSetAdapterRequest();
        request.setTest01("A");
        request.setTest02("B");
        request.setTest03("C");
        request.setActive(false);
        request.setIncludeScore(false);

        DataSetAdapterResponse response = dynamicQueryService.selectAdapterDataSetColumns(request);

        assertThat(response.getFields()).containsEntry("test01", "A");
        assertThat(response.getColumns()).hasSize(3);
        assertThat(response.getColumns().get(0)).containsEntry("recordIndex", "0");
        assertThat(response.getRow()).containsEntry("USERID", "U001");
        assertThat(response.getRow()).containsEntry("STATUSNAME", "INACTIVE");
        assertThat(response.getRow()).doesNotContainKey("SCORE");
    }

    @Test
    void dataSetSupportsAsIsStringAssignments() {
        IDataSet req = new DataSet();
        req.putField("test", "A");
        IDataSet record = dynamicQueryService.toRecordDataSet(List.of(Map.of("test002", "B")));

        String test01 = req.getField("test");
        Map<String, String> wkMap = new HashMap<>();
        wkMap.put("test002", record.getRecord(0).get("test002"));

        assertThat(test01).isEqualTo("A");
        assertThat(wkMap).containsEntry("test002", "B");
    }

    @Test
    void convertsDtoToDataSetAndBackToDto() {
        IDataSetDtoRequest request = new IDataSetDtoRequest();
        request.setTest01("A");
        request.setTest02("B");
        request.setTest03("C");
        request.setActive(true);
        request.setIncludeScore(false);

        IDataSet req = dynamicQueryService.toDataSetAdapter(request);
        IDataSetDtoRequest converted = dynamicQueryService.toDto(req, IDataSetDtoRequest.class);

        assertThat(req.getFieldMap())
                .containsEntry("TEST01", "A")
                .containsEntry("TEST02", "B")
                .containsEntry("TEST03", "C")
                .containsEntry("ACTIVE", true)
                .containsEntry("INCLUDE_SCORE", false);
        assertThat(converted.getTest01()).isEqualTo("A");
        assertThat(converted.getTest02()).isEqualTo("B");
        assertThat(converted.getTest03()).isEqualTo("C");
        assertThat(converted.isActive()).isTrue();
        assertThat(converted.isIncludeScore()).isFalse();
    }

    @Test
    void putFieldMapMergesListValuesWithoutDroppingDtoFields() {
        IDataSetDtoRequest request = new IDataSetDtoRequest();
        request.setTest01("A");
        request.setTest02("B");
        request.setTest03("C");
        request.setActive(true);
        request.setIncludeScore(false);

        IDataSet req = dynamicQueryService.toDataSetAdapter(request);
        List<Map<String, Object>> list = List.of(Map.of("value", "data001-row"));
        List<Map<String, Object>> list2 = List.of(Map.of("value", "data002-row"));
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("data001", list);
        paraMap.put("data002", list2);

        req.putFieldMap(paraMap);
        IDataSetDtoRequest converted = dynamicQueryService.toDto(req, IDataSetDtoRequest.class);

        assertThat(req.getFieldMap())
                .containsEntry("TEST01", "A")
                .containsEntry("TEST02", "B")
                .containsEntry("data001", list)
                .containsEntry("data002", list2);
        assertThat(converted.getTest01()).isEqualTo("A");
        assertThat(converted.getTest02()).isEqualTo("B");
        assertThat(converted.getTest03()).isEqualTo("C");
    }

    @Test
    void convertsListMapResultToRecordDataSet() {
        List<Map<String, Object>> sList = List.of(
                Map.of("test001", "A", "test002", "B"),
                Map.of("test001", "C", "test002", "D")
        );

        IDataSet record = dynamicQueryService.toRecordDataSet(sList);

        assertThat(record.getRecordCount()).isEqualTo(2);
        assertThat(record.getRecord(0)).containsEntry("test001", "A");
        assertThat(record.getRecord(0)).containsEntry("test002", "B");
        assertThat(record.getRecord(1)).containsEntry("test001", "C");
    }

    @Test
    void convertsListMapResultToIRecordSet() {
        List<Map<String, Object>> sList = List.of(
                Map.of("test001", "A", "test002", "B"),
                Map.of("test001", "C", "test002", "D")
        );

        IRecordSet sRecord = dynamicQueryService.toRecordSet(sList);

        assertThat(sRecord.getName()).isEqualTo("records");
        assertThat(sRecord.getRowCount()).isEqualTo(2);
        assertThat(sRecord.getRows().get(0)).containsEntry("test001", "A");
        assertThat(sRecord.getRows().get(0)).containsEntry("test002", "B");
        assertThat(sRecord.getRows().get(1)).containsEntry("test001", "C");
    }

    @Test
    void getListStyleExampleReturnsRecordsFromMapperLikeList() {
        IDataSetDtoRequest request = new IDataSetDtoRequest();
        request.setTest01("A");
        request.setTest02("B");
        request.setTest03("C");
        request.setActive(true);
        request.setIncludeScore(true);

        List<Map<String, Object>> rows = dynamicQueryService.getList(request);

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0)).containsEntry("test001", "A");
        assertThat(rows.get(0)).containsEntry("test002", "ROW-001");
        assertThat(rows.get(1)).containsEntry("test001", "B");
        assertThat(rows.get(1)).containsEntry("test002", "ROW-002");
    }
}
