package com.dbtraining.reconx.observability;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * TICKET-ADV059 — Unit test for custom DatabaseHealthIndicator.
 */
class DatabaseHealthIndicatorTest {

    @Test
    void testHealthCheck_returnsUpWithDetails() throws Exception {
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery("SELECT 1")).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(ds);
        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("query", "SELECT 1");
        assertThat(health.getDetails()).containsKey("elapsedMs");
        verify(stmt).setQueryTimeout(2);
    }

    @Test
    void testHealthCheck_whenSqlException_returnsDown() throws Exception {
        DataSource ds = mock(DataSource.class);
        when(ds.getConnection()).thenThrow(new SQLException("Connection refused"));

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(ds);
        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("query", "SELECT 1");
        assertThat(health.getDetails()).containsKey("error");
    }
}
