package cn.wolfcode.wolf2w.article.controller;

import cn.wolfcode.wolf2w.article.domain.Strategy;
import cn.wolfcode.wolf2w.article.domain.StrategyCatalog;
import cn.wolfcode.wolf2w.article.domain.StrategyRank;
import cn.wolfcode.wolf2w.article.query.StrategyQuery;
import cn.wolfcode.wolf2w.article.service.IStrategyRankService;
import cn.wolfcode.wolf2w.article.service.IStrategyService;
import cn.wolfcode.wolf2w.article.utils.UploadUtils;
import cn.wolfcode.wolf2w.article.vo.StrategyCondition;
import cn.wolfcode.wolf2w.core.query.QueryObject;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/strategies")
public class StrategyController {

    private final IStrategyService strategyService;
    private final IStrategyRankService strategyRankService;

    public StrategyController(IStrategyService strategyService, IStrategyRankService strategyRankService) {
        this.strategyService = strategyService;
        this.strategyRankService = strategyRankService;
    }

    @GetMapping("/findByDestName")
    public JsonResult<List<Strategy>> findByDestName(@RequestParam String destName) {
        return JsonResult.success(strategyService.list(new QueryWrapper<Strategy>().eq("dest_name", destName)));
    }

    @GetMapping("/query")
    public JsonResult<Page<Strategy>> pageList(StrategyQuery qo) {
        return JsonResult.success(strategyService.queryPage(qo));
    }

    @PostMapping("/search")
    public JsonResult<List<Strategy>> searchList(@RequestBody QueryObject qo) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<Strategy>().last("limit " + qo.getOffset() + "," + qo.getSize());
        return JsonResult.success(strategyService.list(wrapper));
    }

    @GetMapping("/groups")
    public JsonResult<List<StrategyCatalog>> groupByCatalog(Long destId) {
        return JsonResult.success(strategyService.findGroupsByDestId(destId));
    }

    @GetMapping("/detail")
    public JsonResult<Strategy> detail(Long id, HttpServletRequest request) {
        Strategy strategy = strategyService.getById(id);
        // 若不是feign请求，则阅读数+1
        if (request.getHeader("Requester") == null)
            // 阅读数+1，数量累计在redis中，定时同步到MySQL中
            strategyService.viewnumIncr(id);
        return JsonResult.success(strategy);
    }

    @PostMapping("/save")
    public JsonResult<?> save(Strategy strategy) {
        strategyService.save(strategy);
        return JsonResult.success();
    }

    @PostMapping("/update")
    public JsonResult<?> update(Strategy strategy) {
        strategyService.updateById(strategy);
        return JsonResult.success();
    }

    @PostMapping("/delete/{id}")
    public JsonResult<?> deleteById(@PathVariable Long id) {
        strategyService.removeById(id);
        return JsonResult.success();
    }

    @GetMapping("/content")
    public JsonResult<?> getContentById(Long id) {
        return JsonResult.success(strategyService.getContentById(id));
    }

    @GetMapping("/conditions")
    public JsonResult<Map<String, List<StrategyCondition>>> getConditions() {
        Map<String, List<StrategyCondition>> map = new HashMap<>();
        List<StrategyCondition> chinaCondition = strategyService.findDestCondition(Strategy.ABROAD_NO);
        map.put("chinaCondition", chinaCondition);
        List<StrategyCondition> abroadCondition = strategyService.findDestCondition(Strategy.ABROAD_YES);
        map.put("abroadCondition", abroadCondition);
        List<StrategyCondition> themeCondition = strategyService.findThemeCondition();
        map.put("themeCondition", themeCondition);
        return JsonResult.success(map);
    }

    @GetMapping("/viewnumTop3")
    public JsonResult<List<Strategy>> strategyViewnumTop3(Long destId) {
        List<Strategy> list = strategyService.queryViewnumTop3(destId);
        return JsonResult.success(list);
    }

    @PostMapping("/uploadImg")
    public JSONObject uploadImg(@RequestParam("upload") MultipartFile file) {
        JSONObject result = new JSONObject();
        if (file == null) {
            JSONObject error = new JSONObject();
            error.put("message", "请选择要上传的文件!");
            result.put("uploaded", 0);
            result.put("error", error);
            return result;
        }
        // 调用阿里云 OSS 工具类进行文件上传
        // 解决文件名重复问题
        // 1. 直接使用 UUID 替换原始文件名
        // 2. 在原始文件名后面拼接时间戳
        String fileName =
                file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".")) + "_" + System.currentTimeMillis();
        // 返回阿里云可访问的 url 地址
        String url = UploadUtils.upload("images", fileName, file);
        result.put("uploaded", 1);
        result.put("fileName", file.getOriginalFilename());
        result.put("url", url);
        return result;
    }

    @GetMapping("/ranks")
    public JsonResult<JSONObject> findRanks() {
        List<StrategyRank> abroadRank = strategyRankService.getRanksByType(StrategyRank.TYPE_ABROAD);
        List<StrategyRank> chinaRank = strategyRankService.getRanksByType(StrategyRank.TYPE_CHINA);
        List<StrategyRank> hotRank = strategyRankService.getRanksByType(StrategyRank.TYPE_HOT);
        JSONObject result = new JSONObject();
        result.put("abroadRank", abroadRank);
        result.put("chinaRank", chinaRank);
        result.put("hotRank", hotRank);
        return JsonResult.success(result);
    }

    @GetMapping("/stat/data")
    public JsonResult<Map<String, Integer>> statistic(Long id) {
        Map<String, Integer> result = strategyService.getStatisticData(id.toString());
        return JsonResult.success(result);
    }
}