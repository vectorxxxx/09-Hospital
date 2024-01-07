package xyz.funnyboy.mongodb;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import xyz.funnyboy.mongodb.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SpringBootTest
class MongodbApplicationTests
{
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 创建用户
     */
    @Test
    public void createUser() {
        final User user = new User();
        user.setName("test");
        user.setAge(20);
        user.setEmail("123@qq.com");
        final User insert = mongoTemplate.insert(user);
        System.out.println(insert);
    }

    /**
     * 查找全部
     */
    @Test
    public void findAll() {
        final List<User> all = mongoTemplate.findAll(User.class);
        System.out.println(all);
    }

    /**
     * 按 ID 获取
     */
    @Test
    public void getById() {
        final User user = mongoTemplate.findById("659a73a663f53e3c3ed66920", User.class);
        System.out.println(user);
    }

    /**
     * 条件查询
     */
    @Test
    public void findUserList() {
        final Query query = new Query(Criteria
                .where("name")
                .is("funnyboy")
                .and("age")
                .is(20));
        final List<User> users = mongoTemplate.find(query, User.class);
        users.forEach(System.out::println);
    }

    /**
     * 模糊查询
     */
    @Test
    public void findUsersLikeName() {
        String name = "est";
        String regex = String.format("%s%s%s", "^.*", name, ".*$");
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Query query = new Query(Criteria
                .where("name")
                .regex(pattern));
        final List<User> users = mongoTemplate.find(query, User.class);
        System.out.println(users);
    }

    /**
     * 分页查询
     */
    @Test
    public void findUserPage() {
        final Query query = new Query();

        // 设置分页参数
        final int pageNo = 1;
        final int pageSize = 10;
        final long totalCount = mongoTemplate.count(query, User.class);

        final List<User> users = mongoTemplate.find(query
                .skip((pageNo - 1) * pageSize)
                .limit(pageSize), User.class);

        // 组装map
        final Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("totalCount", totalCount);
        System.out.println(result);
    }

    @Test
    public void updateUser() {
        final User user = mongoTemplate.findById("659a75d1b53d875dff404519", User.class);
        user.setName("test_1");
        user.setAge(25);
        user.setEmail("493220990@qq.com");

        final Query query = new Query(Criteria
                .where("_id")
                .is(user.getId()));
        final Update update = new Update();
        update.set("name", user.getName());
        update.set("age", user.getAge());
        update.set("email", user.getEmail());
        final UpdateResult result = mongoTemplate.upsert(query, update, User.class);
        System.out.println(result.getModifiedCount());
    }

    @Test
    public void deleteUser() {
        final Query query = new Query(Criteria
                .where("_id")
                .is("659a75d1b53d875dff404519"));
        final DeleteResult remove = mongoTemplate.remove(query, User.class);
        System.out.println(remove.getDeletedCount());
    }
}
