package cn.wolfcode.wolf2w.user.mapper;

import cn.wolfcode.wolf2w.user.domain.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
