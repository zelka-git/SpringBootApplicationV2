package com.example.demo.repository;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureEmbeddedDatabase(type = POSTGRES, provider = ZONKY)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @Sql("classpath:db/insert_item.sql")
    void getById() {
        UUID id = UUID.fromString("866bf385-f4c9-4994-a389-5690fe4c31ee");
        assertTrue(itemRepository.findById(id).isPresent());
    }

}