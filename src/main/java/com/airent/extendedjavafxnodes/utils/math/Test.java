package com.airent.extendedjavafxnodes.utils.math;

public class Test {
    public static void main(String[] args) {
        String asPartialWord = NumberConvert.convertPartialWord("665481210000", 2);
        System.out.println(asPartialWord);
        System.out.println(NumberConvert.convertToNumber(asPartialWord).toPlainString());
    }
}
