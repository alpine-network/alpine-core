/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.util;

import com.google.common.reflect.ClassPath;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Enumerates the class names a plugin ships, so {@link co.crystaldev.alpinecore.framework.Activatable}s
 * can be discovered.
 *
 * @since 0.5.0
 */
@UtilityClass
public final class ClassScanner {

    /**
     * Finds every class shipped alongside the given anchor class, within its package.
     * <p>
     * The anchor supplies both the classloader to scan and, for the fallback, the code source
     * identifying which jar to read.
     *
     * @param anchor a class belonging to the package being scanned
     * @return the fully qualified class names, in no particular order
     */
    public static @NotNull Set<String> scan(@NotNull Class<?> anchor) {
        Package pkg = anchor.getPackage();
        String packageName = pkg == null ? "" : pkg.getName();

        Set<String> names = viaClassPath(anchor.getClassLoader(), packageName);
        if (names.isEmpty()) {
            names = viaCodeSource(anchor, packageName);
        }
        return names;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static @NotNull Set<String> viaClassPath(@Nullable ClassLoader loader, @NotNull String packageName) {
        if (loader == null) {
            return new LinkedHashSet<>();
        }

        Set<String> names = new LinkedHashSet<>();
        try {
            for (ClassPath.ClassInfo info : ClassPath.from(loader).getAllClasses()) {
                if (matches(info.getName(), packageName)) {
                    names.add(info.getName());
                }
            }
        }
        catch (IOException | RuntimeException ignored) {
            // Fall through to the code source scan.
        }
        return names;
    }

    private static @NotNull Set<String> viaCodeSource(@NotNull Class<?> anchor, @NotNull String packageName) {
        Set<String> names = new LinkedHashSet<>();

        CodeSource source = anchor.getProtectionDomain().getCodeSource();
        URL location = source == null ? null : source.getLocation();
        if (location == null) {
            return names;
        }

        File file;
        try {
            file = new File(location.toURI());
        }
        catch (URISyntaxException | IllegalArgumentException ex) {
            // A non-file URL - nothing further we can enumerate.
            return names;
        }

        if (file.isDirectory()) {
            collectFromDirectory(file, file, packageName, names);
        }
        else if (file.isFile()) {
            collectFromArchive(file, packageName, names);
        }
        return names;
    }

    private static void collectFromArchive(@NotNull File archive, @NotNull String packageName,
                                           @NotNull Set<String> names) {
        try (ZipFile zip = new ZipFile(archive)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }

                String className = toClassName(entry.getName());
                if (className != null && matches(className, packageName)) {
                    names.add(className);
                }
            }
        }
        catch (IOException ignored) {
        }
    }

    private static void collectFromDirectory(@NotNull File root, @NotNull File current,
                                              @NotNull String packageName, @NotNull Set<String> names) {
        File[] children = current.listFiles();
        if (children == null) {
            return;
        }

        for (File child : children) {
            if (child.isDirectory()) {
                collectFromDirectory(root, child, packageName, names);
                continue;
            }

            String relative = root.toPath().relativize(child.toPath()).toString().replace(File.separatorChar, '/');
            String className = toClassName(relative);
            if (className != null && matches(className, packageName)) {
                names.add(className);
            }
        }
    }

    /**
     * @return the class name for a class file path, or {@code null} if the path is not a loadable class
     */
    private static @Nullable String toClassName(@NotNull String path) {
        if (!path.endsWith(".class")) {
            return null;
        }

        // Multi-release jars carry duplicate copies under META-INF/versions; module and package
        // descriptors are not loadable types.
        if (path.startsWith("META-INF/") || path.endsWith("module-info.class") || path.endsWith("package-info.class")) {
            return null;
        }

        return path.substring(0, path.length() - ".class".length()).replace('/', '.');
    }

    /**
     * Matches a class against a package prefix.
     * <p>
     * NB: this is a substring test rather than a prefix test, preserving the historical behavior of
     * {@code AlpinePlugin#activateAll}. It is deliberately loose, and callers filter further.
     */
    private static boolean matches(@NotNull String className, @NotNull String packageName) {
        if (packageName.isEmpty()) {
            return true;
        }

        int lastDot = className.lastIndexOf('.');
        String classPackage = lastDot < 0 ? "" : className.substring(0, lastDot);
        return classPackage.contains(packageName);
    }
}
