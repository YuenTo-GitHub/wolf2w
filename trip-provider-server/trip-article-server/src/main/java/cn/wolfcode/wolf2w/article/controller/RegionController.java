package cn.wolfcode.wolf2w.article.controller;

import cn.wolfcode.wolf2w.article.domain.Destination;
import cn.wolfcode.wolf2w.article.domain.Region;
import cn.wolfcode.wolf2w.article.query.RegionQuery;
import cn.wolfcode.wolf2w.article.service.IDestinationService;
import cn.wolfcode.wolf2w.article.service.IRegionService;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("regions")
public class RegionController {

    private final IRegionService regionService;
    private final IDestinationService destinationService;

    public RegionController(IRegionService regionService, IDestinationService destinationService) {
        this.regionService = regionService;
        this.destinationService = destinationService;
    }

    @GetMapping("/detail")
    public JsonResult<?> detail(Long id){
        return JsonResult.success(regionService.getById(id));
    }

    @PostMapping("/save")
    public JsonResult<?> save(Region region){
        regionService.save(region);
        return JsonResult.success();
    }

    @PostMapping("/update")
    public JsonResult<?> update(Region region){
        regionService.updateById(region);
        return JsonResult.success();
    }

    @PostMapping("/delete/{id}")
    public JsonResult<?> delete(@PathVariable Long id){
        regionService.removeById(id);
        return JsonResult.success();
    }


    @GetMapping("/{id}/destination")
    public JsonResult<List<Destination>> queryByRId( @PathVariable Long id){
        List<Destination> list = destinationService.queryByRId(id);
        return JsonResult.success(list);
    }

    @GetMapping("/query")
    public JsonResult<Page<Region>> query(RegionQuery qo){
        Page<Region> page = regionService.queryPage(qo);
        return JsonResult.success(page);
    }

    @GetMapping("/hot")
    public JsonResult<List<Region>> queryHot(){
        List<Region> list = regionService.queryHot();
        return JsonResult.success(list);
    }
}
