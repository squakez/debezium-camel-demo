package com.redhat.debezium.demo.user.repository;

import com.redhat.debezium.demo.user.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
