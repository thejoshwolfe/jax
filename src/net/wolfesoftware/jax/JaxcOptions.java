package net.wolfesoftware.jax;

import java.lang.reflect.Field;
import java.util.*;
import net.wolfesoftware.jax.util.Util;

public class JaxcOptions
{
    public static final String DEFAULT_classPath = ".";
    public String classPath = DEFAULT_classPath;

    public String toString()
    {
        ArrayList<String> options = new ArrayList<String>();
        for (Field field : JaxcOptions.class.getDeclaredFields()) {
            String fieldName = field.getName();
            if (fieldName.startsWith("DEFAULT_"))
                continue;
            try {
                Field defaultField = JaxcOptions.class.getField("DEFAULT_" + fieldName);
                String value = (String)field.get(this);
                String defaultValue = (String)defaultField.get(null);
                if (value.equals(defaultValue))
                    continue;
                options.add("--" + fieldName + "=" + value);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return Util.join(options, " ");
    }

    public static JaxcOptions parse(List<String> args)
    {
        return new JaxcOptions();
    }
}
