package cn.wolfcode.wolf2w.article.feign;

import cn.wolfcode.wolf2w.core.utils.JsonResult;
import cn.wolfcode.wolf2w.user.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "trip-users-server")
public interface IUserInfoFeignService {

    @GetMapping(path = "/users/getUserInfoDTO", headers = "Requester=feign")
    JsonResult<UserInfoDTO> getUserInfoDTO(@RequestParam("id") Long id);
}