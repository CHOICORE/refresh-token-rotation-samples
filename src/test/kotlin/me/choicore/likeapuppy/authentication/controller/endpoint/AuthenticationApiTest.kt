package me.choicore.likeapuppy.authentication.controller.endpoint

import me.choicore.likeapuppy.authentication.jwt.AuthenticationToken
import me.choicore.likeapuppy.authentication.jwt.JwtAuthenticationTokenProvider
import me.choicore.likeapuppy.authentication.repository.ephemeral.TokenRedisRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AuthenticationApiTest(
    private val mockMvc: MockMvc,
    private val jwtAuthenticationTokenProvider: JwtAuthenticationTokenProvider,
    private val redisRepository: TokenRedisRepository,
) {
    @AfterEach
    fun tearDown() {
        redisRepository.deleteAll()
    }

    @Nested
    @DisplayName("인증 및 인가 성공 케이스")
    inner class SuccessCases {
        @Test
        @DisplayName("인증 토큰을 발급받아 인증을 성공한다. [200 - OK]")
        fun verifyJwtAuthenticationWithExpectedExpectedMessage() {

            // given
            val authenticationToken: AuthenticationToken = jwtAuthenticationTokenProvider.generateAuthenticationToken(1L)

            val jwt: String = authenticationToken.accessToken

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
        @DisplayName("리프레시 토큰을 통해 엑세스 토큰과, 리프레시 토큰을 재발급한다. [200 - OK]")
        fun shouldReissueTokensUsingRefreshToken() {

            // given
            val authenticationToken: AuthenticationToken = jwtAuthenticationTokenProvider.generateAuthenticationToken(1L)

            // expect
            mockMvc.post("/v1/auth/token") {
                contentType = MediaType.APPLICATION_JSON
                content = """
                    {
                        "refresh_token": "${authenticationToken.refreshToken}"
                    }
                """.trimIndent()
            }.andExpect {
                status { isOk() }
                content {
                    contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
                    encoding(Charsets.UTF_8.displayName())
                    json(
                        """
                    {
                      "code": 0,
                      "message": "SUCCEED"
                    }
                """.trimIndent()
                    )
                }
            }
        }
    }

    @Nested
    @DisplayName("인증 및 인가 실패 케이스")
    inner class FailCases {

        @Test
        @DisplayName("유효하지 않은 토큰으로 인증 시도 시 [401 - Unauthorized] 응답을 반환한다.")
        fun verifyInvalidTokenResultsInUnauthorized() {

            // given
            val issuedAt: Instant = Instant.now()
            val jwt: String = jwtAuthenticationTokenProvider.generateToken(
                jti = UUID.randomUUID().toString(),
                expiresAt = issuedAt.plusSeconds(10),
                issuedAt = issuedAt
            )

            // expect
            mockMvc.get("") {
                header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
            }.andExpect {
                status { isUnauthorized() }
                content {
                    contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
                    encoding(Charsets.UTF_8.displayName())
                    json(
                        """
                        {"code":{"errorCode":"005","errorMessage":"INVALID_TOKEN"},"message":"유효하지 않은 토큰입니다."}
                    """.trimIndent()
                    )
                }
            }
        }

        @Test
        @DisplayName("만료된 토큰으로 인증 시도 시 [401 - Unauthorized] 응답을 반환한다.")
        fun verifyExpiredTokenResultsInUnauthorized() {
            // given
            val jwt =
                "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJsaWtlLWEtcHVwcHktYXV0aGVudGljYXRpb24tc2VydmVyIiwiZXhwIjoxNjkzMzc4Mzg4LCJpYXQiOjE2OTMzNzc3ODgsImp0aSI6ImE4ZDk5ZmRiLWM5MDMtNDVkZi05NTMxLWE4MjE2ZTAzYTY3MCJ9.lbR7diIfIsbX3uxqUNhOyFpZbIVRdXXAi2FYZEhtjy2IhyLO1mphCO6AkRZp9_m-p-skgyBUqcDDcS_yPVP6CbrrpVMBhTsLBlKdzXKDPZSjNrcKYCdI84olWRS_RnavInaGe1GofGdmjm73eC-NcWwWzBMeXisTNHk2Pq0TzciN7APL23D9PVpFH6dEtcRZ0SBv-nMBkH6nbgK3ZSyT7PHsYJhPGeszehNaqDsZPffCYl99Ua3Go3eUI-n-sZspS2HkFejWz-9LAvWN9GinW-lBJYfqqD_t254Gsrpy5G-3pJdMaBGm0Q7qOhA5JwTTCLzovXaafyARASsfdRu-tQ"

            // expect
            mockMvc.get("") {
                header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
            }.andExpect {
                status { isUnauthorized() }
                content {
                    contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE)
                    encoding(Charsets.UTF_8.displayName())
                    json(
                        """
                        {"code":{"errorCode":"006","errorMessage":"EXPIRED_TOKEN"},"message":"만료된 토큰입니다."}
                    """.trimIndent()
                    )
                    header {
                        exists(HttpHeaders.WWW_AUTHENTICATE)
                    }

                }
            }
        }
    }
}