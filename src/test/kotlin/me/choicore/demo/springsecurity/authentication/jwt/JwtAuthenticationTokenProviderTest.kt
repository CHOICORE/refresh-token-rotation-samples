package me.choicore.demo.springsecurity.authentication.jwt

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.time.Instant


@SpringBootTest
class JwtAuthenticationTokenProviderTest(
    @Autowired private val jwtAuthenticationTokenProvider: JwtAuthenticationTokenProvider,

    @Autowired private val jwtDecoder: JwtDecoder,

    ) {
    val invalidToken =
        "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2ZXIiLCJleHAiOjE2OTI4NjY5NDcsImlhdCI6MTY5Mjg2NjkzNywianRpIjoiYmFhM2E1NjctMmIxNy00NzcwLTgxZmMtYzdiNWE0NTk2MGUzIn0.fGwITJ3nlNabTIvIK3Zmk81pZONWL3RQd_xi0YQZvwBWGadUdyMLiDXCM_wIVIQGELjZKkoLX925GxCAm-_UfubOdlWekSWEpNmByLEeD_DC_ItiXcnNvbtxRBk_8yvXuUToXqbBffbihyAtsS5pt8-ACGK4A81GUGmIx_dey4kp8de2x0EjpsEgnOT0UvyiHwEMzpDpRuSKRTcjwK5B1PfNi1nT547ZYdz8_43xcxzzWON_6zos19HJP89Z6TH5xS3eeff0mfC3LuycHeoiNXQP-yxFz2rlCbYMQ9XAPRrmRTBc3UzJ0JpIQgW0bDJSLQIzVPPBYUqkkrSjcKXnaA"


    val invalidToken2 =
        "eyJhbGciOiJSUzI12NiJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2ZXIiLCJleHAiOjE2OTI4NjY5NDcsImlhdCI6MTY5Mjg2NjkzNywianRpIjoiYmFhM2E1NjctMmIxNy00NzcwLTgxZmMtYzdiNWE0NTk2MGUzIn0.fGwITJ3nlNabTIvIK3Zmk81pZONWL3RQd_xi0YQZvwBWGadUdyMLiDXCM_wIVIQGELjZKkoLX925GxCAm-_UfubOdlWekSWEpNmByLEeD_DC_ItiXcnNvbtxRBk_8yvXuUToXqbBffbihyAtsS5pt8-ACGK4A81GUGmIx_dey4kp8de2x0EjpsEgnOT0UvyiHwEMzpDpRuSKRTcjwK5B1PfNi1nT547ZYdz8_43xcxzzWON_6zos19HJP89Z6TH5xS3eeff0mfC3LuycHeoiNXQP-yxFz2rlCbYMQ9XAPRrmRTBc3UzJ0JpIQgW0bDJSLQIzVPPBYUqkkrSjcKXnaA"


    val validToken =
        "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2ZXIiLCJleHAiOjE2OTI5NTMzMzcsImlhdCI6MTY5Mjg2NjkzNywianRpIjoiYmFhM2E1NjctMmIxNy00NzcwLTgxZmMtYzdiNWE0NTk2MGUzIn0.eQpNllRpFgDhQvZ-XVzVwxwJkIRXmbj2bM_BSE0QGXUH3dUJAAi1fW7EEqd2_7N1zodUpm7IplyEtiEOO7_FVirAKX2ptpoq8b6o0Z7bkTMHzxZMjLR-Dx9XdBPUn6VXH6ufvrLH2rj9BmCeCH6RRyv13M6WNVmG5ISLNN5dbrL5Rflv8faWiKut6tC1pqzD7-fxL3F0VDFEhZXpj31N2DXUJDdw9z0Pl-Da-ENwJtHbsXg6mQlLgxwg9PBSk1y8JuY0JBJRom-2btSPQP_PqK3F5iHSwAzKAU_OnRkcymsGwZGNO8IHZnayvjKqtqg4s5ZyxcWoePIniInEpsruEA"

    @Test
    fun `validation invalid token `() {
        val validateToken = jwtAuthenticationTokenProvider.validateToken(invalidToken)

        Assertions.assertThat(validateToken).isFalse()
    }

    @Test
    fun `create token `() {
        val jwt = jwtAuthenticationTokenProvider.generateToken("choicore", Instant.EPOCH.epochSecond, Instant.EPOCH)

        Assertions.assertThatNoException().isThrownBy {
            jwtDecoder.decode(jwt)
        }
    }


    @Test
    fun `decode token`() {
        Assertions.assertThatThrownBy {
            val decode = jwtDecoder.decode(invalidToken2)
        }.isInstanceOf(BadJwtException::class.java)
    }


    @Test
    fun `validation valid token `() {
        val validateToken = jwtAuthenticationTokenProvider.validateToken(validToken)

        Assertions.assertThat(validateToken).isTrue()
    }

}