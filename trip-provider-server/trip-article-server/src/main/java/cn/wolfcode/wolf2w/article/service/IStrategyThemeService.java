package cn.wolfcode.wolf2w.article.service;

import cn.wolfcode.wolf2w.article.domain.StrategyTheme;
import cn.wolfcode.wolf2w.article.query.StrategyThemeQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IStrategyThemeService extends IService<StrategyTheme> {
    Page<StrategyTheme> queryPage(StrategyThemeQuery qo);
}
