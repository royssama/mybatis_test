package com.example.mybatistest;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.mybatistest.dto.DataSetAdapterRequest;
import com.example.mybatistest.dto.DataSetAdapterResponse;
import com.example.mybatistest.dto.IDataSetDtoRequest;
import com.example.mybatistest.dto.IDataSetDtoResponse;
import com.example.mybatistest.service.DynamicQueryService;
import java.util.HashMap;
import java.util.Map;
import nexcore.framework.core.data.DtaSet;
import nexcore.framework.core.data.IDataSet;
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
        IDataSet req = new DtaSet();
        req.putField("test", "A");
        req.addRow("records", Map.of("test002", "B"));

        String test01 = req.getField("test");
        Map<String, String> wkMap = new HashMap<>();
        wkMap.put("test002", req.getRecord(0).get("test002"));

        assertThat(test01).isEqualTo("A");
        assertThat(wkMap).containsEntry("test002", "B");
    }
}
