[VolumeManagerImplTest.java](https://github.com/apache/accumulo/blob/47ac68d1a220a90bc80618f9684252b243df6b27/server/base/src/test/java/org/apache/accumulo/server/fs/VolumeManagerImplTest.java#L68)

```java
  @Test // Original test method
  public void tabletDirWithTableId() throws Exception {
    String basePath = fs.getDefaultVolume().getBasePath();
    String scheme = fs.getDefaultVolume().getFileSystem().getUri().toURL().getProtocol();
    System.out.println(basePath);
    Path expectedBase = new Path(scheme + ":" + basePath, FileType.TABLE.getDirectory());
    List<String> pathsToTest =
        Arrays.asList("1/default_tablet", "1/default_tablet/", "1/t-0000001");
    for (String pathToTest : pathsToTest) {
      Path fullPath = fs.getFullPath(FileType.TABLE, pathToTest);
      assertEquals(new Path(expectedBase, pathToTest), fullPath);
    }
  }

  /* --------------- REFACTORING --------------- */
  @Test // Refactored test method
  public void tabletDirWithTableId() throws Exception {
    String basePath = fs.getDefaultVolume().getBasePath();
    String scheme = fs.getDefaultVolume().getFileSystem().getUri().toURL().getProtocol();
    
    Path expectedBase = new Path(scheme + ":" + basePath, FileType.TABLE.getDirectory());
    List<String> pathsToTest =
        Arrays.asList("1/default_tablet", "1/default_tablet/", "1/t-0000001");

    List<Path> expectedPaths = getExpectedPaths(pathsToTest, expectedBase);
    List<Path> fullPaths = getFullPaths(pathsToTest);

    assertEquals(expectedPaths, fullPaths);
  }

  private List<Path> getExpectedPaths(List<String> pathsToTest, Path expectedBase) {
    ArrayList<Path> expectedPaths = new ArrayList<>();
    for (String pathToTest : pathsToTest) {
      Path expectedPath = new Path(expectedBase, pathToTest);
      expectedPaths.add(expectedPath);
    }
    return expectedPaths;   
  }

  private List<Path> getFullPaths(List<String> pathsToTest) {
    ArrayList<Path> fullPaths = new ArrayList<>();
    for (String pathToTest : pathsToTest) {
      Path fullPath = fs.getFullPath(FileType.TABLE, pathToTest);
      fullPaths.add(fullPath);
    }
    return fullPaths;
  }

```