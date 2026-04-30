package com.example.mybatistest.mapper;

import com.example.mybatistest.dto.DynamicQueryRequest;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DynamicQueryMapper {

    Map<String, Object> selectDynamicColumns(DynamicQueryRequest request);
}
