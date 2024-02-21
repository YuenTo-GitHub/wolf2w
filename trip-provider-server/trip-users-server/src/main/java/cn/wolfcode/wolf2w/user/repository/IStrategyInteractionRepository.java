package cn.wolfcode.wolf2w.user.repository;

import cn.wolfcode.wolf2w.user.domain.UserStrategyFavorite;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IStrategyInteractionRepository extends MongoRepository<UserStrategyFavorite, String> {
}
