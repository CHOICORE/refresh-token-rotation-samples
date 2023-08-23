package me.choicore.demo.springsecurity.authentication.service.domain

import me.choicore.demo.springsecurity.authentication.repository.persistence.UserEntity

/**
 * 로그인 시도 횟수 제한
 */
private const val LOGIN_ATTEMPTS_LIMIT = 5

/**
 *  [UserEntity]는 공통 코어 모듈에 위치해 있고, 인증 관련 관심사 분리를 위해 [UserEntity]의 확장 함수를 정의합니다.
 * 이를 통해 [UserEntity]의 내부 상태를 변경할 수 있습니다.
 */
internal fun UserEntity.loginAttemptsExceeded(loginAttemptsLimit: Int): Boolean {
    return this.loginAttempts >= LOGIN_ATTEMPTS_LIMIT
}

/**
 * 패스워드 오류 횟수를 초기화한다.
 */
internal fun UserEntity.loginAttemptsClear() {
    this.loginAttempts = 0
}

/**
 * 패스워드 오류 횟수를 증가시킨다.
 */
internal fun UserEntity.loginFailure() {
    this.loginAttempts += 1
}

/**
 * 로그인 성공 시, 패스워드 오류 횟수를 초기화하고 최근 로그인 시간을 갱신한다.
 */
internal fun UserEntity.updateStatus() {
    this.loginAttemptsClear()
    this.lastLoggedInAt = java.time.LocalDateTime.now()
}
