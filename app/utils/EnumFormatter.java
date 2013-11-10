package utils;

import com.avaje.ebean.annotation.EnumValue;
import play.data.format.Formatters;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Locale;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 04.08.13
 * Time: 22:50
 */
public class EnumFormatter<T extends Enum> extends Formatters.SimpleFormatter<T> {

    Class<T> currentClass = null;

    public EnumFormatter(Class<T> theClass) {
        currentClass = theClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T parse(String input, Locale l) throws ParseException {
        if(input == null || input.trim().isEmpty() || currentClass == null) {
            return null;
        }

        for(Field field :  currentClass.getDeclaredFields()){
            if(field.getAnnotation(EnumValue.class) != null && field.getAnnotation(EnumValue.class).value().equals(input)) {
                try
                {
                    return (T)field.get(currentClass);
                }
                catch (IllegalAccessException ex)
                {
                    throw new ParseException("field " + field.getName() + " is not accessible",1);
                }
            }
        }

        throw new ParseException("invalid gender",1);
    }

    @Override
    public String print(T enumValue, Locale l) {

        for(Field field : enumValue.getClass().getDeclaredFields()){
            if(enumValue.name().equals(field.getName()) && field.getAnnotation(EnumValue.class) != null) {
                return field.getAnnotation(EnumValue.class).value();
            }
        }

        return "";
    }
}
