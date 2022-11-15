package com.leih.url.data.manager;

import com.leih.url.data.entity.VisitStats;
import com.leih.url.data.repository.VisitStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VisitStatsManagerImpl implements VisitStatsManager{
    @Autowired
    VisitStatsRepository visitStatsRepository;
    @Override
    public int countAll(Long accountNo, String code) {
        int result = visitStatsRepository.countAllByAccountNoAndCode(accountNo, code);
        return result;
    }

    @Override
    public List<VisitStats> paginateVisitRecord(String code, Long accountNo, int from, int size) {
        List<VisitStats> visitStatsList = visitStatsRepository.paginateVisitRecord(accountNo, code, from, size);
        return visitStatsList;
    }
}
