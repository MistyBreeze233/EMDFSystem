package com.forcast.personnelrequirements.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.forcast.personnelrequirements.entity.Company;
import com.forcast.personnelrequirements.entity.MonthlyFinancialReport;
import com.forcast.personnelrequirements.mapper.CompanyMapper;
import com.forcast.personnelrequirements.mapper.MonthlyFinancialReportMapper;
import com.forcast.personnelrequirements.service.IFinancialStatementService;
import com.forcast.personnelrequirements.vo.FinancialStatement.FinancialStatementVo;
import com.forcast.personnelrequirements.vo.RespBean;
import com.forcast.personnelrequirements.vo.RespBeanEnum;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class FinancialStatementImpl extends ServiceImpl<MonthlyFinancialReportMapper, MonthlyFinancialReport> implements IFinancialStatementService {
    @Autowired
    private MonthlyFinancialReportMapper statementMapper;

    @Autowired
    private CompanyMapper companyMapper;

    public RespBean financialStatementEntry(FinancialStatementVo entryVo) {
        try {
            insertSingleVo(entryVo);
        } catch (Exception e) {
            return RespBean.error(RespBeanEnum.COMPANY_MISMATCH);
        }
        return RespBean.success();
    }

    public RespBean financialStatementsEntryByExcel(MultipartFile file) {
        if (file.isEmpty()) {
            return RespBean.error(RespBeanEnum.MISSING_FILE);
        }
        try {
            List<FinancialStatementVo> voList = parseExcel(file.getInputStream());
            //将获取到的list导入数据库
            for (FinancialStatementVo vo : voList) {
                insertSingleVo(vo);
            }
            return RespBean.success(voList);
        } catch (Exception e) {
            return RespBean.error(RespBeanEnum.COMPANY_MISMATCH);
        }
    }

    private List<FinancialStatementVo> parseExcel(java.io.InputStream inputStream) throws IOException {
        List<FinancialStatementVo> voList = new ArrayList<>();

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
            //共对应五个属性
            Cell firstCell = cellIterator.next();
            Cell secondCell = cellIterator.next();
            Cell thirdCell = cellIterator.next();
            Cell fourthCell = cellIterator.next();
            Cell fifthCell = cellIterator.next();

            FinancialStatementVo vo = new FinancialStatementVo();
            vo.setCompanyName(String.valueOf(firstCell));
            vo.setYear((int) secondCell.getNumericCellValue());
            vo.setMonth((int) thirdCell.getNumericCellValue());
            vo.setRevenue(fourthCell.getNumericCellValue());
            vo.setProfit(fifthCell.getNumericCellValue());

            voList.add(vo);
        }
        workbook.close();
        return voList;
    }

    private void insertSingleVo(FinancialStatementVo vo) throws Exception {
        MonthlyFinancialReport statement = new MonthlyFinancialReport();
        Company company = companyMapper.selectOne(new QueryWrapper<Company>().eq("company_name", vo.getCompanyName()));
        int companyId;
        if (null != company) {
            companyId = company.getId();
        } else {
            throw new Exception("COMPANY_MISMATCH");
        }
        statement.setCompanyId(companyId);
        statement.setYear(vo.getYear());
        statement.setMonth(vo.getMonth());
        statement.setRevenue(vo.getRevenue());
        statement.setProfit(vo.getProfit());
        statementMapper.insert(statement);
    }
}
