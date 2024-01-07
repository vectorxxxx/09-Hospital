package xyz.funnyboy.mongodb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import xyz.funnyboy.mongodb.entity.User;
import xyz.funnyboy.mongodb.repository.UserRepository;

import java.util.List;

@SpringBootTest
class MongodbApplicationTests2
{
    @Autowired
    private UserRepository userRepository;

    /**
     * 创建用户
     */
    // @Test
    public void createUser() {
        final User user = new User();
        user.setAge(20);
        user.setName("张三");
        user.setEmail("3332200@qq.com");
        final User insert = userRepository.save(user);
        System.out.println(insert);
    }

    /**
     * 查找全部
     */
    @Test
    public void findAll() {
        final List<User> all = userRepository.findAll();
        System.out.println(all);
    }

    /**
     * 按 ID 获取
     */
    @Test
    public void getById() {
        final User user = userRepository
                .findById("659a73a663f53e3c3ed66920")
                .get();
        System.out.println(user);
    }

    /**
     * 条件查询
     */
    @Test
    public void findUserList() {
        final User user = new User();
        user.setAge(20);
        user.setName("张三");
        final Example<User> userExample = Example.of(user);
        final List<User> users = userRepository.findAll(userExample);
        users.forEach(System.out::println);
    }

    /**
     * 模糊查询
     */
    @Test
    public void findUsersLikeName() {
        // 模糊查询
        final ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        final User user = new User();
        user.setName("张三");
        final Example<User> userExample = Example.of(user, matcher);
        final List<User> users = userRepository.findAll(userExample);
        System.out.println(users);
    }

    /**
     * 分页查询
     */
    @Test
    public void findUserPage() {
        // 分页查询
        final Sort sort = Sort.by(Sort.Direction.DESC, "age");
        final Pageable pageable = PageRequest.of(0, 10, sort);

        // 模糊查询
        final ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        final User user = new User();
        user.setName("张三");
        final Example<User> userExample = Example.of(user, matcher);
        final Page<User> page = userRepository.findAll(userExample, pageable);
        System.out.println(page.getContent());
        System.out.println(page.getTotalPages());
    }

    @Test
    public void updateUser() {
        final User user = userRepository
                .findById("659a7ca9707f911b39eda116")
                .get();
        user.setName("张三_1");
        user.setAge(25);
        user.setEmail("883220990@qq.com");

        final User save = userRepository.save(user);
        System.out.println(save);
    }

    @Test
    public void deleteUser() {
        userRepository.deleteById("659a7ca9707f911b39eda116");
    }
}
