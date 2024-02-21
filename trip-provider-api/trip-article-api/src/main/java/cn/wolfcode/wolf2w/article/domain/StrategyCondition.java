package cn.wolfcode.wolf2w.article.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 攻略条件统计表
 */
@Setter
@Getter
@TableName("strategy_condition")
public class StrategyCondition implements Serializable {

    public static final int TYPE_ABROAD = 1;  //国外
    public static final int TYPE_CHINA = 2;   //国内
    public static final int TYPE_THEME = 3;     //主题

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Integer count; //个数
    private Long refid; //关联id
    private int type; //条件类型
    private Date statisTime; //归档统计时间
}
