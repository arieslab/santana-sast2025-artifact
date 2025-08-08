[OpTimerTest.java](https://github.com/apache/accumulo/blob/47ac68d1a220a90bc80618f9684252b243df6b27/core/src/test/java/org/apache/accumulo/core/util/OpTimerTest.java#L121)
```java
    // Original test method
    @Test(expected = IllegalStateException.class)
    public void verifyExceptionCallingStopWhenNotRunning() {

        OpTimer timer = new OpTimer().start();

        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            log.info("sleep sleep interrupted");
            Thread.currentThread().interrupt();
        }

        assertTrue(timer.isRunning());

        timer.stop();

        assertFalse(timer.isRunning());

        // should throw exception
        timer.stop();
    }

    /* --------------- REFACTORING --------------- */
    // Refactored
    @Test(expected = IllegalStateException.class)
    public void verifyExceptionCallingStopWhenNotRunning() {
        OpTimer timer = new OpTimer().start();

        assertTrue(timer.isRunning());

        timer.stop();

        assertFalse(timer.isRunning());

        // should throw exception
        timer.stop();
    }
```