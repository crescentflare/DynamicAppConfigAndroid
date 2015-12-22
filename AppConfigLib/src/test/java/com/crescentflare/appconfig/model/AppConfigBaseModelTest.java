package com.crescentflare.appconfig.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Unit test: base model
 */
public class AppConfigBaseModelTest
{
    @Test
    public void testValueListForFields()
    {
        DerivedModelWithFields model = new DerivedModelWithFields();
        String[] expectedValues = { "simpleEnumField", "complexEnumField", "stringField", "longField", "intField", "boolField" };
        String[] modelValues = model.valueList().toArray(new String[0]);
        Arrays.sort(expectedValues);
        Arrays.sort(modelValues);
        Assert.assertArrayEquals(expectedValues, modelValues);
    }

    @Test
    public void testValueListForMethods()
    {
        DerivedModelWithMethods model = new DerivedModelWithMethods();
        String[] expectedValues = { "simpleEnumField", "complexEnumField", "stringField", "longField", "intField", "boolField" };
        String[] modelValues = model.valueList().toArray(new String[0]);
        Arrays.sort(expectedValues);
        Arrays.sort(modelValues);
        Assert.assertArrayEquals(expectedValues, modelValues);
    }

    @Test
    public void testGetCurrentValueForFields()
    {
        DerivedModelWithFields model = new DerivedModelWithFields();
        model.simpleEnumField = SimpleEnum.Value;
        model.complexEnumField = ComplexEnum.Test;
        model.stringField = "Test string";
        model.longField = -3948;
        model.intField = 834892;
        model.boolField = true;
        Assert.assertEquals(SimpleEnum.Value, model.getCurrentValue("simpleEnumField"));
        Assert.assertEquals(ComplexEnum.Test, model.getCurrentValue("complexEnumField"));
        Assert.assertEquals("Test string", model.getCurrentValue("stringField"));
        Assert.assertEquals((long)-3948, model.getCurrentValue("longField"));
        Assert.assertEquals(834892, model.getCurrentValue("intField"));
        Assert.assertEquals(true, model.getCurrentValue("boolField"));
    }

    @Test
    public void testGetCurrentValueForMethods()
    {
        DerivedModelWithMethods model = new DerivedModelWithMethods();
        model.setSimpleEnumField(SimpleEnum.Name);
        model.setComplexEnumField(ComplexEnum.Application);
        model.setStringField("Another test");
        model.setLongField(92345);
        model.setIntField(-234);
        model.setBoolField(false);
        Assert.assertEquals(SimpleEnum.Name, model.getCurrentValue("simpleEnumField"));
        Assert.assertEquals(ComplexEnum.Application, model.getCurrentValue("complexEnumField"));
        Assert.assertEquals("Another test", model.getCurrentValue("stringField"));
        Assert.assertEquals((long)92345, model.getCurrentValue("longField"));
        Assert.assertEquals(-234, model.getCurrentValue("intField"));
        Assert.assertEquals(false, model.getCurrentValue("boolField"));
    }

    @Test
    public void testApplyCustomSettingsForFields()
    {
        DerivedModelWithFields model = new DerivedModelWithFields();
        AppConfigStorageItem overrides = new AppConfigStorageItem();
        overrides.putString("simpleEnumField", SimpleEnum.Simple.toString());
        overrides.putString("complexEnumField", ComplexEnum.EnumValues.toString());
        overrides.putString("stringField", "Override test");
        overrides.putLong("longField", 83480);
        overrides.putInt("intField", -19028234);
        overrides.putBoolean("boolField", false);
        model.applyCustomSettings("", overrides);
        Assert.assertEquals(SimpleEnum.Simple, model.simpleEnumField);
        Assert.assertEquals(ComplexEnum.EnumValues, model.complexEnumField);
        Assert.assertEquals("Override test", model.stringField);
        Assert.assertEquals((long)83480, model.longField);
        Assert.assertEquals(-19028234, model.intField);
        Assert.assertEquals(false, model.boolField);
    }

    @Test
    public void testApplyCustomSettingsForMethods()
    {
        DerivedModelWithMethods model = new DerivedModelWithMethods();
        AppConfigStorageItem overrides = new AppConfigStorageItem();
        overrides.putString("simpleEnumField", SimpleEnum.Value.toString());
        overrides.putString("complexEnumField", ComplexEnum.Application.toString());
        overrides.putString("stringField", "Another override test");
        overrides.putLong("longField", -234908);
        overrides.putInt("intField", 908345);
        overrides.putBoolean("boolField", true);
        model.applyCustomSettings("", overrides);
        Assert.assertEquals(SimpleEnum.Value, model.getSimpleEnumField());
        Assert.assertEquals(ComplexEnum.Application, model.getComplexEnumField());
        Assert.assertEquals("Another override test", model.getStringField());
        Assert.assertEquals((long)-234908, model.getLongField());
        Assert.assertEquals(908345, model.getIntField());
        Assert.assertEquals(true, model.isBoolField());
    }

    /**
     * A simple enum
     */
    public enum SimpleEnum
    {
        Simple,
        Value,
        Name;
    }

    /**
     * A more complex enum which has string serialization
     */
    public enum ComplexEnum
    {
        Unknown(""),
        Test("test"),
        Application("application"),
        EnumValues("enumValues");

        private String text;

        ComplexEnum(String text)
        {
            this.text = text;
        }

        public static ComplexEnum fromString(String text)
        {
            if (text != null)
                for (ComplexEnum e : ComplexEnum.values())
                    if (text.equalsIgnoreCase(e.text))
                        return e;
            return Unknown;
        }

        public String toString()
        {
            return text;
        }
    }

    /**
     * A derived model using public fields
     */
    public static class DerivedModelWithFields extends AppConfigBaseModel
    {
        public SimpleEnum simpleEnumField = SimpleEnum.Simple;
        public ComplexEnum complexEnumField = ComplexEnum.Unknown;
        public String stringField = "";
        public long longField = 0;
        public int intField = 0;
        public boolean boolField = false;
    }

    /**
     * A derived model using getters and setters
     */
    public static class DerivedModelWithMethods extends AppConfigBaseModel
    {
        private SimpleEnum simpleEnumField = SimpleEnum.Simple;
        private ComplexEnum complexEnumField = ComplexEnum.Unknown;
        private String stringField = "";
        private long longField = 0;
        private int intField = 0;
        private boolean boolField = false;

        public SimpleEnum getSimpleEnumField()
        {
            return simpleEnumField;
        }

        public void setSimpleEnumField(SimpleEnum simpleEnumField)
        {
            this.simpleEnumField = simpleEnumField;
        }

        public ComplexEnum getComplexEnumField()
        {
            return complexEnumField;
        }

        public void setComplexEnumField(ComplexEnum complexEnumField)
        {
            this.complexEnumField = complexEnumField;
        }

        public String getStringField()
        {
            return stringField;
        }

        public void setStringField(String stringField)
        {
            this.stringField = stringField;
        }

        public long getLongField()
        {
            return longField;
        }

        public void setLongField(long longField)
        {
            this.longField = longField;
        }

        public int getIntField()
        {
            return intField;
        }

        public void setIntField(int intField)
        {
            this.intField = intField;
        }

        public boolean isBoolField()
        {
            return boolField;
        }

        public void setBoolField(boolean boolField)
        {
            this.boolField = boolField;
        }
    }
}
