package com.forcast.personnelrequirements.vo.CompanyVo;

import lombok.Data;

@Data
public class InfoVo {
    private String companyName;

    private String address;

    //公司行业种类
    private String industry;

    //0表示小，1表示中，2表示大
    private Integer size;
}
