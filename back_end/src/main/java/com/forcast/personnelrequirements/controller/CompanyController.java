package com.forcast.personnelrequirements.controller;

import com.forcast.personnelrequirements.service.ICompanyService;
import com.forcast.personnelrequirements.vo.CompanyVo.InfoVo;
import com.forcast.personnelrequirements.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@Validated
public class CompanyController {
//    @Autowired
//    private ICompanyService companyService;
//
//    @PostMapping("/enterInfo")
//    public RespBean enterInfo(@RequestBody InfoVo infoVo, HttpServletRequest request, HttpServletResponse response) {
//        return companyService.input();
//    }
}
