package com.forcast.personnelrequirements.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.forcast.personnelrequirements.entity.Order;
import com.forcast.personnelrequirements.vo.OrderVo.OrderEntryVo;
import com.forcast.personnelrequirements.vo.RespBean;
import org.springframework.web.multipart.MultipartFile;

public interface IOrderService extends IService<Order> {
    RespBean orderEntry(OrderEntryVo entryVo);

    RespBean orderEntryByExcel(MultipartFile file);
}
