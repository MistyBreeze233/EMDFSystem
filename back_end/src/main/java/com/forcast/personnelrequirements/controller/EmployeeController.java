package com.forcast.personnelrequirements.controller;

import com.forcast.personnelrequirements.service.IEmployeeService;
import com.forcast.personnelrequirements.vo.EmployeeVo.EmployeeEntryVo;
import com.forcast.personnelrequirements.vo.RespBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private IEmployeeService employeeService;

    @PostMapping("/entry")
    public RespBean entry(@RequestBody @Valid EmployeeEntryVo vo) {
        return employeeService.employeeEntry(vo);
    }

    @PostMapping("/entryByExcel")
    public RespBean entryByExcel(@RequestBody @Valid MultipartFile file) {
        return employeeService.employeeEntryByExcel(file);
    }
}
