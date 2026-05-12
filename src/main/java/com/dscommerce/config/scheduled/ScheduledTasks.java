package com.dscommerce.config.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class ScheduledTasks {

    private final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    private final JdbcTemplate jdbcTemplate;

    public ScheduledTasks(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(cron = "0 0 3 * * *") //everyday at 3h
    @Transactional
    public void purgeExpiredAuthorizations() {
        int deleted = jdbcTemplate.update(
                "DELETE FROM oauth2_authorization WHERE refresh_token_expires_at < NOW()"
        );
        LOG.info("Purge {} expired Oauth2 authorizations", deleted);
    }
}
