package cn.wolfcode.wolf2w.article.controller;

import cn.wolfcode.wolf2w.article.domain.Travel;
import cn.wolfcode.wolf2w.article.query.TravelQuery;
import cn.wolfcode.wolf2w.article.service.ITravelService;
import cn.wolfcode.wolf2w.core.query.QueryObject;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.user.domain.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("travels")
public class TravelController {

    private final ITravelService travelService;

    public TravelController(ITravelService travelService) {
        this.travelService = travelService;
    }

    @PostMapping("/save")
    public JsonResult<?> save(Travel travel) {
        travelService.save(travel);
        return JsonResult.success();
    }

    @PostMapping("/search")
    public JsonResult<List<Travel>> searchList(@RequestBody QueryObject qo) {
        QueryWrapper<Travel> wrapper = new QueryWrapper<Travel>().last("limit " + qo.getOffset() + "," + qo.getSize());
        return JsonResult.success(travelService.list(wrapper));
    }

    @PostMapping("/update")
    public JsonResult<?> update(Travel travel) {
        travelService.updateById(travel);
        return JsonResult.success();
    }

    @PostMapping("/delete/{id}")
    public JsonResult<?> delete(@PathVariable Long id) {
        travelService.removeById(id);
        return JsonResult.success();
    }

    @GetMapping("/detail")
    public JsonResult<?> detail(Long id, HttpServletRequest request) {
        Travel travel = travelService.getById(id);
        // 若不是feign请求，则阅读数+1
        if (request.getHeader("Requester") == null)
            travelService.viewnumIncr(id);
        return JsonResult.success(travel);
    }

    @GetMapping("/query")
    public JsonResult<Page<Travel>> query(TravelQuery qo) {
        Page<Travel> page = travelService.queryPage(qo);
        return JsonResult.success(page);
    }

    @GetMapping("/findByDestName")
    public JsonResult<List<Travel>> findByDestName(@RequestParam String destName){
        return JsonResult.success(travelService.queryDestinationName(destName));
    }

    @GetMapping("/viewnumTop3")
    public JsonResult<List<Travel>> travelViewnumTop3(Long destId) {
        List<Travel> list = travelService.queryViewnumTop3(destId);
        return JsonResult.success(list);
    }

    @GetMapping("/stat/data")
    public JsonResult<Map<String, Integer>> statistic(Long id) {
        Map<String, Integer> result = travelService.getStatisticData(id.toString());
        return JsonResult.success(result);
    }
}