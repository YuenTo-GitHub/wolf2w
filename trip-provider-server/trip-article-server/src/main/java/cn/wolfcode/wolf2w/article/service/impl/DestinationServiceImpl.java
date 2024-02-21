package cn.wolfcode.wolf2w.article.service.impl;

import cn.wolfcode.wolf2w.article.domain.Destination;
import cn.wolfcode.wolf2w.article.domain.Region;
import cn.wolfcode.wolf2w.article.mapper.DestinationMapper;
import cn.wolfcode.wolf2w.article.query.DestinationQuery;
import cn.wolfcode.wolf2w.article.service.IDestinationService;
import cn.wolfcode.wolf2w.article.service.IRegionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class DestinationServiceImpl extends ServiceImpl<DestinationMapper, Destination> implements IDestinationService {


    private final IRegionService regionService;

    private final ThreadPoolExecutor businessThreadPoolExecutor;

    public DestinationServiceImpl(IRegionService regionService, ThreadPoolExecutor businessThreadPoolExecutor) {
        this.regionService = regionService;
        this.businessThreadPoolExecutor = businessThreadPoolExecutor;
    }

    @Override
    public List<Destination> queryByRId(Long rid) {
        //查询区域绑定目的地ids
        //select ref_ids from region where id = 5
        Region region = regionService.getById(rid);
        if (region == null) {
            return Collections.emptyList();
        }
        String refIds = region.getRefIds();  //id1,id2,...
        //解析目的地ids集合字符串， 查询目的地列表
        //select * from destination WHERE id in(52,53,54)
        List<Long> ids = region.parseRefIds();
        if (ids.size() == 0){
            return Collections.emptyList();
        }
        return super.listByIds(ids);
    }

    @Override
    public List<Destination> queryByRIdForWebSite(Long rid) {
        QueryWrapper<Destination> wrapper = new QueryWrapper<>();
        List<Destination> destinations;
        //第一步： 查询挂载目的地集合
        //国内
        if(rid == -1){
            //查询中国下所有的省份
            wrapper.eq("parent_id", 1);
            destinations = super.list(wrapper);
        }else{
            //非国内
            destinations = this.queryByRId(rid);
        }
        //第二步： 遍历查询目的地集合的子目的地（多线程并发实现）
        CountDownLatch latch = new CountDownLatch(destinations.size());
        for (Destination dest : destinations) {
            businessThreadPoolExecutor.execute(() ->{
                QueryWrapper<Destination> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("parent_id", dest.getId()).last(" limit 6");
                List<Destination> children = super.list(queryWrapper);
                dest.setChildren(children);
                // 倒计时-1
                latch.countDown();
            });
        }
        // 最终返回结果前，阻塞等待所有任务完成
        try{
            latch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return destinations;
    }

    @Override
    public Page<Destination> queryPage(DestinationQuery qo) {
        Page<Destination> page = new Page<>(qo.getCurrent(), qo.getSize());
        QueryWrapper<Destination> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.hasText(qo.getKeyword()), "name", qo.getKeyword());
        wrapper.eq(qo.getParentId() != null, "parent_id", qo.getParentId());
        wrapper.isNull(qo.getParentId() == null, "parent_id");
        return super.page(page, wrapper);
    }

    //根>>中国>>广东>>广州
    @Override
    public List<Destination> queryToast(Long destId) {
        List<Destination> list = new ArrayList<>();
        while (destId != null){
            Destination destination = super.getById(destId);
            if (destination == null) {
                break;
            }
            list.add(destination);
            destId = destination.getParentId();
        }
        Collections.reverse(list);  //ABC---CBA
        return list;
    }

    @Override
    public Destination queryByName(String name) {
        return super.getOne(new QueryWrapper<Destination>().eq("name", name));
    }

    @Override
    public void updateInfo(Long id, String info) {
        UpdateWrapper<Destination> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id);
        wrapper.set("info", info);
        super.update(wrapper);
    }
}