package me.choicore.demo.springsecurity.authentication.repository.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicUpdate
import java.time.Instant


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

    @Column(
        insertable = false,
        updatable = true
    )
    @ColumnDefault("0")
    var failedLoginAttempts: Int = 0

    var lastLoggedInAt: Instant? = Instant.now()

    var lastPasswordModifiedAt: Instant? = Instant.now()

    var registeredAt: Instant? = Instant.now()

//    val zonedLastLoggedInAt: LocalDateTime?
//        get() = lastLoggedInAt?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) }
//
//    val zonedLastPasswordModifiedAt: LocalDateTime?
//        get() = lastPasswordModifiedAt?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) }
//
//    val zonedRegisteredAt: LocalDateTime?
//        get() = registeredAt?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) }

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