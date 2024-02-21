package cn.wolfcode.wolf2w.article.service.impl;

import cn.wolfcode.wolf2w.article.domain.Region;
import cn.wolfcode.wolf2w.article.mapper.RegionMapper;
import cn.wolfcode.wolf2w.article.query.RegionQuery;
import cn.wolfcode.wolf2w.article.service.IRegionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements IRegionService {
    @Override
    public Page<Region> queryPage(RegionQuery qo) {
        Page<Region> page = new Page<>(qo.getCurrent(), qo.getSize());
        QueryWrapper<Region> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.hasText(qo.getKeyword()), "name", qo.getKeyword());
        return super.page(page, wrapper);
    }

    @Override
    public List<Region> queryHot() {
        QueryWrapper<Region> wrapper = new QueryWrapper<>();
        wrapper.eq("ishot", Region.STATE_HOT);
        wrapper.orderByAsc("seq");
        return super.list(wrapper);
    }
}
