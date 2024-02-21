package cn.wolfcode.wolf2w.search.feign;

import cn.wolfcode.wolf2w.core.query.QueryObject;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.user.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "trip-users-server")
public interface IUserInfoFeignService {

    // 批量获取数据
    @PostMapping(value = "/users/search", headers = "Requester=feign")
    JsonResult<List<Object>> userSearchList(@RequestBody QueryObject qo);

    @GetMapping(value = "/users/findByDestName", headers = "Requester=feign")
    JsonResult<List<UserInfoDTO>> findUserByDestName(@RequestParam String destName);

    @GetMapping(value = "/users/getUserInfoDTO", headers = "Requester=feign")
    JsonResult<UserInfoDTO> getUserById(@RequestParam String id);
}
