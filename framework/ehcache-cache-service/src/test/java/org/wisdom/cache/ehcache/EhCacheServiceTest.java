/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2014 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.wisdom.cache.ehcache;

import org.joda.time.Duration;
import org.junit.Test;
import org.wisdom.api.configuration.ApplicationConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Check the creation of the EhCache-based cache service implementation.
 */
public class EhCacheServiceTest {

    @Test
    public void test() throws InterruptedException {
        ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
        EhCacheService svc = new EhCacheService();
        svc.configuration = configuration;
        svc.start();

        assertThat(svc.get("key")).isNull();
        svc.set("key", "value", Duration.standardSeconds(1));
        assertThat(svc.get("key")).isEqualTo("value");

        waitForCleanup(svc);
        assertThat(svc.get("key")).isNull();

        svc.set("key", "value", 0);
        assertThat(svc.get("key")).isEqualTo("value");
        assertThat(svc.remove("key")).isTrue();
        assertThat(svc.get("key")).isNull();
        assertThat(svc.remove("missing")).isFalse();

        svc.stop();
    }

    /**
     * Test #297.
     */
    @Test
    public void testRestart() throws InterruptedException {
        ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
        EhCacheService svc = new EhCacheService();
        svc.configuration = configuration;
        svc.start();

        assertThat(svc.get("key")).isNull();
        svc.set("key", "value", Duration.standardSeconds(1));
        assertThat(svc.get("key")).isEqualTo("value");

        waitForCleanup(svc);
        assertThat(svc.get("key")).isNull();

        svc.set("key", "value", 0);
        assertThat(svc.get("key")).isEqualTo("value");
        assertThat(svc.remove("key")).isTrue();
        assertThat(svc.get("key")).isNull();
        assertThat(svc.remove("missing")).isFalse();

        svc.stop();

        svc.start();

        assertThat(svc.get("key")).isNull();
        svc.set("key", "value", Duration.standardSeconds(1));
        assertThat(svc.get("key")).isEqualTo("value");

        waitForCleanup(svc);
        assertThat(svc.get("key")).isNull();

        svc.set("key", "value", 0);
        assertThat(svc.get("key")).isEqualTo("value");
        assertThat(svc.remove("key")).isTrue();
        assertThat(svc.get("key")).isNull();
        assertThat(svc.remove("missing")).isFalse();

    }

    private void waitForCleanup(EhCacheService svc) throws InterruptedException {
        for (int count = 0; count < 5; count++) {
            Object obj = svc.get("key");
            if (obj == null) {
                return;
            } else {
                Thread.sleep(1000);
            }
        }
    }
}
