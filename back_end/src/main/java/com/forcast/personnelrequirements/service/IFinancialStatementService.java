package com.forcast.personnelrequirements.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.forcast.personnelrequirements.entity.MonthlyFinancialReport;
import com.forcast.personnelrequirements.vo.FinancialStatement.FinancialStatementVo;
import com.forcast.personnelrequirements.vo.RespBean;
import org.springframework.web.multipart.MultipartFile;

public interface IFinancialStatementService extends IService<MonthlyFinancialReport> {
    RespBean financialStatementEntry(FinancialStatementVo vo);

    RespBean financialStatementsEntryByExcel(MultipartFile file);
}
