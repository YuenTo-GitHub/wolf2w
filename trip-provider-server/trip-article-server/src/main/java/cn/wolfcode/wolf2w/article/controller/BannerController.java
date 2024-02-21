package cn.wolfcode.wolf2w.article.controller;

import cn.wolfcode.wolf2w.article.domain.Banner;
import cn.wolfcode.wolf2w.article.service.IBannerService;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/banners")
public class BannerController {
    private final IBannerService bannerService;

    public BannerController(IBannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping("travel")
    public JsonResult<List<Banner>> getTravelBanners() {
        return JsonResult.success(bannerService.findByType(Banner.TYPE_TRAVEL));
    }

    @GetMapping("strategy")
    public JsonResult<List<Banner>> getStrategyBanners() {
        return JsonResult.success(bannerService.findByType(Banner.TYPE_STRATEGY));
    }
}