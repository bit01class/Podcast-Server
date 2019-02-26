package com.github.davinkevin.podcastserver.podcast.type

import com.github.davinkevin.podcastserver.manager.selector.UpdaterSelector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.reactive.function.server.router

@Configuration
class TypeRoutingConfig {

    @Bean
    fun typeRouter(type: TypeHandler) = router {
        GET("/api/v1/podcasts/types", type::findAll)
    }
}

@Configuration
@Import(TypeRoutingConfig::class, TypeHandler::class, UpdaterSelector::class)
class TypeConfig
