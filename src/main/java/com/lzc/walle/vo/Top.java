package com.lzc.walle.vo;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Top {
    private Integer id;
    private String hostId;
    private Integer number;
    private String name;
    private List problem;


}
