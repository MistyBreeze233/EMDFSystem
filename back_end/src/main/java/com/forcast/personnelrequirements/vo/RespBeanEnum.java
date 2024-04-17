package com.forcast.personnelrequirements.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    SUCCESS(200, "成功"),

    ERROR(500, "服务端异常"),

    //通用错误5000xx
    DUPLICATE_ORDER(5001, "重复订单"),
    MISSING_FILE(5002, "缺少文件"),
    UPLOAD_FAILED(5003, "上传失败"),
    ENTRY_FAILED(5004, "导入失败"),
    COMPANY_MISMATCH(5005, "没有公司匹配");

    private final long code;
    private final String message;
}
