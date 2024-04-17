package com.forcast.personnelrequirements.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.forcast.personnelrequirements.entity.Company;
import com.forcast.personnelrequirements.vo.RespBean;

public interface ICompanyService extends IService<Company> {
    RespBean input();
}
