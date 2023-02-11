package com.example.demo.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.config.RedisConf;
import com.example.demo.model.Student;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@DataRedisTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("redis")
@Import(RedisConf.class)
class StudentRepositoryTest {

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {

        static GenericContainer<?> redis;

        static {
            redis = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine"))
                .withExposedPorts(6379);
            redis.start();
            System.setProperty("spring.redis.host", redis.getHost());
            System.setProperty("spring.redis.port", redis.getMappedPort(6379).toString());
        }
    }

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void test() {
        String id = UUID.randomUUID().toString();
        Student anton = Student.builder()
            .id(id)
            .name("Anton")
            .gender(Student.Gender.MALE)
            .build();

        studentRepository.save(anton);

        Optional<Student> optionalStudent = studentRepository.findById(id);

        Object entries = redisTemplate.opsForHash().entries("student:" + id);

        redisTemplate.opsForSet().add("set:key1", "value1");

        Set members = redisTemplate.opsForSet().members("set:key1");

        redisTemplate.execute((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;

            ScanOptions options = ScanOptions.scanOptions().count(100).build();
            Cursor<byte[]> cursor = stringRedisConn.keyCommands().scan(options);

            while (cursor.hasNext()) {
                byte[] next = cursor.next();
                String s = new String(next);
                System.out.println();
            }
            return null;
        });

        assertTrue(optionalStudent.isPresent());
    }

}