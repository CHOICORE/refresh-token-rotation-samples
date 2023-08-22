package me.choicore.demo.springsecurity.authentication.service

import me.choicore.demo.springsecurity.authentication.repository.persistence.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

internal class AuthenticationUserDetails internal constructor(userEntity: UserEntity) : UserDetails {
    private val identifier: String
    private val password: String
    private val accountNonExpired: Boolean
    private val accountNonLocked: Boolean

    init {
        this.identifier = userEntity.email
        this.password = userEntity.password
        this.accountNonExpired = userEntity.lastLoggedInAt?.plusYears(1)?.isAfter(LocalDateTime.now()) ?: true
        this.accountNonLocked = userEntity.loginAttempts <= 5
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(
            SimpleGrantedAuthority("USER"),
            SimpleGrantedAuthority("ADMIN"),
        )
    }

    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String {
        return this.identifier
    }

    override fun isAccountNonExpired(): Boolean {
        return this.accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return this.accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}