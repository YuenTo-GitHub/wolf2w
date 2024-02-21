package cn.wolfcode.wolf2w.article.service.impl;

import cn.wolfcode.wolf2w.article.domain.StrategyRank;
import cn.wolfcode.wolf2w.article.mapper.StrategyRankMapper;
import cn.wolfcode.wolf2w.article.service.IStrategyRankService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StrategyRankServiceImpl extends ServiceImpl<StrategyRankMapper, StrategyRank> implements IStrategyRankService {

    @Override
    public List<StrategyRank> getRanksByType(int type) {
        QueryWrapper<StrategyRank> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type).last("limit 10");
        return list(wrapper);
    }
}