package com.crescentflare.appconfig.model;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Library model: base model for strict typing
 * Derive your custom app configuration from this model for easy integration
 * Important: this moment, only flat models with primitive data types and custom enums are supported
 */
public class AppConfigBaseModel
{
    /**
     * Reflection helper: get list of values (either from getters, or from fields directly)
     */
    public ArrayList<String> valueList()
    {
        ArrayList<String> list = new ArrayList<>();
        Method[] methods = getClass().getDeclaredMethods();
        for (Method method : methods)
        {
            method.setAccessible(true);
            if (method.isAccessible())
            {
                if (method.getName().startsWith("get"))
                {
                    list.add(method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4));
                }
                else if (method.getName().startsWith("is"))
                {
                    list.add(method.getName().substring(2, 3).toLowerCase() + method.getName().substring(3));
                }
            }
        }
        if (list.size() == 0)
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);
                if (field.isAccessible())
                {
                    list.add(field.getName());
                }
            }
        }
        return list;
    }

    /**
     * Reflection helper: get the current (default) value of a field (either from a getter, or a field directly)
     */
    public Object getDefaultValue(String value)
    {
        Method[] methods = getClass().getDeclaredMethods();
        Object result = null;
        boolean foundMethod = false;
        for (Method method : methods)
        {
            String getMethod = "get" + value.substring(0, 1).toUpperCase() + value.substring(1);
            String isMethod = "is" + value.substring(0, 1).toUpperCase() + value.substring(1);
            if (method.getName().equals(getMethod) || method.getName().equals(isMethod))
            {
                Class noParams[] = {};
                try
                {
                    result = method.invoke(this, (Object[])noParams);
                }
                catch (IllegalAccessException ignored)
                {
                }
                catch (InvocationTargetException ignored)
                {
                }
                foundMethod = true;
            }
        }
        if (!foundMethod)
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                try
                {
                    result = field.get(this);
                }
                catch (IllegalAccessException ignored)
                {
                }
            }
        }
        return result;
    }

    /**
     * Reflection helper: overwrite fields using custom settings
     */
    public void applyCustomSettings(AppConfigStorageItem item)
    {
        Method[] methods = getClass().getDeclaredMethods();
        boolean foundSetter = false;
        for (Method method : methods)
        {
            method.setAccessible(true);
            if (method.isAccessible())
            {
                if (method.getName().startsWith("set") && method.getGenericParameterTypes().length == 1)
                {
                    String value = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                    if (item.get(value) != null)
                    {
                        try
                        {
                            Type parameterType = method.getGenericParameterTypes()[0];
                            if (parameterType.equals(Boolean.class))
                            {
                                method.invoke(this, item.getBoolean(value));
                            }
                            else if (parameterType.equals(Integer.class))
                            {
                                method.invoke(this, item.getInt(value));
                            }
                            else if (parameterType.equals(Long.class))
                            {
                                method.invoke(this, item.getLong(value));
                            }
                            else if (parameterType.equals(String.class))
                            {
                                method.invoke(this, item.getStringNotNull(value));
                            }
                            else if (parameterType instanceof Class && ((Class)parameterType).isEnum())
                            {
                                Object enumTypes[] = ((Class)parameterType).getEnumConstants();
                                String stringValue = item.getStringNotNull(value);
                                for (Object enumType : enumTypes)
                                {
                                    if (enumType.toString().equals(stringValue))
                                    {
                                        method.invoke(this, enumType);
                                        break;
                                    }
                                }
                            }
                        }
                        catch (InvocationTargetException ignored)
                        {
                        }
                        catch (IllegalAccessException ignored)
                        {
                        }
                    }
                    foundSetter = true;
                }
            }
        }
        if (!foundSetter)
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);
                if (field.isAccessible() && item.get(field.getName()) != null)
                {
                    try
                    {
                        Type parameterType = field.getGenericType();
                        if (parameterType.equals(Boolean.class))
                        {
                            field.setBoolean(this, item.getBoolean(field.getName()));
                        }
                        else if (parameterType.equals(Integer.class))
                        {
                            field.setInt(this, item.getInt(field.getName()));
                        }
                        else if (parameterType.equals(Long.class))
                        {
                            field.setLong(this, item.getLong(field.getName()));
                        }
                        else if (parameterType.equals(String.class))
                        {
                            field.set(this, item.getStringNotNull(field.getName()));
                        }
                        else if (parameterType instanceof Class && ((Class)parameterType).isEnum())
                        {
                            Object enumTypes[] = ((Class)parameterType).getEnumConstants();
                            String stringValue = item.getStringNotNull(field.getName());
                            for (Object enumType : enumTypes)
                            {
                                if (enumType.toString().equals(stringValue))
                                {
                                    field.set(this, enumType);
                                    break;
                                }
                            }
                        }
                    }
                    catch (IllegalAccessException ignored)
                    {
                    }
                }
            }
        }
    }
}