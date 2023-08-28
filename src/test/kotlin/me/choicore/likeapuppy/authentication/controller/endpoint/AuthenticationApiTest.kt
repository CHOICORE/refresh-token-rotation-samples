package me.choicore.likeapuppy.authentication.controller.endpoint

import me.choicore.likeapuppy.authentication.jwt.JwtAuthenticationTokenProvider
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AuthenticationApiTest(
    private val mockMvc: MockMvc,
    private val jwtAuthenticationTokenProvider: JwtAuthenticationTokenProvider,
) {

//    @Test
//    fun verifyJwtAuthenticationWithExpectedExpectedMessage() {
//
//        // given
//        val jwt = jwtAuthenticationTokenProvider.generateToken("choicore", 3600)
//
//        // expect
//        mockMvc.get("") {
//            header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
//        }.andExpect {
//            status { isOk() }
//            content {
//                contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
//                encoding(Charsets.UTF_8.displayName())
//                json(
//                    """
//                    {
//                      "code": 0,
//                      "message": "Hello, World!"
//                    }
//                """.trimIndent()
//                )
//            }
//        }
//    }

    @Test
    @DisplayName("만료된 토큰으로 인증 시도 시 [401 - Unauthorized] 응답을 반환한다.")
    fun verifyExpiredTokenResultsInUnauthorized() {
        // given
        val jwt = jwtAuthenticationTokenProvider.generateToken("choicore", Instant.EPOCH.epochSecond + 1, Instant.EPOCH)
        // expect
        mockMvc.get("") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
        }.andExpect {
            status { isUnauthorized() }
            content {
                contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
                encoding(Charsets.UTF_8.displayName())
                header {
                    exists(HttpHeaders.WWW_AUTHENTICATE)
                }
            }
        }
    }

}