package cn.wolfcode.wolf2w.search.repository;

import cn.wolfcode.wolf2w.search.domain.StrategyEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StrategyEsRepository extends ElasticsearchRepository<StrategyEs, String>{
}
