package me.choicore.demo.springsecurity.authentication

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.ZoneId


class SystemDefaultZoneTest {

    @Test
    @DisplayName("시스템 기본 시간대가 서울 시간대인지 확인")
    fun systemDefaultZoneShouldBeEqualToSeoulZone() {
        val systemDefaultZone: ZoneId = ZoneId.systemDefault()
        val seoulZone = ZoneId.of("Asia/Seoul")
        assertThat(systemDefaultZone).isEqualTo(seoulZone)
    }

}