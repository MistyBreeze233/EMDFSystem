package com.forcast.personnelrequirements.controller;

import com.forcast.personnelrequirements.service.IFinancialStatementService;
import com.forcast.personnelrequirements.vo.FinancialStatement.FinancialStatementVo;
import com.forcast.personnelrequirements.vo.RespBean;
import com.forcast.personnelrequirements.vo.RespBeanEnum;
import jakarta.validation.Valid;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/financialStatements")
@Validated
public class FinancialStatementController {
    @Autowired
    private IFinancialStatementService iFinancialStatementService;

    @PostMapping("/entry")
    public RespBean entry(@RequestBody @Valid FinancialStatementVo vo) {
        return iFinancialStatementService.financialStatementEntry(vo);
    }

    @PostMapping("/entryByExcel")
    public RespBean entryByExcel(@RequestBody @Valid MultipartFile file) {
        return iFinancialStatementService.financialStatementsEntryByExcel(file);
    }

}
