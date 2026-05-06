package com.example.mybatistest.dto;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IDataSetDtoResponse {

    private Map<String, Object> fields;
    private List<Map<String, String>> columns;
    private Map<String, Object> row;
}
