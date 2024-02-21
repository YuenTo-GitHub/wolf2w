package cn.wolfcode.wolf2w.search.vo;


import cn.wolfcode.wolf2w.article.dto.DestinationDto;
import cn.wolfcode.wolf2w.article.dto.StrategyDto;
import cn.wolfcode.wolf2w.article.dto.TravelDto;
import cn.wolfcode.wolf2w.user.dto.UserInfoDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class SearchResultVO implements Serializable {

    private Long total = 0L;

    private List<StrategyDto> strategies = new ArrayList<>();
    private List<TravelDto> travels = new ArrayList<>();
    private List<UserInfoDTO> users = new ArrayList<>();
    private List<DestinationDto> dests = new ArrayList<>();
}