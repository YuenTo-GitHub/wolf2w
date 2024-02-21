package cn.wolfcode.wolf2w.article.service;

import cn.wolfcode.wolf2w.article.domain.StrategyRank;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IStrategyRankService extends IService<StrategyRank> {

    List<StrategyRank> getRanksByType(int type);
}
