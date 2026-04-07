package com.exe.vocafy_BE

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.Assert.assertEquals
import org.testng.Assert.assertNotNull
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import javax.sql.DataSource

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DatabaseConnectionTestNG : AbstractTestNGSpringContextTests() {

    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeClass(alwaysRun = true)
    fun verifyInjection() {
        assertNotNull(dataSource, "DataSource should be initialized")
        assertNotNull(jdbcTemplate, "JdbcTemplate should be initialized")
    }

    @Test(groups = ["db"], description = "Simple query confirms DB connectivity")
    fun shouldConnectAndRunSelectOne() {
        val one = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
        assertEquals(one, 1)
    }
}

