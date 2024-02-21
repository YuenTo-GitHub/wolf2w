package cn.wolfcode.wolf2w.article.service;

import cn.wolfcode.wolf2w.article.domain.Travel;
import cn.wolfcode.wolf2w.article.query.TravelQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;


public interface ITravelService extends IService<Travel> {
    Page<Travel> queryPage(TravelQuery qo);

    List<Travel> queryViewnumTop3(Long destId);

    void viewnumIncr(Long tid);

    Map<String, Integer> getStatisticData(String id);

    List<Travel> queryDestinationName(String destName);
}