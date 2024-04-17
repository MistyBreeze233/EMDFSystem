package com.forcast.personnelrequirements.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {

    private Boolean success;
    private long code;
    private String message;
    private Object data;

    //成功
    public static RespBean success() {
        return new RespBean(true, RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);
    }
    //成功+数据
    public static RespBean success(Object data) {
        return new RespBean(true, RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), data);
    }
    //失败
    public static RespBean error(RespBeanEnum respBeanEnum) {
        return new RespBean(false, respBeanEnum.getCode(), respBeanEnum.getMessage(), null);
    }
    //失败+自定义信息
    public static RespBean error(RespBeanEnum respBeanEnum, String msg) {
        return new RespBean(false, respBeanEnum.getCode(), msg, null);
    }
    //失败+数据
    public static RespBean error(RespBeanEnum respBeanEnum, Object data) {
        return new RespBean(false, respBeanEnum.getCode(), respBeanEnum.getMessage(), data);
    }
}
