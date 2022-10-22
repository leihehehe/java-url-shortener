package com.leih.url.account.vo;

import com.leih.url.account.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPlanVo {
    /**
     * The times left on the day
     */
    private Integer dayTotalLeftTimes;
    private Plan currentPlan;
    private List<Long> notUpdatedPlanIds;

}
