package com.forcast.personnelrequirements.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.forcast.personnelrequirements.entity.Company;
import com.forcast.personnelrequirements.mapper.CompanyMapper;
import com.forcast.personnelrequirements.service.ICompanyService;
import com.forcast.personnelrequirements.vo.RespBean;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements ICompanyService {
    @Override
    public RespBean input() {
        System.out.println("输入");
        return RespBean.success();
    }
}
