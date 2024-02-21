package cn.wolfcode.wolf2w.search.strategy;

import cn.wolfcode.wolf2w.core.query.QueryObject;
import cn.wolfcode.wolf2w.core.utils.JsonResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

@Setter
@Getter
@AllArgsConstructor
public class EsDataInitStrategy {
    private Function<QueryObject, JsonResult<List<Object>>> function;

    private Class<?> clazz;
}
