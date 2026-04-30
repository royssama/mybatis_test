package com.example.mybatistest.api;

import com.example.mybatistest.service.DynamicQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
