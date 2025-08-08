[DefaultKeySizeConstraintTest.java](https://github.com/apache/accumulo/blob/47ac68d1a220a90bc80618f9684252b243df6b27/core/src/test/java/org/apache/accumulo/core/constraints/DefaultKeySizeConstraintTest.java#L35)

```java
  @Test // Original test method
  public void testConstraint() {

    // pass constraints
    Mutation m = new Mutation("rowId");
    m.put("colf", "colq", new Value(new byte[] {}));
    assertEquals(Collections.emptyList(), constraint.check(null, m));

    // test with row id > 1mb
    m = new Mutation(oversized);
    m.put("colf", "colq", new Value(new byte[] {}));
    assertEquals(
        Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION),
        constraint.check(null, m));

    // test with colf > 1mb
    m = new Mutation("rowid");
    m.put(new Text(oversized), new Text("colq"), new Value(new byte[] {}));
    assertEquals(
        Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION),
        constraint.check(null, m));

    // test with colf > 1mb
    m = new Mutation("rowid");
    m.put(new Text(oversized), new Text("colq"), new Value(new byte[] {}));
    assertEquals(
        Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION),
        constraint.check(null, m));

    // test sum of smaller sizes violates 1mb constraint
    m = new Mutation(large);
    m.put(new Text(large), new Text(large), new Value(new byte[] {}));
    assertEquals(
        Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION),
        constraint.check(null, m));
  }

// ---------------- REFACTORING: ALTERNATIVE 1 --------

  @Test
  public void testConstraint_empty() {
    // pass constraints
    Mutation m = new Mutation("rowId");
    m.put("colf", "colq", new Value(new byte[] {}));

    assertEquals(Collections.emptyList(), constraint.check(null, m));
  }

  @Test
  public void testConstraint_rowIdOversized() {
    // test with row id > 1mb
    Mutation m = new Mutation(oversized);
    m.put("colf", "colq", new Value(new byte[] {}));

    assertEquals(
        Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION),
        constraint.check(null, m));
  }
  
  @Test
  public void testConstraint_colfOversized() {
    // test with colf > 1mb
    Mutation m = new Mutation("rowid");
    m.put(new Text(oversized), new Text("colq"), new Value(new byte[] {}));

    assertEquals(
        Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION),
        constraint.check(null, m));
  }
  
  @Test
  public void testConstraint_sumSizesViolates() {
    // test sum of smaller sizes violates 1mb constraint
    m = new Mutation(large);
    m.put(new Text(large), new Text(large), new Value(new byte[] {}));

    assertEquals(
        Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION),
        constraint.check(null, m));
  }
```
**It works, however Lazy Test Smell is introduced, once constraint.check is being used by different test methods. Another alternative is to use Parameterized tests.** 

```java
// ALTERNATIVE 2 - PARAMETERIZED CLASS
@RunWith(Parameterized.class)
public class DefaultKeySizeConstraintTest {
    ...

    private final Mutation mutation;
    private final List<Integer> expected;

    public DefaultKeySizeConstraintTest(Mutation mutation, List<Integer> expected) {
        this.mutation = mutation;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // pass constraints
            { createMutation("rowId", "colf", "colq", new byte[]{}, false), Collections.emptyList() },
            // row id > 1mb
            { createMutation(oversized, "colf", "colq", new byte[]{}, false),
              Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION) },
            // colf > 1mb
            { createMutation("rowid", oversized, "colq", new byte[]{}, true),
              Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION) },
            // sum of smaller sizes violates 1mb constraint
            { createMutation(large, large, large, new byte[]{}, true),
              Collections.singletonList(DefaultKeySizeConstraint.MAX__KEY_SIZE_EXCEEDED_VIOLATION) }
        });
    }

    @Test // Test method just uses parameterized values
    public void testConstraint() {
        assertEquals(expected, constraint.check(null, mutation));
    }

    private static Mutation createMutation(Object row, Object colf, Object colq, byte[] value, boolean useText) {
        Mutation m = (row instanceof String) ? new Mutation((String) row) : new Mutation((byte[]) row);
        if (useText) {
            m.put(new Text((byte[]) colf), new Text((byte[]) colq), new Value(value));
        } else {
            m.put((String) colf, (String) colq, new Value(value));
        }
        return m;
    }
}
```