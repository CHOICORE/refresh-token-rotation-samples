//package me.choicore.demo.springsecurity.authentication.service
//
//import me.choicore.demo.springsecurity.authentication.repository.persistence.UserJpaRepository
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.stereotype.Component
//
//@Component
//class AuthenticationUserDetailsService(
//    private val userJpaRepository: UserJpaRepository,
//) : UserDetailsService {
//
//    override fun loadUserByUsername(username: String): UserDetails {
//        return AuthenticationUserDetails(
//            userJpaRepository.findByIdentifier(username)
//                ?: throw IllegalArgumentException("사용자 정보가 올바르지 않습니다. 다시 확인해주세요.")
//        )
//    }
//}