package cn.wolfcode.wolf2w.article.service;

import cn.wolfcode.wolf2w.article.domain.Destination;
import cn.wolfcode.wolf2w.article.query.DestinationQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IDestinationService extends IService<Destination> {

    /**
     * 查询指定区域下目的地集合
     * @param rid
     * @return
     */
    List<Destination> queryByRId(Long rid);

    /**
     * 查询指定区域下目的地集合--国内--展示前端
     * @param rid
     * @return
     */
    List<Destination> queryByRIdForWebSite(Long rid);

    /**
     * 分页
     * @param qo
     * @return
     */
    Page<Destination> queryPage(DestinationQuery qo);

    /**
     * 更新info信息
     * @param id
     * @param info
     */
    void updateInfo(Long id, String info);

    /**
     * 吐司查询
     * @param destId
     * @return
     */
    List<Destination> queryToast(Long destId);

    /**
     * 通过name查询
     * @param name
     * @return
     */
    Destination queryByName(String name);
}
