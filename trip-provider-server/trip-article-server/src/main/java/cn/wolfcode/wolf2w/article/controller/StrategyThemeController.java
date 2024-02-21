package cn.wolfcode.wolf2w.article.controller;

import cn.wolfcode.wolf2w.article.domain.StrategyTheme;
import cn.wolfcode.wolf2w.article.query.StrategyThemeQuery;
import cn.wolfcode.wolf2w.article.service.IStrategyThemeService;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/strategies/themes")
public class StrategyThemeController {

    private final IStrategyThemeService IStrategyThemeService;

    public StrategyThemeController(IStrategyThemeService IStrategyThemeService) {
        this.IStrategyThemeService = IStrategyThemeService;
    }

    @GetMapping("/query")
    public JsonResult<Page<StrategyTheme>> pageList(StrategyThemeQuery qo) {
        return JsonResult.success(IStrategyThemeService.queryPage(qo));
    }

    @GetMapping("/detail")
    public JsonResult<StrategyTheme> getById(Long id) {
        return JsonResult.success(IStrategyThemeService.getById(id));
    }

    @GetMapping("/list")
    public JsonResult<List<StrategyTheme>> listAll() {
        return JsonResult.success(IStrategyThemeService.list());
    }

    @PostMapping("/save")
    public JsonResult<?> save(StrategyTheme strategyTheme) {
        IStrategyThemeService.save(strategyTheme);
        return JsonResult.success();
    }

    @PostMapping("/update")
    public JsonResult<?> updateById(StrategyTheme strategyTheme) {
        IStrategyThemeService.updateById(strategyTheme);
        return JsonResult.success();
    }

    @PostMapping("/delete/{id}")
    public JsonResult<?> deleteById(@PathVariable Long id) {
        IStrategyThemeService.removeById(id);
        return JsonResult.success();
    }
}
