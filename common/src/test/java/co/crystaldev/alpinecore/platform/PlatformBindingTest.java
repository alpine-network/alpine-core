/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.platform;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Covers {@link Platform}'s resolution of the bundled {@link AlpinePlatform}. Guards the
 * failure modes that are otherwise invisible until production.
 *
 * @since 0.5.0
 */
public class PlatformBindingTest {

    @BeforeMethod
    public void resetBinding() {
        Platform.reset();
    }

    @AfterMethod
    public void clearBinding() {
        Platform.reset();
    }

    @Test
    public void resolvesTheDeclaredProvider() {
        AlpinePlatform platform = Platform.bind();

        assertNotNull(platform, "the test service declaration should have been picked up");
        assertEquals(platform.id(), "fake");
    }

    @Test
    public void bindingIsIdempotent() {
        AlpinePlatform first = Platform.bind();
        assertSame(Platform.bind(), first, "rebinding must not create a second provider");
        assertSame(Platform.get(), first, "get() must return the bound provider");
    }

    @Test
    public void isBoundReflectsState() {
        assertFalse(Platform.isBound(), "should start unbound");
        Platform.get();
        assertTrue(Platform.isBound(), "get() should bind on first use");
    }

    /**
     * Under Bukkit the thread context class loader is routinely the server's, not the one that
     * owns AlpineCore's jar. Resolution must ignore it entirely - this is the failure that would
     * otherwise only ever show up on a live server.
     */
    @Test
    public void ignoresTheThreadContextClassLoader() {
        Thread thread = Thread.currentThread();
        ClassLoader original = thread.getContextClassLoader();
        try {
            // A loader that can see no services at all.
            thread.setContextClassLoader(new URLClassLoader(new URL[0], null));
            assertNotNull(Platform.bind(), "resolution must not depend on the context class loader");
        }
        finally {
            thread.setContextClassLoader(original);
        }
    }

    @Test
    public void reportsActionablyWhenNoProviderIsPresent() {
        // Simulates a malformed distribution
        ClassLoader empty = new URLClassLoader(new URL[0], null) {
            @Override
            public Enumeration<URL> getResources(String name) {
                return Collections.emptyEnumeration();
            }
        };

        try {
            Platform.bindWith(empty);
            Assert.fail("expected an IllegalStateException");
        }
        catch (IllegalStateException ex) {
            assertTrue(ex.getMessage().contains("No AlpinePlatform provider"),
                    "message should say what is wrong: " + ex.getMessage());
            assertTrue(ex.getMessage().contains("github.com/alpine-network"),
                    "message should say where to report it: " + ex.getMessage());
        }
    }
}
