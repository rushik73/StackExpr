


import java.util.Scanner;
import java.util.Stack;

public class StackExpr {
    public static void main(String[] args) {
        String equation1 = "( ( 2 + 3 ) / 5 ) - 1";
        System.out.printf("Test 1: '%s'\n", equation1);
        PostFix p1 = new PostFix(equation1);
        System.out.println("Postfix form: " + p1.expression);
        System.out.println("Result: " + p1.solve());

        String equation2 = "( ( -2 + 7 ) / 3 ) * 17";
        System.out.printf("Test 2: '%s'\n", equation2);
        PostFix p2 = new PostFix(equation2);
        System.out.println("Postfix form: " + p2.expression);
        System.out.println("Result: " + p2.solve());
    }
}

enum Operator {
    ADD(1), SUB(1), MUL(2), DIV(2), LPAREN(3), RPAREN(3);

    public final int priority;

    Operator(int priority) {
        this.priority = priority;
    }

    int operate(int l, int r) {
        switch (this) {
            case ADD:
                return l + r;
            case SUB:
                return l - r;
            case MUL:
                return l * r;
            case DIV:
                return l / r;
            case LPAREN:
            case RPAREN:
            default:
                throw new IllegalArgumentException("Cannot perform operation on parenthesis");
        }
    }

    static Operator intoOperator(char c) {
        ;
        switch (c) {
            case '+':
                return Operator.ADD;
            case '-':
                return Operator.SUB;
            case '*':
                return Operator.MUL;
            case '/':
                return Operator.DIV;
            case '(':
                return Operator.LPAREN;
            case ')':
                return Operator.RPAREN;
            default:
                throw new IllegalArgumentException(String.format("'%c' is not a known operand", c));
        }
    }

    static boolean isOperator(char c) {
        try {
            intoOperator(c);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

class PostFix {
    Stack<Object> expression;

    /// Solves the expression
    int solve() {
        Stack<Integer> result = new Stack<>();
        for (Object symbol : this.expression) {
            if (symbol instanceof Integer)
                result.push((Integer) symbol);
            else if (symbol instanceof Operator) {
                Operator operator = (Operator) symbol;
                int r = result.pop();
                int l = result.pop();
                int res = operator.operate(l, r);
                result.push(res);
            } else
                throw new IllegalArgumentException("Invalid symbol '" + symbol + "' found in the expression stack'");
        }

        if (result.size() != 1) {
            throw new IllegalArgumentException(
                    String.format("Could not solve expression '%s' correctly, resulted in: '%s'", expression, result));
        }
        return result.get(0);
    }

    PostFix(String infix) {
        expression = new Stack<>();
        Scanner scanner = new Scanner(infix);
        Stack<Operator> operStack = new Stack<>();

        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.length() == 1 && Operator.isOperator(token.charAt(0))) {
                Operator operator = Operator.intoOperator(token.charAt(0));

                switch (operator) {
                    case LPAREN:
                        operStack.push(operator);
                        break;
                    case RPAREN:
                        Operator symbol;
                        do {
                            symbol = operStack.pop();
                            if (symbol == Operator.LPAREN)
                                break;
                            else
                                expression.push(symbol);
                        } while (!operStack.isEmpty());
                        if (symbol != Operator.LPAREN)
                            throw new IllegalArgumentException(
                                    String.format("Parenthesis imbalance in '%s'", infix));
                        break;
                    case ADD:
                    case SUB:
                    case MUL:
                    case DIV:
                        while (!operStack.isEmpty()) {
                            Operator opFromStack = operStack.peek();
                            if (opFromStack != Operator.LPAREN && opFromStack.priority >= operator.priority) {
                                operStack.pop();
                                expression.push(opFromStack);
                            } else
                                break;
                        }
                        operStack.push(operator);
                        break;
                }
            } else {
                int operand = Integer.parseInt(token);
                expression.push(operand);
            }
        }
        while (!operStack.isEmpty()) {
            expression.push(operStack.pop());
        }
        scanner.close();
    }
}
