[TestDbTxnManager.java](https://github.com/apache/hive/blob/5160d3af392248255f68e41e1e0557eae4d95273/ql/src/test/org/apache/hadoop/hive/ql/lockmgr/TestDbTxnManager.java#L218)
```java
 @Test // Original test method
  public void testExceptions() throws Exception {
    addPartitionOutput(newTable(true), WriteEntity.WriteType.INSERT);
    QueryPlan qp = new MockQueryPlan(this);
    ((DbTxnManager) txnMgr).openTxn(ctx, "NicholasII", HiveConf.getTimeVar(conf, HiveConf.ConfVars.HIVE_TXN_TIMEOUT, TimeUnit.MILLISECONDS) * 2);
    Thread.sleep(HiveConf.getTimeVar(conf, HiveConf.ConfVars.HIVE_TXN_TIMEOUT, TimeUnit.MILLISECONDS));
    runReaper();
    LockException exception = null;
    try {
      txnMgr.commitTxn();
    }
    catch(LockException ex) {
      exception = ex;
    }
    Assert.assertNotNull("Expected exception1", exception);
    Assert.assertEquals("Wrong Exception1", ErrorMsg.TXN_ABORTED, exception.getCanonicalErrorMsg());

    exception = null;
    ((DbTxnManager) txnMgr).openTxn(ctx, "AlexanderIII", HiveConf.getTimeVar(conf, HiveConf.ConfVars.HIVE_TXN_TIMEOUT, TimeUnit.MILLISECONDS) * 2);
    Thread.sleep(HiveConf.getTimeVar(conf, HiveConf.ConfVars.HIVE_TXN_TIMEOUT, TimeUnit.MILLISECONDS));
    runReaper();//this will abort the txn
    TxnStore txnHandler = TxnUtils.getTxnStore(conf);
    GetOpenTxnsInfoResponse txnsInfo = txnHandler.getOpenTxnsInfo();
    assertEquals(2, txnsInfo.getTxn_high_water_mark());
    assertEquals(2, txnsInfo.getOpen_txns().size());
    Assert.assertEquals(TxnState.ABORTED, txnsInfo.getOpen_txns().get(1).getState());
    txnMgr.rollbackTxn();//this is idempotent
  }

  /* --------------- REFACTORING --------------- */

  @Test // Refactored test method
  public void testExceptions() throws Exception {
    // set a short timeout (10ms) to avoid Thread.sleep
    conf.setTimeVar(HiveConf.ConfVars.HIVE_TXN_TIMEOUT, TIMEOUT_10_MS, TimeUnit.MILLISECONDS);

    addPartitionOutput(newTable(true), WriteEntity.WriteType.INSERT);
    QueryPlan qp = new MockQueryPlan(this);
    ((DbTxnManager) txnMgr).openTxn(ctx, "NicholasII", DELAY_20_MS);
    
    runReaper();
    
    LockException exception = null;
    try {
      txnMgr.commitTxn();
    }
    catch(LockException ex) {
      exception = ex;
    }
    
    Assert.assertNotNull("Expected exception1", exception);
    Assert.assertEquals("Wrong Exception1", ErrorMsg.TXN_ABORTED, exception.getCanonicalErrorMsg());
  }

  @Test // Extracted test method
  public void testExceptions_stateAborted() throws Exception {
    // set a short timeout (10ms) to avoid Thread.sleep
    conf.setTimeVar(HiveConf.ConfVars.HIVE_TXN_TIMEOUT, TIMEOUT_10_MS, TimeUnit.MILLISECONDS);
    
    addPartitionOutput(newTable(true), WriteEntity.WriteType.INSERT);
    QueryPlan qp = new MockQueryPlan(this);

    // open the transaction with a delay > timeout
    ((DbTxnManager) txnMgr).openTxn(ctx, "AlexanderIII", DELAY_20_MS);
    
    runReaper();//this will abort the txn
    
    TxnStore txnHandler = TxnUtils.getTxnStore(conf);
    GetOpenTxnsInfoResponse txnsInfo = txnHandler.getOpenTxnsInfo();
    assertEquals(2, txnsInfo.getTxn_high_water_mark());
    assertEquals(2, txnsInfo.getOpen_txns().size());
    Assert.assertEquals(TxnState.ABORTED, txnsInfo.getOpen_txns().get(1).getState());
    txnMgr.rollbackTxn();//this is idempotent
  }
```