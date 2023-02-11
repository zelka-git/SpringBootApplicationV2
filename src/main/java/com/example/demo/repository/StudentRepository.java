package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Profile("redis")
@Repository
public interface StudentRepository extends CrudRepository<Student, String> {
}
