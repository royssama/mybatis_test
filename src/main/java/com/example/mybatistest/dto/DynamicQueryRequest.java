package com.example.mybatistest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
public class DynamicQueryRequest {

    private List<Map<String, String>> columns;
    private boolean active;
    private boolean includeTraceColumn;

}
