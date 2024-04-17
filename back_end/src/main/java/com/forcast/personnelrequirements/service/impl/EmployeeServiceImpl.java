package com.forcast.personnelrequirements.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.forcast.personnelrequirements.entity.Company;
import com.forcast.personnelrequirements.entity.Employee;
import com.forcast.personnelrequirements.mapper.CompanyMapper;
import com.forcast.personnelrequirements.mapper.EmployeeMapper;
import com.forcast.personnelrequirements.service.IEmployeeService;
import com.forcast.personnelrequirements.vo.EmployeeVo.EmployeeEntryVo;
import com.forcast.personnelrequirements.vo.RespBean;
import com.forcast.personnelrequirements.vo.RespBeanEnum;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public RespBean employeeEntry(EmployeeEntryVo entryVo) {
        try {
            insertSingleVo(entryVo);
        } catch (Exception e) {
            return RespBean.error(RespBeanEnum.COMPANY_MISMATCH);
        }
        return RespBean.success();
    }

    @Override
    public RespBean employeeEntryByExcel(MultipartFile file) {
        if (file.isEmpty()) {
            return RespBean.error(RespBeanEnum.MISSING_FILE);
        }
        try {
            List<EmployeeEntryVo> voList = parseExcel(file.getInputStream());
            //将获取到的list导入数据库
            for (EmployeeEntryVo vo : voList) {
                insertSingleVo(vo);
            }
            return RespBean.success(voList);
        } catch (Exception e) {
            return RespBean.error(RespBeanEnum.COMPANY_MISMATCH);
        }
    }

    private List<EmployeeEntryVo> parseExcel(java.io.InputStream inputStream) throws IOException {
        List<EmployeeEntryVo> voList = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        //跳过第一行名称
        if (iterator.hasNext()) {
            iterator.next();
        }
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            //共对应三个属性
            Cell firstCell = cellIterator.next();
            Cell secondCell = cellIterator.next();
            Cell thirdCell = cellIterator.next();
            Cell fourthCell = cellIterator.next();
            Cell fifthCell = cellIterator.next();

            EmployeeEntryVo vo = new EmployeeEntryVo();
            vo.setCompanyName(String.valueOf(firstCell));
            vo.setName(secondCell.getStringCellValue());
            vo.setPosition(thirdCell.getStringCellValue());
            vo.setHiredDate(fourthCell.getLocalDateTimeCellValue());
            vo.setDismissalDate(fifthCell.getLocalDateTimeCellValue());

            voList.add(vo);
        }
        workbook.close();
        return voList;
    }

    private void insertSingleVo(EmployeeEntryVo vo) throws Exception {
        Employee employee = new Employee();
        Company company = companyMapper.selectOne(new QueryWrapper<Company>().
                eq("company_name", vo.getCompanyName()));
        int companyId;
        if (null != company) {
            companyId = company.getId();
        } else {
            throw new Exception("COMPANY_MISMATCH");
        }
        employee.setName(vo.getName());
        employee.setPosition(vo.getPosition());
        employee.setCompanyId(companyId);
        employee.setHiredDate(vo.getHiredDate());
        employee.setDismissalDate(vo.getDismissalDate());
        employeeMapper.insert(employee);
    }
}
