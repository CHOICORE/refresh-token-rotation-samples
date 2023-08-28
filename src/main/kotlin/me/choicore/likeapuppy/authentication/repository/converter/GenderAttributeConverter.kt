package me.choicore.likeapuppy.authentication.repository.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import me.choicore.likeapuppy.authentication.repository.persistence.entity.UserEntity

@Converter(autoApply = true)
class GenderAttributeConverter : AttributeConverter<UserEntity.Gender, Int> {
    override fun convertToDatabaseColumn(attribute: UserEntity.Gender?): Int {
        return attribute?.code ?: 0
    }

    override fun convertToEntityAttribute(dbData: Int?): UserEntity.Gender {
        return UserEntity.Gender.of(dbData ?: 0)
    }
}