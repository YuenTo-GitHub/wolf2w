package cn.wolfcode.wolf2w.data.job;

import cn.wolfcode.wolf2w.article.domain.Strategy;
import cn.wolfcode.wolf2w.article.domain.StrategyRank;
import cn.wolfcode.wolf2w.data.mapper.StrategyMapper;
import cn.wolfcode.wolf2w.data.mapper.StrategyRankMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Component
public class StrategyRankStatisticJob {

    private final StrategyMapper strategyMapper;
    private final StrategyRankMapper strategyRankMapper;

    public StrategyRankStatisticJob(StrategyMapper strategyMapper, StrategyRankMapper strategyRankMapper) {
        this.strategyMapper = strategyMapper;
        this.strategyRankMapper = strategyRankMapper;
    }

    // 每小时统计一次攻略排行榜
    @Scheduled(cron = "0 0 0/1 * * ? ")
    @Transactional(rollbackFor = Exception.class)
    public void statisticRank() {
        log.info("定时任务：statisticRank，统计攻略排行榜");
        Date now = new Date();
        // 删除旧的排行
        strategyRankMapper.delete(new QueryWrapper<StrategyRank>().lt("UNIX_TIMESTAMP(statis_time)", (now.getTime() - 500) / 1000));
        // 统计国内攻略
        doStatistic(now, StrategyRank.TYPE_CHINA, () -> strategyMapper.selectStrategyRankByAbroad(Strategy.ABROAD_NO));
        // 统计国外攻略
        doStatistic(now, StrategyRank.TYPE_ABROAD, () -> strategyMapper.selectStrategyRankByAbroad(Strategy.ABROAD_YES));
        // 统计热门攻略
        doStatistic(now, StrategyRank.TYPE_HOT, strategyMapper::selectStrategyRankHotList);
    }

    public void doStatistic(Date now, Integer type, Supplier<List<StrategyRank>> rankSupplier) {
        List<StrategyRank> strategyRanks = rankSupplier.get();
        for (StrategyRank rank : strategyRanks) {
            rank.setType(type);
            rank.setStatisTime(now);
        }
        strategyRankMapper.insertBatch(strategyRanks);
    }
}
