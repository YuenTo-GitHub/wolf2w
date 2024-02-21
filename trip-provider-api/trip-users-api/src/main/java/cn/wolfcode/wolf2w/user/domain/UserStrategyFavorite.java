package cn.wolfcode.wolf2w.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document("user_strategy_favorite")
public class UserStrategyFavorite {
    @Id
    private String userId;

    // 收藏的攻略的id列表
    private List<Long> favoriteList = new ArrayList<>();
}