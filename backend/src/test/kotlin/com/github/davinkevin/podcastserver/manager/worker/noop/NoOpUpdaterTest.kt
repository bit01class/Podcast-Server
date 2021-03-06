package com.github.davinkevin.podcastserver.manager.worker.noop

import com.github.davinkevin.podcastserver.manager.worker.NO_MODIFICATION
import com.github.davinkevin.podcastserver.manager.worker.PodcastToUpdate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI
import java.util.*

/**
 * Created by kevin on 17/03/2016 for Podcast Server
 */
class NoOpUpdaterTest {

    private val noOpUpdater = NoOpUpdater()

    @Test
    fun `should return an empty set of items`() {
        assertThat(noOpUpdater.blockingFindItems(PodcastToUpdate(UUID.randomUUID(), URI("https://foo.bar.com"), "")))
                .isEmpty()
    }

    @Test
    fun `should return an empty signature`() {
        assertThat(noOpUpdater.blockingSignatureOf(URI("http://foo.bar.com"))).isEmpty()
    }

    @Test
    fun `should return its type`() {
        assertThat(noOpUpdater.type().key).isEqualTo("NoOpUpdater")
        assertThat(noOpUpdater.type().name).isEqualTo("NoOpUpdater")
    }

    @Test
    fun `should not be compatible`() {
        assertThat(noOpUpdater.compatibility("foo"))
                .isEqualTo(Integer.MAX_VALUE)
    }
}
