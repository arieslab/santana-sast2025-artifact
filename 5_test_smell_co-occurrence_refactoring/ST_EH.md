[AutoRecoveryMainTest.java](https://github.com/apache/bookkeeper/blob/f233320077991b4b50218598858f6d31a1914884/bookkeeper-server/src/test/java/org/apache/bookkeeper/replication/AutoRecoveryMainTest.java#L51C4-L64C6)

```java
    @Test // Original test method
    public void testStartup() throws Exception {
        AutoRecoveryMain main = new AutoRecoveryMain(confByIndex(0));
        try {
            main.start();
            Thread.sleep(500);
            assertTrue("AuditorElector should be running",
                    main.auditorElector.isRunning());
            assertTrue("Replication worker should be running",
                    main.replicationWorker.isRunning());
        } finally {
            main.shutdown();
        }
    }

    /* --------------- REFACTORING --------------- */

    AutoRecoveryMain main;
    ...
    @Test
    public void testStartup() throws Exception {
        main = new AutoRecoveryMain(confByIndex(0));
        main.start();

        // replaced with Awaitility
        // given the test runs on top of async components 
        await().atMost(500, TimeUnit.MILLISECONDS)
            .pollInterval(10, TimeUnit.MILLISECONDS)
            .until(() -> main.auditorElector.isRunning() && main.replicationWorker.isRunning());

        assertTrue("AuditorElector should be running", main.auditorElector.isRunning());
        assertTrue("Replication worker should be running", main.replicationWorker.isRunning());
    }

    @After
    public void tearDown() throws Exception {
        main.shutdown();
    }
```
