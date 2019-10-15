package com.redhat.debezium.demo.user.configuration;

import com.redhat.debezium.demo.user.repository.UserRepository;
import com.redhat.debezium.demo.user.service.UserService;
import com.redhat.debezium.demo.user.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
public class UserBootConfiguration {

    @Bean
    public UserService userService(){
        return new UserServiceImpl();
    }
}
