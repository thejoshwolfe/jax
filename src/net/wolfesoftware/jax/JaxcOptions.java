package net.wolfesoftware.jax;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import net.wolfesoftware.jax.util.Util;

public class JaxcOptions
{
    public static final String[] DEFAULT_classPath = { "." };
    public String[] classPath = DEFAULT_classPath;
    public static final HashMap<String, String> aliases = new HashMap<String, String>();
    static {
        aliases.put("cp", "classPath");
    }
    private static final HashSet<String> fieldNames = new HashSet<String>();
    static {
        for (Field field : JaxcOptions.class.getDeclaredFields()) {
            if (field.getType() != String[].class)
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
                String[] values = (String[])field.get(this);
                String[] defaultValue = (String[])defaultField.get(null);
                if (Arrays.equals(values, defaultValue))
                    continue;
                for (String value : values)
                    options.add("-" + fieldName + "=" + value);
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
    public static JaxcOptions parse(List<String> args) throws IllegalArgumentException
    {
        HashMap<String, LinkedList<String>> argsMap = new HashMap<String, LinkedList<String>>();
        ArrayList<String> keepArgs = new ArrayList<String>();
        LinkedList<String> unknownArgs = new LinkedList<String>();
        LinkedList<String> needValueArgs = new LinkedList<String>();

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
            // get argValue from next arg if needed
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
            // arg is valid; add to argsMap
            LinkedList<String> argValues = argsMap.get(argName);
            if (argValues == null) {
                argValues = new LinkedList<String>();
                argsMap.put(argName, argValues);
            }
            argValues.add(argValue);
        }
        // report problems
        if (!(unknownArgs.isEmpty() && needValueArgs.isEmpty())) {
            ArrayList<String> errorMessages = new ArrayList<String>();
            for (String unknownArg : unknownArgs)
                errorMessages.add("Unknown option: " + unknownArg);
            for (String needValueArg : needValueArgs)
                errorMessages.add("Option needs value: " + needValueArg);
            throw new IllegalArgumentException(Util.join(errorMessages, "\n"));
        }

        // remove all the switches and leave only the non-switches.
        args.clear();
        args.addAll(keepArgs);

        // create an options object, set the fields as needed, and return it
        JaxcOptions options = new JaxcOptions();
        try {
            for (Entry<String, LinkedList<String>> kvp : argsMap.entrySet())
                JaxcOptions.class.getField(kvp.getKey()).set(options, kvp.getValue().toArray(new String[kvp.getValue().size()]));
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return options;
    }
}