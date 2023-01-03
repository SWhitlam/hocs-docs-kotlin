package uk.gov.digital.ho.hocs.document.application

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlywayConfiguration(
        @Value("\${spring.flyway.url}") private val url: String,
        @Value("\${spring.flyway.locations}") private val locations: String,
        @Value("\${spring.flyway.schemas}") private val schemas: String,
        @Value("\${spring.r2dbc.username}") private val user: String,
        @Value("\${spring.r2dbc.password}") private val password: String
) {
    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        return Flyway(
                Flyway.configure()
                        .dataSource(url, user, password)
                        .locations(locations)
                        .schemas(schemas)
        )
    }
}