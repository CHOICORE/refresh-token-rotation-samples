package me.choicore.demo.springsecurity.authentication.repository.persistence

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime

@Entity
@DynamicUpdate
@Table(name = "user")
class UserEntity(
    val nickname: String,
    @Embedded
    val username: Username,
    val email: String,
    val password: String,
    val mobile: String,
    @Column(length = 1)
    val gender: Gender,

    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long = 0
    var lastLoggedInAt: LocalDateTime? = null
    var loginAttempts: Int = 0

    @Embeddable
    class Username(
        val firstName: String,
        val lastName: String,
    )

    enum class Gender(val code: Int) {
        UNKNOWN(0),
        MAN(1),
        WOMAN(2);

        companion object {
            fun of(code: Int): Gender = entries.firstOrNull { it.code == code } ?: UNKNOWN
        }
    }
}