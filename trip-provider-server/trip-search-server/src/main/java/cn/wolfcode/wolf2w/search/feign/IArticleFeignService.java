package cn.wolfcode.wolf2w.search.feign;

import cn.wolfcode.wolf2w.article.dto.DestinationDto;
import cn.wolfcode.wolf2w.article.dto.StrategyDto;
import cn.wolfcode.wolf2w.article.dto.TravelDto;
import cn.wolfcode.wolf2w.core.query.QueryObject;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("trip-article-server")
public interface IArticleFeignService {

    @PostMapping(value = "/travels/search", headers = "Requester=feign")
    JsonResult<List<Object>> travelSearchList(@RequestBody QueryObject qo);

    @GetMapping(value = "/travels/findByDestName", headers = "Requester=feign")
    JsonResult<List<TravelDto>> findTravelByDestName(@RequestParam String destName);

    @PostMapping(value = "/strategies/search", headers = "Requester=feign")
    JsonResult<List<Object>> strategySearchList(@RequestBody QueryObject qo);

    @GetMapping(value = "/strategies/findByDestName", headers = "Requester=feign")
    JsonResult<List<StrategyDto>> findStrategyByDestName(@RequestParam String destName);

    @PostMapping(value = "/destinations/search", headers = "Requester=feign")
    JsonResult<List<Object>> destinationSearchList(@RequestBody QueryObject qo);

    @GetMapping(value = "/destinations/getByName", headers = "Requester=feign")
    JsonResult<DestinationDto> getDestByName(@RequestParam String name);

    @GetMapping(value = "/strategies/detail", headers = "Requester=feign")
    JsonResult<StrategyDto> getStrategyById(@RequestParam String id);

    @GetMapping(value = "/travels/detail", headers = "Requester=feign")
    JsonResult<TravelDto> getTravelById(@RequestParam String id);

    @GetMapping(value = "/destinations/detail", headers = "Requester=feign")
    JsonResult<DestinationDto> getDestById(@RequestParam String id);
}