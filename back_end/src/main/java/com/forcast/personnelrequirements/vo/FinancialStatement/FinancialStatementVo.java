package com.forcast.personnelrequirements.vo.FinancialStatement;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinancialStatementVo {
    @NotNull
    private String companyName;

    @NotNull

    private int year;

    @NotNull
    private int month;

    @NotNull
    private double revenue;

    @NotNull
    private double profit;
}
