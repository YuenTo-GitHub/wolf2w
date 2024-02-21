package cn.wolfcode.wolf2w.article.service;

import cn.wolfcode.wolf2w.article.domain.Strategy;
import cn.wolfcode.wolf2w.article.domain.StrategyCatalog;
import cn.wolfcode.wolf2w.article.domain.StrategyContent;
import cn.wolfcode.wolf2w.article.query.StrategyQuery;
import cn.wolfcode.wolf2w.article.vo.StrategyCondition;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IStrategyService extends IService<Strategy> {

    List<StrategyCatalog> findGroupsByDestId(Long destId);

    Page<Strategy> queryPage(StrategyQuery qo);

    StrategyContent getContentById(Long id);

    List<Strategy> queryViewnumTop3(Long destId);

    List<StrategyCondition> findDestCondition(int abroad);

    List<StrategyCondition> findThemeCondition();

    void viewnumIncr(Long id);

    Map<String, Integer> getStatisticData(String string);
}