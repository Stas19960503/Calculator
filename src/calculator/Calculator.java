package calculator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class Calculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueCalculation = true;

        System.out.println("Умный Калькулятор");
        System.out.println("Примеры: '2+3*4', '10/2-3', 'sqrt(16)+5', '2^3'");

        while (continueCalculation) {
            System.out.println("\nВведите математическое выражение:");

            String input = scanner.nextLine()
                    .replace(",", ".")
                    .replaceAll("\\s+", "");

            try {
                double result = evaluateExpression(input);
                System.out.println("Результат: " + result);
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }

            boolean validAnswer = false;
            while (!validAnswer) {
                System.out.println("\nХотите выполнить еще одно вычисление?");
                String answer = scanner.nextLine().trim();

                if (answer.equalsIgnoreCase("нет")) {
                    continueCalculation = false;
                    validAnswer = true;
                } else if (answer.equalsIgnoreCase("да")) {
                    validAnswer = true;
                } else {
                    System.out.println("Ошибка: Пожалуйста, введите именно 'да' или 'нет'.");
                }
            }
        }

        System.out.println("Программа завершена. До свидания!");
        scanner.close();
    }

    private static double evaluateExpression(String expression) {
        Queue<String> tokens = tokenize(expression);
        Queue<String> rpn = infixToRPN(tokens);
        return calculateRPN(rpn);
    }

    private static Queue<String> tokenize(String expression) {
        Queue<String> tokens = new LinkedList<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else {
                if (numberBuffer.length() > 0) {
                    tokens.add(numberBuffer.toString());
                    numberBuffer.setLength(0);
                }

                if (c == 's' && expression.startsWith("sqrt", i)) {
                    tokens.add("sqrt");
                    i += 3;
                } else {
                    tokens.add(String.valueOf(c));
                }
            }
        }
        if (numberBuffer.length() > 0) {
            tokens.add(numberBuffer.toString());
        }
        return tokens;
    }

    private static Queue<String> infixToRPN(Queue<String> tokens) {
        Queue<String> output = new LinkedList<>();
        Stack<String> operators = new Stack<>();

        while (!tokens.isEmpty()) {
            String token = tokens.poll();

            if (token.matches("\\d+(\\.\\d+)?")) {
                output.add(token);
            } else if (token.equals("sqrt")) {
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop();
                }
                if (!operators.isEmpty() && operators.peek().equals("sqrt")) {
                    output.add(operators.pop());
                }
            } else {
                while (!operators.isEmpty() && hasHigherPrecedence(operators.peek(), token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        return output;
    }

    private static boolean hasHigherPrecedence(String op1, String op2) {
        int p1 = getPrecedence(op1);
        int p2 = getPrecedence(op2);
        return p1 >= p2 && !op1.equals("(") && !op1.equals("sqrt");
    }

    private static int getPrecedence(String op) {
        if (op.equals("+") || op.equals("-")) return 1;
        if (op.equals("*") || op.equals("/")) return 2;
        if (op.equals("^")) return 3;
        return 0;
    }

    private static double calculateRPN(Queue<String> rpn) {
        Stack<Double> stack = new Stack<>();

        while (!rpn.isEmpty()) {
            String token = rpn.poll();

            if (token.matches("\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else if (token.equals("sqrt")) {
                if (stack.isEmpty()) throw new IllegalArgumentException("Неверное выражение (sqrt)");
                double a = stack.pop();
                if (a < 0) throw new IllegalArgumentException("Корень из отрицательного числа");
                stack.push(Math.sqrt(a));
            } else {
                if (stack.size() < 2) throw new IllegalArgumentException("Неверное выражение");
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/":
                        if (b == 0) throw new IllegalArgumentException("Деление на ноль");
                        stack.push(a / b);
                        break;
                    case "^": stack.push(Math.pow(a, b)); break;
                }
            }
        }

        if (stack.size() != 1) throw new IllegalArgumentException("Неверное выражение");
        return stack.pop();
    }
}
