package com.forcast.personnelrequirements.vo.OrderVo;

import com.forcast.personnelrequirements.validator.isLocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderEntryVo {
    @NotBlank
    private String companyName;

    @NotNull
    @isLocalDateTime
    private LocalDateTime orderDate;

    @NotNull
    private double amount;
}
