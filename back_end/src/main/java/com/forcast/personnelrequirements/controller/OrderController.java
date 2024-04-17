package com.forcast.personnelrequirements.controller;

import com.forcast.personnelrequirements.service.IOrderService;
import com.forcast.personnelrequirements.vo.OrderVo.OrderEntryVo;
import com.forcast.personnelrequirements.vo.RespBean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/order")
@Validated
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/entry")
    public RespBean entry(@RequestBody @Valid OrderEntryVo vo) {
        return orderService.orderEntry(vo);
    }

    @PostMapping("/entryByExcel")
    public RespBean entryByExcel(@RequestBody @Valid MultipartFile file) {
        return orderService.orderEntryByExcel(file);
    }
}
