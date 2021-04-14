package org.hazelcast.query.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientApplication {

    @Bean
    public HazelcastInstance client() {
        return HazelcastClient.newHazelcastClient();
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
