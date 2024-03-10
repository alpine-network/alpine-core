package co.crystaldev.alpinecore.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author BestBearr
 * @since 0.3.0
 */
@UtilityClass
@SuppressWarnings("unchecked")
public final class ReflectionHelper {

    @Nullable
    public static Field findField(@NotNull Class<?> clazz, @NotNull String... fieldNames) {
        for (String fieldName : fieldNames) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            }
            catch (Exception ignored) {
                // NO OP
            }
        }
        return null;
    }

    @Nullable
    public static <T, E> T getPrivateValue(@NotNull Class<? super E> classToAccess, @NotNull E instance, @NotNull String... fieldNames) {
        try {
            Field field = findField(classToAccess, fieldNames);
            if (field != null) {
                return (T) field.get(instance);
            }
        }
        catch (Exception ignored) {
            // NO OP
        }
        return null;
    }

    public static <T, E> void setPrivateValue(@NotNull Class<? super T> classToAccess, @NotNull T instance, @Nullable E value, @NotNull String... fieldNames) {
        try {
            Field field = findField(classToAccess, fieldNames);
            if (field != null) {
                field.set(instance, value);
            }
        }
        catch (Exception ignored) {
            // NO OP
        }
    }

    @NotNull
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Nullable
    public static Class<? super Object> getClass(@NotNull ClassLoader loader, @NotNull String... classNames) {
        for (String className : classNames) {
            try {
                return (Class<? super Object>) Class.forName(className, false, loader);
            }
            catch (Exception ex) {
                // NO OP
            }
        }
        return null;
    }

    @Nullable
    public static Class<? super Object> getClass(@NotNull ClassLoader loader, @NotNull String className) {
        try {
            return (Class<? super Object>) Class.forName(className, false, loader);
        }
        catch (Exception ex) {
            return null;
        }
    }

    @Nullable
    public static Method findMethod(@NotNull Class<?> clazz, @NotNull String methodName, @NotNull Class<?>... parameterTypes) {
        try {
            Method m = clazz.getDeclaredMethod(methodName, parameterTypes);
            m.setAccessible(true);
            return m;
        }
        catch (Exception ignored) {
            // NO OP
        }
        return null;
    }

    @Nullable
    public static Method findMethod(@NotNull Class<?> clazz, @NotNull String[] methodNames, @NotNull Class<?>... parameterTypes) {
        for (String name : methodNames) {
            try {
                Method m = clazz.getDeclaredMethod(name, parameterTypes);
                m.setAccessible(true);
                return m;
            }
            catch (Exception ignored) {
                // NO OP
            }
        }
        return null;
    }

    @Nullable
    public static <R> R invokeMethod(@NotNull Method method, @Nullable Object source, @Nullable Object... parameters) {
        try {
            return (R) method.invoke(source, parameters);
        }
        catch (Exception ex) {
            return null;
        }
    }

    @Nullable
    public static <R> R invokeMethod(@NotNull Class<R> returnType, @NotNull Method method, @Nullable Object source, @Nullable Object... parameters) {
        try {
            Object returnedValue = method.invoke(source, parameters);
            if (returnedValue != null && returnedValue.getClass().isInstance(returnType)) {
                return (R) returnedValue;
            }
        }
        catch (Exception ex) {
            // NO OP
        }
        return null;
    }
}
