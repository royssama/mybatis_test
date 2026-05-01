package com.example.mybatistest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
public class BasicDtoRequest {

    @NotEmpty(message = "columns must not be empty")
    private List<Map<String, String>> columns;

    @NotBlank(message = "test01 must not be blank")
    private String test01;

    @NotBlank(message = "test02 must not be blank")
    private String test02;

    @NotBlank(message = "test03 must not be blank")
    private String test03;

}
