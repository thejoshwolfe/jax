package goal.testcases;

import java.util.regex.Pattern;
import sun.misc.LRUCache;

class Scanner_LRUCache extends LRUCache
{
    public Scanner_LRUCache(int i)
    {
        super(i);
    }

    protected Object create(Object s) {
        return Pattern.compile((String)s);
    }
    protected boolean hasName(Object p, Object s) {
        return ((Pattern)p).pattern().equals(s);
    }
}
