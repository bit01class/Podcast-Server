package com.github.davinkevin.podcastserver.config

import com.github.davinkevin.podcastserver.cover.CoverConfig
import com.github.davinkevin.podcastserver.download.DownloadConfig
import com.github.davinkevin.podcastserver.find.FindConfig
import com.github.davinkevin.podcastserver.item.ItemConfig
import com.github.davinkevin.podcastserver.playlist.PlaylistConfig
import com.github.davinkevin.podcastserver.podcast.PodcastConfig
import com.github.davinkevin.podcastserver.service.MimeTypeService
import com.github.davinkevin.podcastserver.service.properties.Api
import com.github.davinkevin.podcastserver.service.properties.ExternalTools
import com.github.davinkevin.podcastserver.service.properties.PodcastServerParameters
import com.github.davinkevin.podcastserver.tag.TagConfig
import com.github.davinkevin.podcastserver.update.UpdateConfig
import lan.dk.podcastserver.service.JsonService
import org.apache.tika.Tika
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.convert.converter.Converter
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by kevin on 26/12/2013.
 */
@Configuration
@EnableConfigurationProperties(PodcastServerParameters::class, Api::class, ExternalTools::class)
@Import(
        ItemConfig::class,
        UpdateConfig::class,
        FindConfig::class,
        PodcastConfig::class,
        TagConfig::class,
        CoverConfig::class,
        PlaylistConfig::class,
        DownloadConfig::class,

        JsonService::class
)
@ComponentScan(basePackages = [
    "com.github.davinkevin.podcastserver.service",
    "com.github.davinkevin.podcastserver.manager",
    "com.github.davinkevin.podcastserver.config"
])

class BeanConfigScan {

    @Bean
    fun mimeTypeService(): MimeTypeService {
        return MimeTypeService(Tika())
    }

    @Bean
    @ConfigurationPropertiesBinding
    fun pathConverter() = Converter<String, Path> { source -> Paths.get(source) }
}
