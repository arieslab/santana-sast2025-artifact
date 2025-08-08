[TestAnalyzeDepMgt.java](https://github.com/apache/maven-dependency-plugin/blob/e8c1a621f22f9d3b379217f7dec9596867b0a76f/src/test/java/org/apache/maven/plugins/dependency/analyze/TestAnalyzeDepMgt.java#L153)

```java
    // Original test method
    public void testAddExclusions() {

        assertEquals(0, mojo.addExclusions(null).size());

        List<Exclusion> list = new ArrayList<>();
        list.add(ex);
        Map<String, Exclusion> map = mojo.addExclusions(list);

        assertEquals(1, map.size());
        assertTrue(map.containsKey(mojo.getExclusionKey(ex)));
        assertSame(ex, map.get(mojo.getExclusionKey(ex)));
    }

    /* --------------- REFACTORING --------------- */
    // refactored test method
    public void testAddExclusions() {
        int expectedEmptySize = 0;
        int expectedAddedExclusions = 1;

        assertEquals(expectedEmptySize, mojo.addExclusions(null).size());

        List<Exclusion> list = new ArrayList<>();
        list.add(ex);

        Map<String, Exclusion> map = mojo.addExclusions(list);

        assertEquals(expectedAddedExclusions, map.size());
    }

    // New test method
    public void testGetExclusionKey() {
        String expectedKey = getTestExclusionKey();
        String actualKey = mojo.getExclusionKey(ex);
        
        assertEquals(expectedKey, actualKey);
    }

    public String getTestExclusionKey() {
        return ex.getGroupId() + ":" + ex.getArtifactId();
    }
```