package me.choicore.likeapuppy.authentication.repository.persistence

import me.choicore.likeapuppy.authentication.repository.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface UserJpaRepository : CrudRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.email = :identifier OR u.mobile = :identifier")
    fun findByIdentifier(@Param("identifier") identifier: String): UserEntity?
}