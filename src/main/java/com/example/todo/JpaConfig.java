package com.example.todo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration enabling auditing (createdAt / updatedAt auto-population).
 * Kept in a separate class so @WebMvcTest slice tests do not load it.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
