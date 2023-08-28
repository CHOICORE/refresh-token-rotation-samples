package me.choicore.demo.springsecurity.authentication

import com.zaxxer.hikari.HikariDataSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import java.sql.ResultSet
import javax.sql.DataSource


@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class DataSourceTest(
    var dataSource: DataSource
) {

    @Test
    fun `verify that the data source is not null`() {
        assertThat(dataSource).isNotNull
    }

    @Test
    fun `verify that the data source is HikariDataSource`() {
        assertThat(dataSource).isInstanceOf(HikariDataSource::class.java)
    }

    @Test
    fun `verify that the data source is postgreSQL database`() {
        assertThat(dataSource.connection).isNotNull
        assertThat(dataSource.connection.metaData).isNotNull
        assertThat(dataSource.connection.metaData.databaseProductName).isEqualTo("PostgreSQL")
        assertThat(dataSource.connection.metaData.url).isEqualTo("jdbc:postgresql://localhost:5432/test")
    }

    @Test
    fun `print using table`() {
        dataSource.connection.use { connection ->
            val databaseMetaData = connection.metaData
            val resultSet: ResultSet = databaseMetaData.getTables(null, null, "%", arrayOf("TABLE"))
            while (resultSet.next()) {
                val tableName = resultSet.getString("TABLE_NAME")
                println(tableName)
            }
        }
    }
}