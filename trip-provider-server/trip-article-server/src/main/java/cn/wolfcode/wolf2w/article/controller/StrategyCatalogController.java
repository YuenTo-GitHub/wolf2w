package cn.wolfcode.wolf2w.article.controller;

import cn.wolfcode.wolf2w.article.domain.StrategyCatalog;
import cn.wolfcode.wolf2w.article.query.StrategyCatalogQuery;
import cn.wolfcode.wolf2w.article.service.IStrategyCatalogService;
import cn.wolfcode.wolf2w.article.vo.StrategyCatalogGroup;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/strategies/catalogs")
public class StrategyCatalogController {

    private final IStrategyCatalogService strategyCatalogService;

    public StrategyCatalogController(IStrategyCatalogService strategyCatalogService) {
        this.strategyCatalogService = strategyCatalogService;
    }

    @GetMapping("/query")
    public JsonResult<Page<StrategyCatalog>> pageList(StrategyCatalogQuery qo) {
        return JsonResult.success(strategyCatalogService.queryPage(qo));
    }

    @GetMapping("/groups")
    public JsonResult<List<StrategyCatalogGroup>> groupList() {
        return JsonResult.success(strategyCatalogService.findGroupList());
    }

    @GetMapping("/detail")
    public JsonResult<StrategyCatalog> getById(Long id) {
        return JsonResult.success(strategyCatalogService.getById(id));
    }

    @PostMapping("/save")
    public JsonResult<?> save(StrategyCatalog strategyCatalog) {
        strategyCatalogService.save(strategyCatalog);
        return JsonResult.success();
    }

    @PostMapping("/update")
    public JsonResult<?> updateById(StrategyCatalog strategyCatalog) {
        strategyCatalogService.updateById(strategyCatalog);
        return JsonResult.success();
    }

    @PostMapping("/delete/{id}")
    public JsonResult<?> deleteById(@PathVariable Long id) {
        strategyCatalogService.removeById(id);
        return JsonResult.success();
    }
}
