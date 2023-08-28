package me.choicore.likeapuppy.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ObjectMapperTest {
    private val javaTimeIncludedObjectMapper = ObjectMapper().registerModules(JavaTimeModule())
    private val defaultEmbeddedObjectMapper = ObjectMapper()


    @Test
    @DisplayName("[java.time.*] 객체를 JavaTimeModule 모듈을 등록한 ObjectMapper로 직렬화 한다.")
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
    @DisplayName("[java.time.*] 객체를 serialize 하기 위해서는 ObjectMapper에 [JavaTimeModule]을 등록해야 한다.")
    fun javaTimeSerializeToDefaultEmbeddedObjectMapper() {

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