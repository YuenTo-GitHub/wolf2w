package cn.wolfcode.wolf2w.data.mapper;


import cn.wolfcode.wolf2w.article.domain.Travel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* 游记持久层接口
*/
@Mapper
public interface TravelMapper extends BaseMapper<Travel>{
}