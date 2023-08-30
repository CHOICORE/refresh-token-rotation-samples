package me.choicore.likeapuppy.authentication.jwt

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.test.context.TestConstructor


@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JwtAuthenticationTokenProviderTest(
    private val jwtAuthenticationTokenProvider: JwtAuthenticationTokenProvider,
) {

    @Nested
    @DisplayName("토큰을 검증한다.")
    inner class ValidateJwt {

        @Test
        @DisplayName("만료된 토큰을 검증하면 [InvalidBearerTokenException]가 발생한다.")
        fun testExpiredTokenValidate() {

            // given
            val jwt =
                "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJsaWtlLWEtcHVwcHktYXV0aGVudGljYXRpb24tc2VydmVyIiwiZXhwIjoxNjkzMzc4Mzg4LCJpYXQiOjE2OTMzNzc3ODgsImp0aSI6ImE4ZDk5ZmRiLWM5MDMtNDVkZi05NTMxLWE4MjE2ZTAzYTY3MCJ9.lbR7diIfIsbX3uxqUNhOyFpZbIVRdXXAi2FYZEhtjy2IhyLO1mphCO6AkRZp9_m-p-skgyBUqcDDcS_yPVP6CbrrpVMBhTsLBlKdzXKDPZSjNrcKYCdI84olWRS_RnavInaGe1GofGdmjm73eC-NcWwWzBMeXisTNHk2Pq0TzciN7APL23D9PVpFH6dEtcRZ0SBv-nMBkH6nbgK3ZSyT7PHsYJhPGeszehNaqDsZPffCYl99Ua3Go3eUI-n-sZspS2HkFejWz-9LAvWN9GinW-lBJYfqqD_t254Gsrpy5G-3pJdMaBGm0Q7qOhA5JwTTCLzovXaafyARASsfdRu-tQ"

            // then
            Assertions.assertThatThrownBy {

                // when
                jwtAuthenticationTokenProvider.validateTokenValue(jwt)
            }.isInstanceOf(InvalidBearerTokenException::class.java)
                .hasMessageContaining("Jwt expired at")
        }

        @Test
        @DisplayName("유효하지 않은 토큰을 검증하면 [InvalidBearerTokenException]가 발생한다.")
        fun testInvalidTokenValidate() {

            // given
            val jwt = "INVALID"

            // then
            Assertions.assertThatThrownBy {

                // when
                jwtAuthenticationTokenProvider.validateTokenValue(jwt)
            }.isInstanceOf(InvalidBearerTokenException::class.java)
                .hasMessageContaining("Invalid JWT")
        }
    }
}