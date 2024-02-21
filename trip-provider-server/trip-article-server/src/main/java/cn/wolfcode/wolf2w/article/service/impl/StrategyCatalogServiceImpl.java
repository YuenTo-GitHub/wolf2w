package cn.wolfcode.wolf2w.article.service.impl;

import cn.wolfcode.wolf2w.article.domain.StrategyCatalog;
import cn.wolfcode.wolf2w.article.mapper.StrategyCatalogMapper;
import cn.wolfcode.wolf2w.article.query.StrategyCatalogQuery;
import cn.wolfcode.wolf2w.article.service.IStrategyCatalogService;
import cn.wolfcode.wolf2w.article.vo.StrategyCatalogGroup;
import cn.wolfcode.wolf2w.core.query.QueryObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StrategyCatalogServiceImpl extends ServiceImpl<StrategyCatalogMapper, StrategyCatalog> implements IStrategyCatalogService {

    @Override
    public List<StrategyCatalogGroup> findGroupList() {
        return getBaseMapper().selectGroupList();
    }

    @Override
    public Page<StrategyCatalog> queryPage(StrategyCatalogQuery qo) {
        Page<StrategyCatalog> page = new Page<>(qo.getCurrent(), qo.getSize());
        QueryWrapper<StrategyCatalog> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.hasText(qo.getKeyword()), "name", qo.getKeyword());
        return super.page(page, wrapper);
    }
}