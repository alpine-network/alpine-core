/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.util;

import com.cryptomorin.xseries.base.XModule;
import de.exlll.configlib.ConfigurationException;
import de.exlll.configlib.Serializer;
import org.apache.commons.lang.Validate;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author Thomas Wearmouth
 * @since 0.4.10
 */
public final class XModuleSerializer<XForm extends XModule<XForm, ?>> implements Serializer<XModule<XForm, ?>, String> {
    private final Class<? extends XModule<XForm, ?>> cls;

    public XModuleSerializer(Class<? extends XModule<XForm, ?>> cls) {
        Validate.notNull(cls, "XModule class");
        this.cls = cls;
    }

    @Override
    public String serialize(XModule<XForm, ?> element) {
        return element.name();
    }

    @Override
    public XModule<XForm, ?> deserialize(String element) {
        try {
            Method method = this.cls.getDeclaredMethod("of", String.class);
            Optional<XForm> opt = (Optional<XForm>) method.invoke(null, element);
            return opt.orElseThrow(() -> {
                String msg = createExceptionMessage(element);
                return new RuntimeException(msg);
            });
        }
        catch (ReflectiveOperationException ex) {
            throw new ConfigurationException("Encountered a reflection-based exception during deserialization", ex); // TODO(tom): replace with ConfigurationException
        }
    }

    private String createExceptionMessage(String element) {
        return "XModule class " + this.cls.getSimpleName() + " does not contain value '" +
                element + "'.";
    }

    public Class<? extends XModule<XForm, ?>> getModuleCls() {
        return this.cls;
    }
}
