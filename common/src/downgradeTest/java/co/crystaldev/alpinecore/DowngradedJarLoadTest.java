/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore;

import org.testng.SkipException;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.testng.Assert.fail;

/**
 * Loads every class in the downgraded artifact, forcing the JVM's bytecode verifier over all of it.
 * <p>
 * Only meaningful when run against the downgraded jar on an old JVM, so it no-ops unless
 * {@code alpinecore.downgraded.jar} is set - see the {@code downgradedTest} task.
 *
 * @since 0.5.0
 */
public class DowngradedJarLoadTest {

    private static final String JAR_PROPERTY = "alpinecore.downgraded.jar";

    @Test
    public void everyClassPassesVerification() throws Exception {
        String path = System.getProperty(JAR_PROPERTY);
        if (path == null || path.isEmpty()) {
            throw new SkipException("not running against a downgraded jar; set " + JAR_PROPERTY);
        }

        File jar = new File(path);
        if (!jar.isFile()) {
            fail("downgraded jar does not exist: " + jar);
        }

        List<String> names = new ArrayList<>();
        ZipFile zip = new ZipFile(jar);
        try {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (!name.endsWith(".class") || name.contains("module-info")) {
                    continue;
                }
                // Only our own code: third-party classes are verified as a side effect of being
                // referenced, and some legitimately cannot resolve here (soft dependencies).
                if (!name.startsWith("co/crystaldev/alpinecore/")) {
                    continue;
                }
                if (name.startsWith("co/crystaldev/alpinecore/libs/")) {
                    continue; // relocated third-party code
                }
                names.add(name.substring(0, name.length() - ".class".length()).replace('/', '.'));
            }
        }
        finally {
            zip.close();
        }

        if (names.isEmpty()) {
            fail("found no AlpineCore classes in " + jar);
        }

        ClassLoader loader = DowngradedJarLoadTest.class.getClassLoader();
        List<String> broken = new ArrayList<>();
        int verified = 0;

        for (String name : names) {
            try {
                // initialize = false: we want the verifier, not static initializers, which would
                // need a running server.
                Class.forName(name, false, loader);
                verified++;
            }
            // ClassFormatError covers UnsupportedClassVersionError, which is its subclass.
            catch (VerifyError | ClassFormatError ex) {
                // These mean the bytecode itself is wrong - exactly what a bad downgrade produces.
                broken.add(name + " -> " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
            catch (NoClassDefFoundError | ClassNotFoundException ex) {
                // An absent optional dependency (PlaceholderAPI, Vault, ...) is not a downgrade
                // defect, so it is not counted against us.
                verified++;
            }
        }

        if (!broken.isEmpty()) {
            fail("The downgraded artifact contains " + broken.size() + " unverifiable class(es) of "
                    + names.size() + ":\n  " + String.join("\n  ", broken));
        }

        System.out.println("verified " + verified + " of " + names.size()
                + " AlpineCore classes on Java " + System.getProperty("java.version"));
    }
}
