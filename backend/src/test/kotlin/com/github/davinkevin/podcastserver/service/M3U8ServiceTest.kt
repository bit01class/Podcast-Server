package com.github.davinkevin.podcastserver.service

import com.github.davinkevin.podcastserver.fileAsReader
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.io.IOException
import java.io.UncheckedIOException

/**
 * Created by kevin on 22/07/2016.
 */
@ExtendWith(MockitoExtension::class)
class M3U8ServiceTest {

    @Mock lateinit var urlService: UrlService
    @InjectMocks lateinit var m3U8Service: M3U8Service

    @Test
    fun `should get last m3u8 url`() {
        /* Given */
        val resourcePath = "/__files/service/urlService/canalplus.lepetitjournal.20150707.m3u8"
        whenever(urlService.asReader(resourcePath)).then { fileAsReader(resourcePath) }

        /* When */
        val lastUrl = m3U8Service.getM3U8UrlFormMultiStreamFile(resourcePath)

        /* Then */
        assertThat(lastUrl).isEqualTo("http://us-cplus-aka.canal-plus.com/i/1507/02/nip_NIP_59957_,200k,400k,800k,1500k,.mp4.csmil/segment146_3_av.ts")
    }

    @Test
    fun `should handle relative url`() {
        /* Given */
        val resourcePath = "http://a.custom.dom/__files/service/urlService/relative.m3u8"
        whenever(urlService.asReader(resourcePath)).then { fileAsReader("/__files/service/urlService/relative.m3u8") }

        /* When */
        val lastUrl = m3U8Service.getM3U8UrlFormMultiStreamFile(resourcePath)

        /* Then */
        assertThat(lastUrl).isEqualTo("http://a.custom.dom/__files/service/urlService/9dce76b19072beda39720aa04aa2e47a-video=1404000-audio_AACL_fra_70000_315=70000.m3u8")
    }

    @Test
    fun `should throw exception if no url found`() {
        /* Given */
        val resourcePath = "/__files/service/urlService/canalplus.lepetitjournal.20150707.m3u8"
        whenever(urlService.asReader(resourcePath)).then { throw UncheckedIOException("Error during stream processing", IOException()) }

        /* When */
        assertThatThrownBy { m3U8Service.getM3U8UrlFormMultiStreamFile(resourcePath) }

                /* Then */
                .hasMessageStartingWith("java.io.UncheckedIOException: Error during stream processing")

    }
}
