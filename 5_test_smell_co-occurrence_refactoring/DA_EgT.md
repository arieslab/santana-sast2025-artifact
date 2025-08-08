[FormatterConfigTest.java](https://github.com/apache/accumulo/blob/47ac68d1a220a90bc80618f9684252b243df6b27/core/src/test/java/org/apache/accumulo/core/util/format/FormatterConfigTest.java#L37)

```java
  @Test // Original test method
  public void testSetShownLength() throws Exception {
    FormatterConfig config = new FormatterConfig();
    try {
      config.setShownLength(-1);
      fail("Should throw on negative length.");
    } catch (IllegalArgumentException e) {}

    config.setShownLength(0);
    assertEquals(0, config.getShownLength());
    assertEquals(true, config.willLimitShowLength());

    config.setShownLength(1);
    assertEquals(1, config.getShownLength());
    assertEquals(true, config.willLimitShowLength());
  }

  // ---------------- REFACTORING ------------------------
  
  @Test // split 1
  public void testSetShownLength_negativeLength() throws Exception {
    FormatterConfig config = new FormatterConfig();
    try {
      config.setShownLength(-1);
      fail("Should throw on negative length.");
    } catch (IllegalArgumentException e) {}
  }

  @Test // split 2
  public void testSetShownLength_nonNegativeLength() throws Exception {
    FormatterConfig config = new FormatterConfig();
    config.setShownLength(0);

    assertEquals(0, config.getShownLength());

    // optional validation
    config.setShownLength(1);
    assertEquals(1, config.getShownLength());
  }

  @Test // split 3
  public void testSetShownLength_willLimitShowLength() throws Exception {
    FormatterConfig config = new FormatterConfig();

    config.setShownLength(1);

    assertEquals(true, config.willLimitShowLength());
  }
```
