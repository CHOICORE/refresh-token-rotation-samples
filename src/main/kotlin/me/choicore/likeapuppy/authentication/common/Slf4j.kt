package me.choicore.likeapuppy.authentication.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline val <reified T> T.Slf4j: Logger
    get() = LoggerFactory.getLogger(T::class.java)
