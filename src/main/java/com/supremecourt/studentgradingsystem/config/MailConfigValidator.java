package com.supremecourt.studentgradingsystem.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailConfigValidator {

    @Value("${spring.mail.host:}")
    private String host;

    @Value("${spring.mail.port:}")
    private String port;

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    @Value("${app.mail.from:${spring.mail.username:}}")
    private String from;

    private final Environment env;

    public MailConfigValidator(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void validate() {
        boolean missing = isEmpty(host) || isEmpty(port) || isEmpty(username) || isEmpty(password) || isEmpty(from);
        boolean isProd = hasProfile("prod");
        if (missing) {
            if (isProd) {
                throw new IllegalStateException("Mail config missing in prod. Set SPRING_MAIL_HOST/PORT/USERNAME/PASSWORD and APP_MAIL_FROM.");
            } else {
                log.warn("Mail config incomplete (profile: {}). Set SPRING_MAIL_* and APP_MAIL_FROM before sending emails.", String.join(",", env.getActiveProfiles()));
            }
        }
    }

    private boolean hasProfile(String profile) {
        for (String p : env.getActiveProfiles()) {
            if (profile.equalsIgnoreCase(p)) return true;
        }
        return false;
    }

    private boolean isEmpty(String s) { return s == null || s.isBlank(); }
}
