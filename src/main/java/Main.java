import java.math.BigDecimal;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        final String expression = "2.5+2.5+(2.5+2.5)*2.5/2+(2+(2+2))";
//        final String expression = "2.5+2";
        try {
            System.out.println(expressionParser(expression));
        } catch (Exception e) {
            System.err.println("Expression is not well formed");
            throw e;
        }
    }
    private static String expressionParser(String expression) {
        StringBuilder strBuilder = new StringBuilder(expression.replaceAll("\\s", ""));

        Stack<Integer> stack = new Stack();

        for(int i = 0; i < strBuilder.length(); i++) {
            if(isOpenParenthesis(strBuilder.charAt(i))) {
                stack.push(i);
            } else if(isCloseParenthesis(strBuilder.charAt(i))) {
                int beginning = stack.pop();
                if(stack.isEmpty()) {
                    int exprStart = beginning + 1;
                    int blockEnd = i + 1;
                    String subexpression = strBuilder.substring(exprStart, i);
                    if (subexpression.contains("(")) {
                        subexpression = expressionParser(subexpression);
                    }
                    strBuilder.replace(beginning, blockEnd, expressionSolver(subexpression));
                    i = beginning;
                }
            }
        }

        return expressionSolver(strBuilder.toString());
    }

    private static String singleOperation(String expr, char operation) {
        final String strOp = ""+operation;

        String numberRegex = "([\\d]+[\\.\\d]*)(\\%s)([\\d]+[\\.\\d]*)";
        Pattern pattern = Pattern.compile(numberRegex.formatted(strOp));
        Matcher matcher = pattern.matcher(expr);

        StringBuilder strBuilder = new StringBuilder(expr);

        while(matcher.find()) {
            int startIdx = matcher.start();
            int endIdx = matcher.end();

            BigDecimal solution = new BigDecimal(Double.parseDouble(matcher.group(1)));
            BigDecimal nextVal = new BigDecimal(Double.parseDouble(matcher.group(3)));

            switch (operation) {
                case '/' -> solution = solution.divide(nextVal);
                case '*' -> solution = solution.multiply(nextVal);
                case '-' -> solution = solution.subtract(nextVal);
                case '+' -> solution = solution.add(nextVal);
            }

            strBuilder.replace(startIdx, endIdx, solution.toString());

            pattern = Pattern.compile(numberRegex.formatted(strOp));
            matcher = pattern.matcher(strBuilder.toString());
        }

        return strBuilder.toString();
    }

    private static String expressionSolver(String expr) {
        expr = singleOperation(expr, '/');
        expr = singleOperation(expr, '*');
        expr = singleOperation(expr, '-');
        expr = singleOperation(expr, '+');
        return expr;
    }

    private static boolean isOpenParenthesis(char a) {
        return areSameCharacters('(', a);
    }

    private static boolean isCloseParenthesis(char a) {
        return areSameCharacters(')', a);
    }

    private static boolean areSameCharacters(char a, char b) {
        return areSameCharacters(Character.valueOf(a), Character.valueOf(b));
    }

    private static boolean areSameCharacters(Character a, char b) {
        return areSameCharacters(a, Character.valueOf(b));
    }

    private static boolean areSameCharacters(char a, Character b) {
        return areSameCharacters(Character.valueOf(a), b);
    }

    private static boolean areSameCharacters(Character a, Character b) {
        return (char) a == b;
    }
}
