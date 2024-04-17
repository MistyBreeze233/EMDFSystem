package com.forcast.personnelrequirements.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.forcast.personnelrequirements.entity.Company;
import com.forcast.personnelrequirements.entity.Order;
import com.forcast.personnelrequirements.mapper.CompanyMapper;
import com.forcast.personnelrequirements.mapper.OrderMapper;
import com.forcast.personnelrequirements.service.IOrderService;
import com.forcast.personnelrequirements.vo.OrderVo.OrderEntryVo;
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
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public RespBean orderEntry(OrderEntryVo entryVo) {
        try {
            insertSingleVo(entryVo);
        } catch (Exception e) {
            return RespBean.error(RespBeanEnum.COMPANY_MISMATCH);
        }
        return RespBean.success();
    }

    @Override
    public RespBean orderEntryByExcel(MultipartFile file) {
        if (file.isEmpty()) {
            return RespBean.error(RespBeanEnum.MISSING_FILE);
        }
        try {
            List<OrderEntryVo> voList = parseExcel(file.getInputStream());
            //将获取到的list导入数据库
            for (OrderEntryVo vo : voList) {
                insertSingleVo(vo);
            }
            return RespBean.success(voList);
        } catch (Exception e) {
            return RespBean.error(RespBeanEnum.COMPANY_MISMATCH);
        }
    }

    private List<OrderEntryVo> parseExcel(java.io.InputStream inputStream) throws IOException {
        List<OrderEntryVo> voList = new ArrayList<>();

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

            OrderEntryVo vo = new OrderEntryVo();
            vo.setCompanyName(String.valueOf(firstCell));
            vo.setOrderDate(secondCell.getLocalDateTimeCellValue());
            vo.setAmount(thirdCell.getNumericCellValue());

            voList.add(vo);
        }
        workbook.close();
        return voList;
    }

    private void insertSingleVo(OrderEntryVo vo) throws Exception {
        Order order = new Order();
        Company company = companyMapper.selectOne(new QueryWrapper<Company>().eq("company_name", vo.getCompanyName()));
        int companyId;
        if (null != company) {
            companyId = company.getId();
        } else {
            throw new Exception("COMPANY_MISMATCH");
        }
        order.setAmount(vo.getAmount());
        order.setOrderDate(vo.getOrderDate());
        order.setCompanyId(companyId);
        orderMapper.insert(order);
    }
}
