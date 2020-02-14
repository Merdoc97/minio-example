package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExampleDto {

    private String name;

    private String secondName;
}
