package com.forcast.personnelrequirements.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.forcast.personnelrequirements.entity.Employee;
import com.forcast.personnelrequirements.vo.EmployeeVo.EmployeeEntryVo;
import com.forcast.personnelrequirements.vo.RespBean;
import org.springframework.web.multipart.MultipartFile;

public interface IEmployeeService extends IService<Employee> {
    RespBean employeeEntry(EmployeeEntryVo entryVo);

    RespBean employeeEntryByExcel(MultipartFile file);
}
