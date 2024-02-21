package cn.wolfcode.wolf2w.article.controller;

import cn.wolfcode.wolf2w.article.domain.Destination;
import cn.wolfcode.wolf2w.article.query.DestinationQuery;
import cn.wolfcode.wolf2w.article.service.IDestinationService;
import cn.wolfcode.wolf2w.core.query.QueryObject;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.user.domain.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("destinations")
public class DestinationController {
    
    private final IDestinationService destinationService;

    public DestinationController(IDestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping("/detail")
    public JsonResult<Destination> detail(Long id){
        return JsonResult.success(destinationService.getById(id));
    }

    @GetMapping("/query")
    public JsonResult<Page<Destination>> query(DestinationQuery qo){
        Page<Destination> page = destinationService.queryPage(qo);
        return JsonResult.success(page);
    }

    @GetMapping("/list")
    public JsonResult<List<Destination>> list(){
        return JsonResult.success(destinationService.list());
    }

    @PostMapping("/search")
    public JsonResult<List<Destination>> searchList(@RequestBody QueryObject qo){
        QueryWrapper<Destination> wrapper = new QueryWrapper<Destination>().last("limit "+ qo.getOffset() + "," + qo.getSize());
        return JsonResult.success(destinationService.list(wrapper));
    }

    @GetMapping("/getByName")
    public JsonResult<Destination> findByDestName(@RequestParam String name){
        return JsonResult.success(destinationService.getOne(new QueryWrapper<Destination>().eq("name", name)));
    }

    @PostMapping("/updateInfo")
    public JsonResult<?> updateInfo(Long id, String info){
        destinationService.updateInfo(id, info);
        return JsonResult.success();
    }

    @PostMapping("/delete/{id}")
    public JsonResult<?> delete(@PathVariable Long id){
        destinationService.removeById(id);
        return JsonResult.success();
    }

    @GetMapping("/hot")
    public JsonResult<?> queryHot(Long rid){
        return JsonResult.success(destinationService.queryByRIdForWebSite(rid));
    }

    @GetMapping("/toasts")
    public JsonResult<List<Destination>> toasts(Long destId){
        return JsonResult.success(destinationService.queryToast(destId));
    }
}
