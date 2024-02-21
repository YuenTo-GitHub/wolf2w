package cn.wolfcode.wolf2w.article.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StrategyCURDEevent extends ApplicationEvent {
    private Long id;  //攻略id
    public StrategyCURDEevent(Object source, Long id) {
        super(source);  //触发事件时传进来数据--约定：save/delete/update
        this.id = id;
    }
}
