package com.example.mybatistest.mapper;

import com.example.mybatistest.dto.BasicDtoRequest;
import com.example.mybatistest.dto.DatasetDtoRequest;
import com.example.mybatistest.dto.DynamicQueryRequest;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DynamicQueryMapper {

    Map<String, Object> selectDynamicColumns(DynamicQueryRequest request);

    Map<String, Object> selectBasicColumns(BasicDtoRequest dto);
    Map<String, Object> selectDatasetColumns(DatasetDtoRequest dto);
}
