package me.choicore.demo.springsecurity.authentication.controller.endpoint

import me.choicore.demo.springsecurity.authentication.jwt.JwtAuthenticationTokenProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class AuthenticationApiTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val jwtAuthenticationTokenProvider: JwtAuthenticationTokenProvider,
) {

    @Test
    fun verifyJwtAuthenticationWithExpectedExpectedMessage() {
        // given
        val jwt = jwtAuthenticationTokenProvider.generateToken("choicore", 3600)
        // expect
        mockMvc.get("") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
        }.andExpect {
            status { isOk() }
            content {
                contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
                encoding(Charsets.UTF_8.displayName())
                json(
                    """
                    {
                      "code": 0,
                      "message": "Hello, World!"
                    }
                """.trimIndent()
                )
            }
        }
    }

    @Test
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
