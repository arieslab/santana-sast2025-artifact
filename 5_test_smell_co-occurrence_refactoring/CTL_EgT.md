[EnumTypeTest.java](https://github.com/apache/cayenne/blob/7e156078c7a11372d64f578cbc676638adf67db5/cayenne-server/src/test/java/org/apache/cayenne/access/types/EnumTypeTest.java#L33)

```java
    @Test // Original test method
    public void testConstructor() throws Exception {
        EnumType type = new EnumType(MockEnum.class);
        assertEquals(MockEnum.class.getName(), type.getClassName());
        assertEquals(MockEnum.values().length, type.values.length);
        
        for(int i = 0; i < MockEnum.values().length; i++) {
            assertSame(MockEnum.values()[i], type.values[i]);
        }
    }

    // ---------------- REFACTORING ------------------------
    @Test
    public void testConstructor_getClassName() throws Exception {
        EnumType type = new EnumType(MockEnum.class);

        assertEquals(MockEnum.class.getName(), type.getClassName());
    }

    @Test
    public void testConstructor_values() throws Exception {
        EnumType type = new EnumType(MockEnum.class);

        assertEquals(MockEnum.values().length, type.values.length);
        
        assertArrayEquals(MockEnum.values(), type.values);
    }
```
