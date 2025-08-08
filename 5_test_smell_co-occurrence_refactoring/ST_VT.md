[BatchedArrayBlockingQueueTest.java](https://github.com/apache/bookkeeper/blob/f233320077991b4b50218598858f6d31a1914884/bookkeeper-common/src/test/java/org/apache/bookkeeper/common/collections/BatchedArrayBlockingQueueTest.java#L95)
```java
    @Test // Original test method
    public void blockingTake() throws Exception {
        BlockingQueue<Integer> queue = new GrowableMpScArrayConsumerBlockingQueue<>();

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                int expected = 0;

                for (int i = 0; i < 100; i++) {
                    int n = queue.take();

                    assertEquals(expected++, n);
                }

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        int n = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                queue.put(n);
                ++n;
            }

            // Wait until all the entries are consumed
            while (!queue.isEmpty()) {
                Thread.sleep(1);
            }
        }

        latch.await();
    }

    /* --------------- REFACTORING --------------- */

    @Test // Refactored - original test method
    public void blockingTake() throws Exception {
        BlockingQueue<Integer> queue = new GrowableMpScArrayConsumerBlockingQueue<>();

        CountDownLatch latch = new CountDownLatch(1);

        takeValues(queue, latch);
        putValues(queue);

        latch.await();
    }

    // NEW - method to take values
    public void takeValues(BlockingQueue<Integer> queue, CountDownLatch latch) {
        new Thread(() -> {
            try {
                int expected = 0;

                for (int i = 0; i < 100; i++) {
                    int n = queue.take();

                    assertEquals(expected++, n);
                }

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // NEW - method to put values
    public void putValues(BlockingQueue<Integer> queue) {
        int n = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                queue.put(n);
                ++n;
            }

            // replace Thread.sleep with Awaitility
            // given the test requires multi-thread communication
            await().atMost(100, TimeUnit.MILLISECONDS)
                .pollInterval(1, TimeUnit.MILLISECONDS)
                .until(queue::isEmpty);
        }
    }
```