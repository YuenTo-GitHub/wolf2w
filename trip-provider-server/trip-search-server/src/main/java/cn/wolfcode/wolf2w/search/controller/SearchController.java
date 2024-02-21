package cn.wolfcode.wolf2w.search.controller;

import cn.wolfcode.wolf2w.article.dto.DestinationDto;
import cn.wolfcode.wolf2w.article.dto.StrategyDto;
import cn.wolfcode.wolf2w.article.dto.TravelDto;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.search.domain.DestinationEs;
import cn.wolfcode.wolf2w.search.domain.StrategyEs;
import cn.wolfcode.wolf2w.search.domain.TravelEs;
import cn.wolfcode.wolf2w.search.domain.UserInfoEs;
import cn.wolfcode.wolf2w.search.feign.IArticleFeignService;
import cn.wolfcode.wolf2w.search.feign.IUserInfoFeignService;
import cn.wolfcode.wolf2w.search.query.SearchQueryObject;
import cn.wolfcode.wolf2w.search.service.IElasticsearchService;
import cn.wolfcode.wolf2w.search.vo.SearchResultVO;
import cn.wolfcode.wolf2w.user.dto.UserInfoDTO;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/q")
public class SearchController {


    private final IElasticsearchService searchService;

    private final IUserInfoFeignService userInfoFeignService;

    private final IArticleFeignService articleFeignService;

    public SearchController(IElasticsearchService searchService, IUserInfoFeignService userInfoFeignService, IArticleFeignService articleFeignService) {
        this.searchService = searchService;
        this.userInfoFeignService = userInfoFeignService;
        this.articleFeignService = articleFeignService;
    }

    //约定：当前接口只操作一次--后续mysql数据发生变化，使用其他方案同步
    //@GetMapping("/dataInit")
    public JsonResult dataInit() {
        //查询mysql数据-以user
        /*JsonResult<List<UserInfoDTO>> jsonResult = userInfoFeignService.list();
        if (jsonResult != null && jsonResult.getCode() == 200) {
            List<UserInfoDTO> userInfos = jsonResult.getData();
            //添加es中
            for (UserInfoDTO userInfo : userInfos) {
                UserInfoEs es = new UserInfoEs();
                BeanUtils.copyProperties(userInfo, es);
                userInfoEsService.save(es);
            }
        }
        //查询mysql数据-Destination
        JsonResult<List<Destination>> dresult = destinationFeignService.list();
        if (dresult != null && dresult.getCode() == 200) {
            List<Destination> ds = dresult.getData();
            //添加es中
            for (Destination d : ds) {
                DestinationEs es = new DestinationEs();
                BeanUtils.copyProperties(d, es);
                destinationEsService.save(es);
            }
        }
        //查询mysql数据-以Travel
        JsonResult<List<Travel>> travelResult = travelFeignService.list();
        if (travelResult != null && travelResult.getCode() == 200) {
            List<Travel> ts = travelResult.getData();
            //添加es中
            for (Travel travel : ts) {
                TravelEs es = new TravelEs();
                BeanUtils.copyProperties(travel, es);
                travelEsService.save(es);
            }
        }
        //查询mysql数据-以Strategy
        JsonResult<List<Strategy>> strResult = strategyFeignService.list();
        if (jsonResult != null && jsonResult.getCode() == 200) {
            List<Strategy> sts = strResult.getData();
            //添加es中
            for (Strategy strategy : sts) {
                StrategyEs es = new StrategyEs();
                BeanUtils.copyProperties(strategy, es);
                strategyEsService.save(es);
            }
        }*/
        return JsonResult.success();
    }

    @GetMapping
    public JsonResult<?> search(SearchQueryObject qo) throws UnsupportedEncodingException {
        //http://localhost/views/search/searchDest.html?type=0&keyword=%E5%B9%BF%E5%B7%9E
        qo.setKeyword(URLDecoder.decode(qo.getKeyword(), StandardCharsets.UTF_8));
        return switch (qo.getType()) {
            case SearchQueryObject.TYPE_DEST -> this.searchDest(qo);
            case SearchQueryObject.TYPE_STRATEGY -> this.searchStrategy(qo);
            case SearchQueryObject.TYPE_TRAVEL -> this.searchTravel(qo);
            case SearchQueryObject.TYPE_USER -> this.searchUser(qo);
            default -> this.searchAll(qo);
        };
    }

