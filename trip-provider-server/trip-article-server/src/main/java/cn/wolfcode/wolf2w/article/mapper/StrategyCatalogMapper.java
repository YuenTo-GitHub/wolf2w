package cn.wolfcode.wolf2w.article.mapper;


import cn.wolfcode.wolf2w.article.domain.StrategyCatalog;
import cn.wolfcode.wolf2w.article.vo.StrategyCatalogGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* 攻略分类持久层接口
*/
@Mapper
public interface StrategyCatalogMapper extends BaseMapper<StrategyCatalog>{
    List<StrategyCatalogGroup> selectGroupList();
}
