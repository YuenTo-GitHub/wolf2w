package cn.wolfcode.wolf2w.article.service.impl;

import cn.wolfcode.wolf2w.article.domain.StrategyTheme;
import cn.wolfcode.wolf2w.article.mapper.StrategyThemeMapper;
import cn.wolfcode.wolf2w.article.query.StrategyThemeQuery;
import cn.wolfcode.wolf2w.article.service.IStrategyThemeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StrategyThemeServiceImpl extends ServiceImpl<StrategyThemeMapper, StrategyTheme> implements IStrategyThemeService {
    @Override
    public Page<StrategyTheme> queryPage(StrategyThemeQuery qo) {
        Page<StrategyTheme> page = new Page<>(qo.getCurrent(), qo.getSize());
        QueryWrapper<StrategyTheme> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.hasText(qo.getKeyword()), "name", qo.getKeyword());
        return super.page(page, wrapper);
    }
}