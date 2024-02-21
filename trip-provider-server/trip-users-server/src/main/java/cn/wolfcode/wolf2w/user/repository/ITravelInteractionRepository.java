package cn.wolfcode.wolf2w.user.repository;

import cn.wolfcode.wolf2w.user.domain.UserTravelFavorite;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ITravelInteractionRepository extends MongoRepository<UserTravelFavorite, String> {
}