    //精确查询目的地
    private JsonResult<?> searchDest(SearchQueryObject qo) {
        //1: 通过keyword 查询目的地对象
        String destName = qo.getKeyword();
        JsonResult<DestinationDto> destResult = articleFeignService.getDestByName(destName);
        DestinationDto dest = destResult.checkAndGet();
        //2：判断对象是否存在，如果存在， 查询该目的地下 游记， 攻略， 用户
        SearchResultVO result = new SearchResultVO();
        if (dest != null) {
            result.setTotal(1L);
            // 远程调用攻略接口，基于目的地名称查询攻略
            JsonResult<List<StrategyDto>> strategiesResult = articleFeignService.findStrategyByDestName(destName);
            List<StrategyDto> strategyDtos = strategiesResult.checkAndGet();
            result.setStrategies(strategyDtos);
            // 远程调用攻略接口，基于目的地名称查询游记
            JsonResult<List<TravelDto>> travelsResult = articleFeignService.findTravelByDestName(destName);
            List<TravelDto> travelDtos = travelsResult.checkAndGet();
            result.setTravels(travelDtos);
            // 远程调用攻略接口，基于目的地名称查询用户
            JsonResult<List<UserInfoDTO>> usersResult = userInfoFeignService.findUserByDestName(destName);
            List<UserInfoDTO> userDtos = usersResult.checkAndGet();
            result.setUsers(userDtos);
            result.setTotal(result.getTotal() + strategyDtos.size() + travelDtos.size() + userDtos.size());
        }
        JSONObject json = new JSONObject();
        json.put("qo", qo);
        json.put("result", result);
        json.put("dest", dest);
        return JsonResult.success(json);
    }

    //搜索攻略
    private JsonResult<?> searchStrategy(SearchQueryObject qo) {
        Page<StrategyDto> page = createStrategyPage(qo);
        JSONObject json = new JSONObject();
        json.put("page", page);
        json.put("qo", qo);
        return JsonResult.success(json);
    }

    //搜索游记
    private JsonResult<?> searchTravel(SearchQueryObject qo) {
        Page<TravelDto> page = createTravelPage(qo);
        JSONObject json = new JSONObject();
        json.put("page", page);
        json.put("qo", qo);
        return JsonResult.success(json);
    }

    //搜索用户
    private JsonResult<?> searchUser(SearchQueryObject qo) {
        Page<UserInfoDTO> page = createUserInfoPage(qo);
        JSONObject json = new JSONObject();
        json.put("page", page);
        json.put("qo", qo);
        return JsonResult.success(json);
    }

    //搜索全部
    private JsonResult<?> searchAll(SearchQueryObject qo) {
        SearchResultVO result = new SearchResultVO();
        Page<UserInfoDTO> userPage = createUserInfoPage(qo);
        result.setUsers(userPage.getContent());
        Page<TravelDto> travelPage = createTravelPage(qo);
        result.setTravels(travelPage.getContent());
        Page<StrategyDto> strategyPage = createStrategyPage(qo);
        result.setStrategies(strategyPage.getContent());
        Page<DestinationDto> destPage = createDestPage(qo);
        result.setDests(destPage.getContent());
        result.setTotal(userPage.getTotalElements() + travelPage.getTotalElements() + strategyPage.getTotalElements() + destPage.getTotalElements());
        JSONObject json = new JSONObject();
        json.put("result", result);
        json.put("qo", qo);
        return JsonResult.success(json);
    }

    private Page<StrategyDto> createStrategyPage(SearchQueryObject qo) {
        return searchService.searchWithHighlight(StrategyEs.class,
                StrategyDto.class,
                qo,
                (clazz, id) -> articleFeignService.getStrategyById(id).checkAndGet(),
                "title", "subTitle", "summary");
    }

    private Page<TravelDto> createTravelPage(SearchQueryObject qo) {
        return searchService.searchWithHighlight(TravelEs.class,
                TravelDto.class,
                qo,
                (clazz, id) -> articleFeignService.getTravelById(id).checkAndGet(),
                "title", "summary");
    }

    private Page<UserInfoDTO> createUserInfoPage(SearchQueryObject qo) {
        return searchService.searchWithHighlight(UserInfoEs.class,
                UserInfoDTO.class,
                qo,
                (clazz, id) -> userInfoFeignService.getUserById(id).checkAndGet(),
                "city", "info");
    }

    private Page<DestinationDto> createDestPage(SearchQueryObject qo) {
        return searchService.searchWithHighlight(DestinationEs.class,
                DestinationDto.class,
                qo,
                (clazz, id) -> articleFeignService.getDestById(id).checkAndGet(),
                "name", "info");
    }
}
