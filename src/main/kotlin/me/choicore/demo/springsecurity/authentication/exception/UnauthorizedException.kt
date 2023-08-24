package me.choicore.demo.springsecurity.authentication.exception


sealed class UnauthorizedException : RuntimeException {
    @Suppress("unused")
    constructor() : super()
    constructor(message: String) : super(message)

    @Suppress("unused")
    constructor(message: String, cause: Throwable) : super(message, cause)

    @Suppress("unused")
    constructor(cause: Throwable) : super(cause)
}

sealed class InvalidCredentialsException : UnauthorizedException(ERROR_MESSAGE) {

    private companion object {
        const val ERROR_MESSAGE: String = "아이디 또는 비밀번호가 잘못되었습니다."
    }
}

class InvalidPasswordException : InvalidCredentialsException()

class UsernameNotFoundException : InvalidCredentialsException()

class ExceededLoginAttemptsException : UnauthorizedException(ERROR_MESSAGE) {
    private companion object {
        const val ERROR_MESSAGE: String = "로그인 시도 횟수를 초과하였습니다."
    }
}