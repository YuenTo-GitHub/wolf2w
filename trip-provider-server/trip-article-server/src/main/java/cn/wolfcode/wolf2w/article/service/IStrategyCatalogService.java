package cn.wolfcode.wolf2w.article.service;

import cn.wolfcode.wolf2w.article.domain.StrategyCatalog;
import cn.wolfcode.wolf2w.article.query.StrategyCatalogQuery;
import cn.wolfcode.wolf2w.article.vo.StrategyCatalogGroup;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IStrategyCatalogService extends IService<StrategyCatalog> {

    List<StrategyCatalogGroup> findGroupList();

    Page<StrategyCatalog> queryPage(StrategyCatalogQuery qo);
}
