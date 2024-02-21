package cn.wolfcode.wolf2w.user.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class UserArticleInteractionVO {
    private List<Long> likesList = new ArrayList<>();

    private List<Long> favoriteList = new ArrayList<>();

    private List<Long> viewedList = new ArrayList<>();
}
