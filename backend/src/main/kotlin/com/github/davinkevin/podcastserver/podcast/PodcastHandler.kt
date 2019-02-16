package com.github.davinkevin.podcastserver.podcast

import com.github.davinkevin.podcastserver.extension.ServerRequest.extractHost
import com.github.davinkevin.podcastserver.service.FileService
import com.github.davinkevin.podcastserver.service.properties.PodcastServerParameters
import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.seeOther
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import reactor.core.publisher.switchIfEmpty
import reactor.core.publisher.toMono
import java.net.URI
import java.nio.file.Paths
import java.util.*

/**
 * Created by kevin on 2019-02-15
 */
@Component
class PodcastHandler(
        private val podcastService: PodcastService,
        private val p: PodcastServerParameters,
        private val fileService: FileService
) {

    private var log = LoggerFactory.getLogger(PodcastHandler::class.java)

    fun cover(r: ServerRequest): Mono<ServerResponse> {
        val host = r.extractHost()
        val id = UUID.fromString(r.pathVariable("id"))

        return podcastService.findById(id)
                .doOnNext { log.debug("the url of the podcast cover is ${it.cover.url}")}
                .flatMap { podcast -> podcast
                        .toMono()
                        .map { Paths.get(it.title).resolve("${p.coverDefaultName}.${it.cover.extension()}") }
                        .flatMap { fileService.exists(it) }
                        .map { it.toString().substringAfterLast("/") }
                        .map { UriComponentsBuilder.fromUri(host)
                                .pathSegment("data", podcast.title, it)
                                .build().toUri()
                        }
                        .switchIfEmpty { URI(podcast.cover.url).toMono() }
                }
                .doOnNext { log.debug("Redirect cover to {}", it)}
                .flatMap { seeOther(it).build() }
    }

}

private fun CoverForPodcast.extension() = FilenameUtils.getExtension(url)
