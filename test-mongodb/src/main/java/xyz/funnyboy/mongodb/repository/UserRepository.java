package xyz.funnyboy.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import xyz.funnyboy.mongodb.entity.User;

/**
 * UserRepository
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-07 18:17:20
 */
@Repository
public interface UserRepository extends MongoRepository<User, String>
{
}
