package com.edi.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@Profile("prod")
public class ProdDataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(@Value("${DATABASE_URL}") String databaseUrl) throws Exception {
        URI uri = new URI(databaseUrl.replace("postgres://", "postgresql://")
                                     .replace("postgresql://", "http://"));
        String[] userInfo = uri.getUserInfo().split(":", 2);

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath());
        ds.setUsername(userInfo[0]);
        ds.setPassword(userInfo[1]);
        return ds;
    }
}
