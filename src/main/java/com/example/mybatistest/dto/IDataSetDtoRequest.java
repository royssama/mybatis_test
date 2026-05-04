package com.example.mybatistest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IDataSetDtoRequest {

    @NotBlank(message = "test01 must not be blank")
    private String test01;

    @NotBlank(message = "test02 must not be blank")
    private String test02;

    @NotBlank(message = "test03 must not be blank")
    private String test03;

    private boolean active = true;
    private boolean includeScore = true;
}
