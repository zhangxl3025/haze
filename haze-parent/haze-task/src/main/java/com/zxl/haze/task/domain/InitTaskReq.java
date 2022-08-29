package com.zxl.haze.task.domain;

import lombok.Data;

@Data
public class InitTaskReq {

    private String taskId;
    private String fileName;
    private Integer taskType;
    private String inputParams;

}
