package com.airent.extendedjavafxnodes.utils.math;

import com.airent.extendedjavafxnodes.utils.Named;
import com.airent.extendedjavafxnodes.utils.CharIterator;

import java.io.Serial;
import java.io.Serializable;
import ch.obermuhlner.math.big.BigDecimalMath;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Equation implements Serializable {
    @Serial
    private static final long serialVersionUID = 250195486L;

    private String equation;

    public Equation(String equation) {
        this.equation = equation;
    }

    public boolean hasVariables() {
        return equation.contains("$");
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public BigDecimal calculate() {
        return eval(this.equation);
    }

    public BigDecimal calculate(@NotNull String... vars) {
        return eval(apply(vars));
    }

    public BigDecimal calculate(Number... variables) {
        return eval(apply(variables));
    }

    public BigDecimal calculate(Map<String, ? extends Number> namedVariables) {
        return eval(apply(namedVariables));
    }

    public BigDecimal calculate(Map<String, ? extends Number> namedVariables, List<? extends Number> variables) {
        return eval(apply(namedVariables, variables));
    }

    public String apply(Number... variables) {
        return apply(null, Arrays.asList(variables));
    }

    public String apply(@NotNull Map<String, ? extends Number> namedVariables) {
        return apply(namedVariables, null);
    }

    public String apply(@NotNull String... variables) {
        HashMap<String, BigDecimal> namedVars = new HashMap<>();
        ArrayList<BigDecimal> vars = new ArrayList<>();
        for (String var : variables) {
            var = var.trim();
            String[] keyVal = var.split(":");
            if (keyVal.length == 2) {
                namedVars.put(keyVal[0].trim(), eval(keyVal[1].trim()));
            } else if (keyVal.length == 1) {
                vars.add(eval(keyVal[0].trim()));
            }
        }
        return apply(namedVars, vars);
    }

    public String apply(final Map<String, ? extends Number> namedVariables, List<? extends Number> variables) {
        HashMap<String, Number> vars;
        if (namedVariables != null && !namedVariables.isEmpty()) {
            vars = new HashMap<>(namedVariables);
        } else {
            vars = new HashMap<>();
        }
        String equate = this.equation;
        if (variables != null && !variables.isEmpty()) {
            equate = new CharIterator(this.equation, true) {
                String endString = getEquation();
                final ArrayList<String> alreadyDone = new ArrayList<>();
                String parse() {
                    if (nextEnded()) {
                        //setEquation(endString);
                        return endString;
                    }
                    return parseExpression();
                }

                String parseExpression() {
                    for (;;) {
                        if (eat('$') || nextEnded()) break;
                        else nextChar();
                    }
                    if (eat('(')) {
                        parseFactor();
                    }
                    return parse();
                }

                void parseFactor() {
                    int parenthsCount = 1;
                    StringBuilder equate = new StringBuilder();
                    for (;;) {
                        if (parenthsCount == 0) break;
                        else {
                            if (nextEnded()) throw new RuntimeException("Failed to close variable parser.");
                            if (ch == ')') {
                                parenthsCount--;
                            } else if (ch == '(') parenthsCount++;
                            else if (ch == '$') throw new RuntimeException("Cannot have variables in a variable parser.");
                            equate.append((char)ch);
                            nextChar();
                        }
                    }

                    if (alreadyDone.contains(equate.toString())) return;
                    if (equate.toString().endsWith(")")) {
                        alreadyDone.add(equate.toString());
                        int index = eval(equate.substring(0, equate.length()-1)).intValue();
                        String unNamedVar = "$("+index+")";
                        endString = endString.replace("$("+equate, unNamedVar);
                        vars.put(unNamedVar, variables.get(index));
                    } else {
                        throw new RuntimeException("Unexpectedly the variable parser wasn't registered as closed.");
                    }
                }
            }.parse();
        }

        for (Map.Entry<String, Number> entry : vars.entrySet()) {
            String name = entry.getKey();
            if (!name.startsWith("$")) {
                name = "$" + name;
            }
            if (entry.getValue() instanceof BigDecimal val) {
                equate = equate.replace(name, val.toPlainString());
            } else {
                equate = equate.replace(name, entry.getValue().toString());
            }
        }
        return equate;
    }

    // Static

    public static boolean evalStatement(@NotNull final String str) {
        if (str.equals("true")) {
            return true;
        } if (str.equals("false")) {
            return false;
        } else {
            BigDecimal bin = eval("if("+str+", 1, 0)");
            return bin.compareTo(BigDecimal.ONE) == 0;
        }
    }

    public static BigDecimal eval(final String str) {
        return eval(str, 100);
    }

    public static BigDecimal eval(final String str, int precision) {
        return new EvalParser(str, precision).parse();
    }

    private static class EvalParser extends CharIterator {
        private final MathContext context;//MathContext.UNLIMITED;
        public EvalParser(CharSequence sequence, int precision) {
            super(sequence, true);
            context = new MathContext(precision);
        }

        BigDecimal parse() {
            nextChar();
            BigDecimal x = parseExpression();
            if (pos < getChars().length()) throw new RuntimeException("Unexpected: " + (char)ch);
            return x;
        }

        // Grammar:
        // expression = term | expression `+` term | expression `-` term
        // term = factor | term `*` factor | term `/` factor
        // factor = `+` factor | `-` factor | `(` expression `)` | number
        //        | functionName `(` expression `)` | functionName factor
        //        | factor `^` factor

        BigDecimal parseExpression() {
            BigDecimal x = parseTerm();
            for (;;) {
                if      (eat('+')) x = x.add(parseTerm(), context); // addition
                else if (eat('-')) x = x.subtract(parseTerm(), context); // subtraction
                else return x;
            }
        }

        BigDecimal parseTerm() {
            BigDecimal x = parseFactor();
            for (;;) {
                if      (eat('*')) x = x.multiply(parseFactor(), context); // multiplication
                else if (eat('/')) x = x.divide(parseFactor(), context); // division
                else return x;
            }
        }

        BigDecimal parseFactor() {
            if (eat('+')) return parseFactor().plus(context); // unary plus
            if (eat('-')) return parseFactor().negate(context); // unary minus

            BigDecimal val;
            int startPos = this.pos;
            if (eat('(')) { // parentheses
                val = parseExpression();
                if (!eat(')')) throw new RuntimeException("Missing ')'");
            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                val = new BigDecimal(getAsString().substring(startPos, this.pos));
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) { // functions
                while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) nextChar();
                String func = getAsString().substring(startPos, this.pos);
                if (func.equals("pi")) {
                    val = BigDecimalMath.pi(context);
                } else if (func.equals("e")) {
                    val = BigDecimalMath.e(context);
                } else {
                    BigDecimal first = new BigDecimal(1);
                    Boolean statement = null;
                    List<String> rounding = Arrays.asList(
                            "round",
                            "roundTo",
                            "ceiling",
                            "floor"
                    );
                    List<String> twoParts = Arrays.asList(
                            "root",
                            "log",
                            "pow",
                            "roundTo"
                    );
                    List<String> threeParts = Arrays.asList(
                            "if",
                            "nif"
                    );
                    List<String> infiniteParts = Arrays.asList(
                            "average",
                            "sum",
                            "reduce",
                            "multiply",
                            "divide",
                            "max",
                            "min"
                    );

                    if (eat('(')) {
                        if (twoParts.contains(func)) {
                            first = parseExpression();
                            if (!eat(',')) throw new RuntimeException("Missing ',' in "+func);
                        } else if (threeParts.contains(func)) {
                            statement = parseStatement();
                            if (!eat(',')) throw new RuntimeException("Missing first ',' in "+func);
                            first = parseExpression();
                            if (!eat(',')) throw new RuntimeException("Missing second ',' in "+func);
                        }
                        val = parseExpression();
                        if (infiniteParts.contains(func)) {
                            while (eat(',')) {
                                val = switch (func) {
                                    case "average", "sum" -> val.add(parseExpression());
                                    case "reduce" -> val.subtract(parseExpression());
                                    case "multiply" -> val.multiply(parseExpression());
                                    case "divide" -> val.divide(parseExpression(), context);
                                    case "max" -> val.max(parseExpression());
                                    case "min" -> val.min(parseExpression());
                                    default -> throw new IllegalStateException("Unexpected value: " + func);
                                };
                                first = first.add(new BigDecimal(1));
                            }
                        }
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        List<String> all = Arrays.asList("","");
                        all.addAll(twoParts);
                        all.addAll(threeParts);
                        all.addAll(infiniteParts);
                        if (all.contains(func)) throw new RuntimeException("Missing '(' after function of " + func);
                        val = parseFactor();
                    }

                    int seconta = 0;
                    if (rounding.contains(func)) {
                        seconta = val.toPlainString().split("\\.")[0].length();
                    }

                    val = switch (func) {
                        // single
                        case "sqrt" -> BigDecimalMath.sqrt(val, context);

                        case "sin" -> BigDecimalMath.sin(val, context);
                        case "asin" -> BigDecimalMath.asin(val, context);
                        case "sinh" -> BigDecimalMath.sinh(val, context);
                        case "asinh" -> BigDecimalMath.asinh(val, context);

                        case "cos" -> BigDecimalMath.cos(val, context);
                        case "acos" -> BigDecimalMath.acos(val, context);
                        case "cosh" -> BigDecimalMath.cosh(val, context);
                        case "acosh" -> BigDecimalMath.acosh(val, context);

                        case "tan" -> BigDecimalMath.tan(val, context);
                        case "atan" -> BigDecimalMath.atan(val, context);
                        case "tanh" -> BigDecimalMath.tanh(val, context);
                        case "atanh" -> BigDecimalMath.atanh(val, context);

                        case "cot" -> BigDecimalMath.cot(val, context);
                        case "acot" -> BigDecimalMath.acot(val, context);
                        case "coth" -> BigDecimalMath.coth(val, context);
                        case "acoth" -> BigDecimalMath.acoth(val, context);

                        case "abs" -> val.abs(context);
                        case "factorial" -> BigDecimalMath.factorial(val, context);
                        case "gamma" -> BigDecimalMath.gamma(val, context);
                        case "exp" -> BigDecimalMath.exp(val, context);
                        case "exponent" -> new BigDecimal(BigDecimalMath.exponent(val), context);
                        case "mantissa" -> BigDecimalMath.mantissa(val);
                        case "ceiling" -> BigDecimalMath.round(val, new MathContext(seconta, RoundingMode.CEILING));
                        case "floor" -> BigDecimalMath.round(val, new MathContext(seconta, RoundingMode.FLOOR));
                        case "round" -> BigDecimalMath.round(val, new MathContext(seconta, context.getRoundingMode()));
                        case "logE" -> BigDecimalMath.log(val, context);
                        case "logTen" -> BigDecimalMath.log10(val, context);
                        case "logTwo" -> BigDecimalMath.log2(val, context);
                        case "rad" -> BigDecimalMath.toRadians(val, context);
                        case "deg" -> BigDecimalMath.toDegrees(val, context);
                        // two
                        case "roundTo" -> BigDecimalMath.round(val, new MathContext(Math.abs(first.intValue())+seconta, context.getRoundingMode()));
                        case "pow" -> BigDecimalMath.pow(first, val, context);
                        case "root" -> BigDecimalMath.root(first, val, context);
                        case "log" -> BigDecimalMath.log10(first, context).divide(BigDecimalMath.log10(val, context), context);
                        // three
                        case "if" -> (Boolean.TRUE.equals(statement) ? first : val);
                        case "nif" -> (!Boolean.TRUE.equals(statement) ? first : val);
                        // infinite
                        case "average" -> val.divide(first, context);
                        case "sum", "reduce", "multiply", "divide", "max", "min" -> (context.getPrecision()==0?val:BigDecimalMath.round(val, context));
                        // other
                        default -> throw new RuntimeException("Unknown function: " + func);
                    };
                }
            } else {
                throw new RuntimeException("Unexpected: " + (char)ch);
            }

            if (eat('^')) val = BigDecimalMath.pow(val, parseFactor(), context); // exponentiation
            if (eat('%')) {
                if (eat('%')) {
                    val = val.divideAndRemainder(parseFactor(), context)[1]; // modulo
                } else {
                    val = val.divide(new BigDecimal(100), context)
                            .multiply(parseFactor(), context); // percent of
                }
            }

            return val;
        }

        boolean parseStatement() {
            return new Object() {
                private boolean forceEnd = false;
                boolean parse() {
                    boolean x = parseExpression();
                    if (pos < getChars().length() && ch != ',') throw new RuntimeException("Unexpected: " + (char)ch);
                    return x;
                }

                boolean parseExpression() {
                    return parseExpression(parseFactor());
                }

                boolean parseExpression(boolean x) {
                    for (;;) {
                        if (eat('&')) x = x && parseFactor();
                        else if (eat('|')) x = x || parseFactor();
                        else return x;
                    }
                }

                boolean parseTerm() {
                    return parseTerm(parseEquation());
                }

                boolean parseTerm(BigDecimal x) {
                    return parseTerm(x, null);
                }

                boolean parseTerm(BigDecimal x, Boolean lastGood) {
                    Boolean bool = null;
                    BigDecimal x2 = null;
                    if (eat('>')) {
                        x2 = parseEquation();
                        if (eat('=')) bool = x.compareTo(x2) >= 0;
                        else bool = x.compareTo(x2) > 0;
                    } else if (eat('<')) {
                        x2 = parseEquation();
                        if (eat('=')) bool = x.compareTo(x2) <= 0;
                        else bool = x.compareTo(x2) < 0;
                    } else if (eat('=')) {
                        x2 = parseEquation();
                        bool = x.compareTo(x2) == 0;
                    } else if (eat('!')) {
                        if (eat('=')) {
                            x2 = parseEquation();
                            bool = x.compareTo(x2) != 0;
                        } else {
                            throw new RuntimeException("Missing '='");
                        }
                    }
                    if (forceEnd) {
                        if (bool == null) throw new RuntimeException("Could not force end statement.");
                        if (lastGood != null) bool = bool && lastGood;
                        return bool;
                    }
                    if (needsCompare()) {
                        if (bool == null) throw new RuntimeException("Incomplete compare statement.");
                        return parseTerm(x2, bool);
                    }
                    if (bool!=null) {
                        if (lastGood != null) bool = bool && lastGood;
                        if (backToBegin()) {return parseExpression(bool);}
                        return bool;
                    } else {
                        if (backToBegin()) {
                            if (lastGood != null) return parseExpression(lastGood);
                        }
                    }
                    throw new RuntimeException("Could not detect comparison symbols.");
                }

                boolean backToBegin() {
                    return ch=='&' || ch=='|';
                }

                boolean needsCompare() {
                    return ch == '>' || ch == '<' ||
                            ch == '=' || ch == '!';
                }

                boolean parseFactor() {
                    if (eat('!')) return !parseFactor();

                    boolean x;
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')'");
                    } else {
                        x = parseTerm();
                        if (forceEnd) return x;
                    }
                    if (backToBegin()) x = parseExpression(x);

                    return x;
                }

                BigDecimal parseEquation() {
                    BigDecimal x = EvalParser.this.parseExpression();
                    if (ch == ',') {
                        forceEnd = true;
                    }
                    return x;
                }
            }.parse();
        }
    }
}
