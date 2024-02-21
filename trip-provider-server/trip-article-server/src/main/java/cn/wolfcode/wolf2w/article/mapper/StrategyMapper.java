package cn.wolfcode.wolf2w.article.mapper;

import cn.wolfcode.wolf2w.article.domain.Strategy;
import cn.wolfcode.wolf2w.article.domain.StrategyCatalog;
import cn.wolfcode.wolf2w.article.vo.StrategyCondition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
/**
* 攻略文章持久层接口
*/
@Mapper
public interface StrategyMapper extends BaseMapper<Strategy>{
    List<StrategyCatalog> selectGroupsByDestId(Long destId);

    List<StrategyCondition> selectThemeCondition();
    List<StrategyCondition> selectDestCondition(int abroad);

    boolean numIncr(Long sid, String column, Integer increment);
}