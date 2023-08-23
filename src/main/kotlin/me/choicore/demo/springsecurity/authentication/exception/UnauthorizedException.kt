package me.choicore.demo.springsecurity.authentication.exception


sealed class UnauthorizedException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

class InvalidCredentialsException : UnauthorizedException(ERROR_MESSAGE) {
    private companion object {
        const val ERROR_MESSAGE: String = "아이디 또는 비밀번호가 잘못되었습니다."
    }
}

class ExceededLoginAttemptsException : UnauthorizedException(ERROR_MESSAGE) {
    private companion object {
        const val ERROR_MESSAGE: String = "로그인 시도 횟수를 초과하였습니다."
    }
}