import components.statement.Statement;
import components.statement.StatementKernel;

/**
 * Utility class with method to count the number of calls to primitive
 * instructions (move, turnleft, turnright, infect, skip) in a given
 * {@code Statement}.
 *
 * @author Ashim Dhakal
 *
 */
public final class CountPrimitiveCalls {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private CountPrimitiveCalls() {
    }

    /**
     * Reports the number of calls to primitive instructions (move, turnleft,
     * turnright, infect, skip) in a given {@code Statement}.
     *
     * @param s
     *            the {@code Statement}
     * @return the number of calls to primitive instructions in {@code s}
     * @ensures <pre>
     * countOfPrimitiveCalls =
     *  [number of calls to primitive instructions in s]
     * </pre>
     */
    public static int countOfPrimitiveCalls(Statement s) {
        int numTimes = 0;
        switch (s.kind()) {
            case BLOCK: {
                /*
                 * Add up the number of calls to primitive instructions in each
                 * nested statement in the BLOCK.
                 */

                int i = 0;
                while (i < s.lengthOfBlock()) {
                    Statement removed = s.removeFromBlock(i);
                    numTimes += countOfPrimitiveCalls(removed);
                    s.addToBlock(i, removed);
                    i++;
                }

                break;
            }
            case IF: {
                /*
                 * Find the number of calls to primitive instructions in the
                 * body of the IF.
                 */
                Statement newInstanceInitialize = s.newInstance();
                StatementKernel.Condition condition = s
                        .disassembleIf(newInstanceInitialize);

                numTimes = numTimes
                        + countOfPrimitiveCalls(newInstanceInitialize);
                s.assembleIf(condition, newInstanceInitialize);

                break;
            }
            case IF_ELSE: {
                /*
                 * Add up the number of calls to primitive instructions in the
                 * "then" and "else" bodies of the IF_ELSE.
                 */

                Statement compareFirst = s.newInstance();
                Statement compareSecond = s.newInstance();
                StatementKernel.Condition condition = s
                        .disassembleIfElse(compareFirst, compareSecond);

                numTimes = numTimes + countOfPrimitiveCalls(compareFirst);
                numTimes = numTimes + countOfPrimitiveCalls(compareSecond);

                s.assembleIfElse(condition, compareFirst, compareSecond);

                break;
            }
            case WHILE: {
                /*
                 * Find the number of calls to primitive instructions in the
                 * body of the WHILE.
                 */

                Statement newInstanceInitialization = s.newInstance();
                StatementKernel.Condition condition = s
                        .disassembleWhile(newInstanceInitialization);

                numTimes = numTimes
                        + countOfPrimitiveCalls(newInstanceInitialization);

                s.assembleWhile(condition, newInstanceInitialization);
                break;
            }
            case CALL: {
                /*
                 * This is a leaf: the count can only be 1 or 0. Determine
                 * whether this is a call to a primitive instruction or not.
                 */

                // TODO - fill in case
                String dissaseemble = s.disassembleCall();

                if (dissaseemble.equals("turnleft")
                        || dissaseemble.equals("turnright")
                        || dissaseemble.equals("move")
                        || dissaseemble.equals("infect")
                        || dissaseemble.equals("skip")) {
                    numTimes = numTimes + 1;
                }

                s.assembleCall(dissaseemble);

                break;
            }
            default: {
                // this will never happen...can you explain why?
                break;
            }
        }
        return numTimes;
    }

}
