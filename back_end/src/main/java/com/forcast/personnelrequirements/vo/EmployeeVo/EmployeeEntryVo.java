package com.forcast.personnelrequirements.vo.EmployeeVo;

import com.forcast.personnelrequirements.validator.isLocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmployeeEntryVo {
    @NotBlank
    private String companyName;

    @NotBlank
    private String name;

    @NotBlank
    private String position;

    @NotNull
    @isLocalDateTime
    private LocalDateTime hiredDate;

    private LocalDateTime dismissalDate;
}
