package net.wolfesoftware.jax;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import net.wolfesoftware.jax.util.Util;

public class JaxcOptions
{
    public static final String[] DEFAULT_classPath = { "." };
    public String[] classPath = DEFAULT_classPath;
    public static final boolean DEFAULT_javaCompatabilityMode = false;
    public boolean javaCompatabilityMode = DEFAULT_javaCompatabilityMode;
    public static final HashMap<String, String> aliases = new HashMap<String, String>();
    static {
        aliases.put("cp", "classPath");
        aliases.put("javaMode", "javaCompatabilityMode");
    }
    private static final HashSet<String> fieldNames = new HashSet<String>();
    static {
        for (Field field : JaxcOptions.class.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (!(fieldType == String[].class || fieldType == boolean.class))
                continue;
            String fieldName = field.getName();
            if (fieldName.startsWith("DEFAULT_"))
                continue;
            fieldNames.add(fieldName);
        }
    }

    public String toString()
    {
        ArrayList<String> options = new ArrayList<String>();
        for (String fieldName : fieldNames) {
            try {
                Field field = JaxcOptions.class.getField(fieldName);
                Field defaultField = JaxcOptions.class.getField("DEFAULT_" + fieldName);
                Class<?> fieldType = defaultField.getType();
                if (fieldType == String[].class) {
                    String[] values = (String[])field.get(this);
                    String[] defaultValue = (String[])defaultField.get(null);
                    if (Arrays.equals(values, defaultValue))
                        continue;
                    for (String value : values)
                        options.add("-" + fieldName + "=" + value);
                } else if (fieldType == boolean.class) {
                    boolean value = field.getBoolean(this);
                    boolean defaultBooleanValue = defaultField.getBoolean(this);
                    if (value == defaultBooleanValue)
                        continue;
                    options.add("-" + fieldName);
                } else
                    throw null;
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return Util.join(options, " ");
    }

    /**
     * Removes options from args and returns an options object.
     * @param args list of string arguments like from <code>main(String[] args)</code>.
     *     Options are removed from the list leaving only non-option arguments. Array-based
     *     List implementations are recommended as random access is performed and 'removal'
     *     is actually implemented as a {@link List#clear() clear} and 
     *     {@link List#addAll(Collection) addAll}.
     * @return a {@link JaxcOptions} object representing the options removed from args
     * @throws IllegalArgumentException if a parameter is unrecognized or needs a value it didn't get.
     */
    @SuppressWarnings("unchecked")
    public static JaxcOptions parse(List<String> args) throws IllegalArgumentException
    {
        // results
        HashMap<String, Object> argsMap = new HashMap<String, Object>();
        ArrayList<String> keepArgs = new ArrayList<String>();
        // error collecting
        LinkedList<String> unknownArgs = new LinkedList<String>();
        LinkedList<String> needValueArgs = new LinkedList<String>();
        LinkedList<String> needNoValueArgs = new LinkedList<String>();
        LinkedList<String> duplicateArgs = new LinkedList<String>();

        // analyze all the arguments
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (!arg.startsWith("-")) {
                // not a switch. leave this one in args 
                keepArgs.add(arg);
                continue;
            }
            // split on equals
            int equalsIndex = arg.indexOf('=');
            String argName, argValue;
            if (equalsIndex != -1) {
                argName = arg.substring(1, equalsIndex);
                argValue = arg.substring(equalsIndex + 1);
            } else {
                argName = arg.substring(1);
                argValue = null;
            }
            // check name and resolve any alias
            if (!fieldNames.contains(argName)) {
                String realArgName = aliases.get(argName);
                if (realArgName == null) {
                    // bad name
                    unknownArgs.add(argName);
                    continue;
                }
                argName = realArgName;
            }
            Class<?> fieldType = getFieldType(argName);
            boolean needsValue;
            if (fieldType == String[].class)
                needsValue = true;
            else if (fieldType == boolean.class)
                needsValue = false;
            else
                throw null;
            // get argValue from next arg if needed
            if (needsValue) {
                if (argValue == null) {
                    if (i == args.size() - 1) {
                        needValueArgs.add(arg);
                        continue;
                    }
                    String nextArg = args.get(i + 1);
                    if (nextArg.startsWith("-")) {
                        needValueArgs.add(arg);
                        continue;
                    }
                    argValue = nextArg;
                    i++;
                }
            } else {
                if (argValue != null) {
                    // value specified even though you don't need one.
                    needNoValueArgs.add(arg);
                    continue;
                }
            }
            // try to store the value
            Object cachedValue = argsMap.get(argName);
            if (fieldType == String[].class) {
                LinkedList<String> argValues = (LinkedList<String>)cachedValue;
                if (argValues == null) {
                    argValues = new LinkedList<String>();
                    argsMap.put(argName, argValues);
                }
                argValues.add(argValue);
            } else if (fieldType == boolean.class) {
                if (cachedValue != null) {
                    duplicateArgs.add(arg);
                    continue;
                }
                argsMap.put(argName, true);
            } else
                throw null;
        }
        // report problems
        ArrayList<String> errorMessages = new ArrayList<String>();
        for (String unknownArg : unknownArgs)
            errorMessages.add("Unknown option: " + unknownArg);
        for (String needValueArg : needValueArgs)
            errorMessages.add("Option needs value: " + needValueArg);
        for (String needNoValueArg : needNoValueArgs)
            errorMessages.add("Option doesn't take value: " + needNoValueArg);
        for (String duplicateArg : duplicateArgs)
            errorMessages.add("Duplicate option: " + duplicateArg);
        if (!errorMessages.isEmpty())
            throw new IllegalArgumentException(Util.join(errorMessages, "\n"));

        // remove all the switches and leave only the non-switches.
        args.clear();
        args.addAll(keepArgs);

        // create an options object, set the fields as needed, and return it
        JaxcOptions options = new JaxcOptions();
        try {
            for (Entry<String, Object> kvp : argsMap.entrySet()) {
                Field field = JaxcOptions.class.getField(kvp.getKey());
                Class<?> fieldType = field.getType();
                if (fieldType == String[].class) {
                    LinkedList<String> valueList = (LinkedList)kvp.getValue();
                    field.set(options, valueList.toArray(new String[valueList.size()]));
                } else if (fieldType == boolean.class) {
                    field.set(options, true);
                } else
                    throw null;
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return options;
    }

    private static Class<?> getFieldType(String name)
    {
        try {
            return JaxcOptions.class.getField(name).getType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
