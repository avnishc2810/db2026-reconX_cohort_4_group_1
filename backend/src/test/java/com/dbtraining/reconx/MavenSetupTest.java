package com.dbtraining.reconx;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TICKET-ADV048 — Unit test verifying Maven enterprise project setup and dependencies.
 */
class MavenSetupTest {

    @Test
    void testMavenProjectSetup_hasRequiredClassesOnClasspath() {
        assertThat(com.dbtraining.reconx.ReconxApplication.class).isNotNull();
        assertThat(org.mapstruct.Mapper.class).isNotNull();
    }
}
