package com.forcast.personnelrequirements.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.forcast.personnelrequirements.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
