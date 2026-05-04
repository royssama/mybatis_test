package com.example.mybatistest.api;

import com.example.mybatistest.dto.BasicDtoRequest;
import com.example.mybatistest.dto.DatasetDtoRequest;
import com.example.mybatistest.dto.IDataSetDtoRequest;
import com.example.mybatistest.dto.IDataSetDtoResponse;
import com.example.mybatistest.service.DynamicQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

@Tag(name = "MyBatis XML sample", description = "Dynamic column select example")
@RestController
@RequestMapping("/api/mybatis")
public class DynamicQueryController {

    private final DynamicQueryService dynamicQueryService;

    public DynamicQueryController(DynamicQueryService dynamicQueryService) {
        this.dynamicQueryService = dynamicQueryService;
    }

    @Operation(summary = "Select test data through MyBatis XML")
    @GetMapping("/dynamic-select")
    public Map<String, Object> selectDynamicColumns(
            @RequestParam(defaultValue = "true") boolean active,
            @RequestParam(defaultValue = "true") boolean includeScore
    ) {
        return dynamicQueryService.selectTestData(active, includeScore);
    }

    @Operation(summary = "Select test data use dataset MyBatis XML")
    @PostMapping("/Basic-select")
    public Map<String, Object> selectBasicColumns(@Valid @RequestBody BasicDtoRequest dto) {
        return dynamicQueryService.selectBasicColumns(dto);
    }

    @Operation(summary = "Select test data use dataset MyBatis XML")
    @PostMapping("/dataset-select")
    public Map<String, Object> selectDatasetColumns(@Valid @RequestBody DatasetDtoRequest dto) {
        return dynamicQueryService.selectDatasetColumns(dto);
    }

    @Operation(summary = "DTO request converted to NEXCORE IDataSet style flow")
    @PostMapping("/idata-set-select")
    public IDataSetDtoResponse selectIDataSetColumns(@Valid @RequestBody IDataSetDtoRequest dto) {
        return dynamicQueryService.selectIDataSetColumns(dto);
    }
}
