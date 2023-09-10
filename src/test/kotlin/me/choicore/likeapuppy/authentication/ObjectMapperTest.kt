package me.choicore.likeapuppy.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ObjectMapperTest {
    private val javaTimeIncludedObjectMapper = ObjectMapper().registerModules(JavaTimeModule())
    private val defaultEmbeddedObjectMapper = ObjectMapper()

    @Nested
    @DisplayName("Jackson ObjectMapper로 [java.time.*] 를 직렬화 한다.")
    inner class JavaTimeSerializeTests {
        @Test
        @DisplayName("[java.time.*] 객체를 JavaTimeModule 모듈을 등록한 ObjectMapper 로 직렬화 한다.")
        fun testJavaTimeSerialize() {

            // given
            val localDateTime: LocalDateTime = LocalDateTime.now()
            val instant: Instant = Instant.now()
            val zonedDateTime: ZonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault())

            // then
            Assertions.assertThatNoException().isThrownBy {

                // when
                javaTimeIncludedObjectMapper.writeValueAsString(localDateTime)
                javaTimeIncludedObjectMapper.writeValueAsString(instant)
                javaTimeIncludedObjectMapper.writeValueAsString(zonedDateTime)
            }
        }

        @Test
        @DisplayName("[java.time.*] 객체를 직렬화하기 위해서는 ObjectMapper 에 [JavaTimeModule]을 등록해야 한다.")
        fun testJavaTimeSerializeToDefaultEmbeddedObjectMapper() {

            // given
            val localDateTime: LocalDateTime = LocalDateTime.now()
            val instant: Instant = Instant.now()
            val zonedDateTime: ZonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault())

            // then
            Assertions.assertThatThrownBy {

                // when
                defaultEmbeddedObjectMapper.writeValueAsString(localDateTime)
                defaultEmbeddedObjectMapper.writeValueAsString(instant)
                defaultEmbeddedObjectMapper.writeValueAsString(zonedDateTime)
            }.isInstanceOf(InvalidDefinitionException::class.java)
        }
    }
}