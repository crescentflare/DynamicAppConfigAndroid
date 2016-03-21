package com.crescentflare.appconfig.model;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        //Gather items
        ArrayList<ModelValue> list = new ArrayList<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields)
        {
            if (field.getName().substring(0, 1).equals("$"))
            {
                continue;
            }
            String addValue = null;
            if (Modifier.isPublic(field.getModifiers()))
            {
                addValue = field.getName();
            }
            else
            {
                String findMethod = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                boolean foundSetter = false;
                boolean foundGetter = false;
                for (Method method : getClass().getDeclaredMethods())
                {
                    if (method.getName().equals("get" + findMethod) || method.getName().equals("is" + findMethod))
                    {
                        foundGetter = true;
                    }
                    if (method.getName().equals("set" + findMethod))
                    {
                        foundSetter = true;
                    }
                    if (foundGetter && foundSetter)
                    {
                        addValue = field.getName();
                        break;
                    }
                }
            }
            if (addValue != null)
            {
                ModelValue modelValue = new ModelValue();
                modelValue.value = addValue;
                for (Annotation annotation : field.getDeclaredAnnotations())
                {
                    if (annotation instanceof AppConfigModelSort)
                    {
                        modelValue.sortIndex = ((AppConfigModelSort)annotation).value();
                        break;
                    }
                }
                list.add(modelValue);
            }
        }

        //Sort
        Collections.sort(list, new Comparator<ModelValue>()
        {
            @Override
            public int compare(ModelValue v1, ModelValue v2)
            {
                return v1.sortIndex - v2.sortIndex;
            }
        });

        //Extract strings and return
        ArrayList<String> stringValues = new ArrayList<>();
        for (ModelValue listItem : list)
        {
            stringValues.add(listItem.value);
        }
        return stringValues;
    }

    /**
     * Reflection helper: get the list of categories (if some items don't have any category, an empty string is added)
     */
    public ArrayList<String> getCategories()
    {
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<String> values = valueList();
        boolean foundUnCategorized = false;
        for (String value : values)
        {
            if (value.equals("name"))
            {
                continue;
            }
            try
            {
                Field field = getClass().getDeclaredField(value);
                boolean hasCategory = false;
                for (Annotation annotation : field.getDeclaredAnnotations())
                {
                    if (annotation instanceof AppConfigModelCategory)
                    {
                        String category = ((AppConfigModelCategory)annotation).value();
                        if (category.length() > 0)
                        {
                            if (!categories.contains(category))
                            {
                                categories.add(category);
                            }
                            hasCategory = true;
                            break;
                        }
                    }
                }
                if (!hasCategory)
                {
                    foundUnCategorized = true;
                }
            }
            catch (NoSuchFieldException ignored)
            {
            }
        }
        if (foundUnCategorized && categories.size() > 0)
        {
            categories.add("");
        }
        return categories;
    }

    /**
     * Reflection helper: check if the value belongs to the given category
     */
    public boolean valueBelongsToCategory(String value, String category)
    {
        for (Field field : getClass().getDeclaredFields())
        {
            if (field.getName().equals(value))
            {
                for (Annotation annotation : field.getDeclaredAnnotations())
                {
                    if (annotation instanceof AppConfigModelCategory)
                    {
                        String checkCategory = ((AppConfigModelCategory)annotation).value();
                        return checkCategory.equals(category);
                    }
                }
                return category.length() == 0;
            }
        }
        return false;
    }

    /**
     * Reflection helper: get the current (or default) value of a field (either from a getter, or a field directly)
     */
    public Object getCurrentValue(String value)
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
                    if (field.getName().equals(value))
                    {
                        result = field.get(this);
                        break;
                    }
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
    public void applyCustomSettings(String configName, AppConfigStorageItem item)
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
                    if (value.equals("name"))
                    {
                        try
                        {
                            Type parameterType = method.getGenericParameterTypes()[0];
                            if (parameterType.equals(String.class) && configName.length() > 0)
                            {
                                method.invoke(this, configName);
                            }
                        }
                        catch (InvocationTargetException ignored)
                        {
                        }
                        catch (IllegalAccessException ignored)
                        {
                        }
                    }
                    else if (item.get(value) != null)
                    {
                        try
                        {
                            Type parameterType = method.getGenericParameterTypes()[0];
                            if (parameterType.equals(Boolean.class) || parameterType.equals(boolean.class))
                            {
                                method.invoke(this, item.getBoolean(value));
                            }
                            else if (parameterType.equals(Integer.class) || parameterType.equals(int.class))
                            {
                                method.invoke(this, item.getInt(value));
                            }
                            else if (parameterType.equals(Long.class) || parameterType.equals(long.class))
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
                if (field.isAccessible())
                {
                    if (field.getName().equals("name"))
                    {
                        try
                        {
                            Type parameterType = field.getGenericType();
                            if (parameterType.equals(String.class) && configName.length() > 0)
                            {
                                field.set(this, configName);
                            }
                        }
                        catch (IllegalAccessException ignored)
                        {
                        }
                    }
                    if (item.get(field.getName()) != null)
                    {
                        try
                        {
                            Type parameterType = field.getGenericType();
                            if (parameterType.equals(Boolean.class) || parameterType.equals(boolean.class))
                            {
                                field.setBoolean(this, item.getBoolean(field.getName()));
                            }
                            else if (parameterType.equals(Integer.class) || parameterType.equals(int.class))
                            {
                                field.setInt(this, item.getInt(field.getName()));
                            }
                            else if (parameterType.equals(Long.class) || parameterType.equals(long.class))
                            {
                                field.setLong(this, item.getLong(field.getName()));
                            }
                            else if (parameterType.equals(String.class))
                            {
                                field.set(this, item.getStringNotNull(field.getName()));
                            }
                            else if (parameterType instanceof Class && ((Class) parameterType).isEnum())
                            {
                                Object enumTypes[] = ((Class) parameterType).getEnumConstants();
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

    /**
     * Helper object
     */
    private static class ModelValue
    {
        public String value = "";
        public int sortIndex = 0;
    }
}
