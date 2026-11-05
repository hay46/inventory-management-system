package com.injibara.iams.backend.demo.servlet;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {
    @Bean
    public ServletRegistrationBean<DemoServlet> demoServletRegistration() {
        ServletRegistrationBean<DemoServlet> reg = new ServletRegistrationBean<>(new DemoServlet(), "/demo-servlet");
        reg.setLoadOnStartup(1);
        return reg;
    }
}
