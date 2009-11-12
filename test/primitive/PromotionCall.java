import java.lang.reflect.Method;

public class PromotionCall
{
    public static void main(String[] args)
    {
        Method[] methods = Promotion.class.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("add2"))
                continue; // helper function. not part of test
            Object value;
            try {
                value = method.invoke(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Class<?> valueClass = value.getClass();
            if (valueClass == Integer.class)
                checkInt(method.getName(), value);
            if (valueClass == Long.class)
                checkLong(method.getName(), value);
            if (valueClass == Float.class)
                checkFloat(method.getName(), value);
            if (valueClass == Double.class)
                checkDouble(method.getName(), value);
        }
        System.out.println("+++ PASS");
    }
    private static void checkInt(String methodName, Object value)
    {
        if (((Integer)value).intValue() != 4)
            throw new RuntimeException(methodName + " : " + value);
    }
    private static void checkLong(String methodName, Object value)
    {
        if (((Long)value).longValue() != 4)
            throw new RuntimeException(methodName + " : " + value);
    }
    private static void checkFloat(String methodName, Object value)
    {
        if (((Float)value).floatValue() != 4)
            throw new RuntimeException(methodName + " : " + value);
    }
    private static void checkDouble(String methodName, Object value)
    {
        if (((Double)value).doubleValue() != 4)
            throw new RuntimeException(methodName + " : " + value);
    }
}