package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roger
 * Date: 23.11.13
 * Time: 18:52
 * To change this template use File | Settings | File Templates.
 */
public class TypeAheadDatum {

    public TypeAheadDatum() {
        tokens = new ArrayList<String>();
    }
    public String value;
    public List<String> tokens;
    public Long id;
    public Object dataobject;
}
