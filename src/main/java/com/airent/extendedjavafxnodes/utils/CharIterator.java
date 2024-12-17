package com.airent.extendedjavafxnodes.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class CharIterator implements Iterator<Character> {
    protected int pos = -1;
    protected int ch = -2;
    private boolean ateNext = false;
    private boolean atePrevious = false;
    private final CharSequence chars;
    protected boolean skipSpace;

    public CharIterator(@NotNull Character[] chars, boolean skipSpace) {
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            sb.append(c);
        }
        this.chars = sb;
        this.skipSpace = skipSpace;
    }

    public CharIterator(@NotNull char[] chars, boolean skipSpace) {
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            sb.append(c);
        }
        this.chars = sb;
        this.skipSpace = skipSpace;
    }

    public CharIterator(@NotNull CharSequence sequence, boolean skipSpace) {
        Objects.requireNonNull(sequence);
        this.chars = sequence;
        this.skipSpace = skipSpace;
    }

    public CharSequence getChars() {
        return chars;
    }

    public String getAsString() {
        return chars.toString();
    }

    /**
     * Gets the currently selected char position,
     * if {@link #getCh()} returns {@code -2}, then this method
     * must, return {@code -1}.
     *
     * @return The index of the current char.
     */
    public int getPos() {
        return pos;
    }

    /**
     * Gets the current char.
     *
     * @return The integer value of the current char.
     */
    public int getCh() {
        return ch;
    }

    /**
     * Advance the current char to the next char.
     */
    protected void nextChar() {
        if (pos < chars.length()) {
            ch = (++pos < chars.length()) ? chars.charAt(pos) : -1;
        } else {
            ch = -1;
        }
    }

    protected boolean hasNextChar() {
        if (nextEnded()) return false;
        return pos+1 < chars.length();
    }

    /**
     * Back tracks to the previous char.
     * <br>
     * This method is the same as {@link #nextChar()},
     * but in reverse.
     */
    protected void previousChar() {
        if (pos > -1) {
            ch = (--pos > -1) ? chars.charAt(pos) : -2;
        } else {
            ch = -2;
        }
    }

    protected boolean hasPreviousChar() {
        if (previousEnded()) return false;
        return pos-1 >= 0;
    }

    /**
     * Checks to see if the current char is equal to {@code charToEat}.
     * If the current char is equal to {@code charToEat},
     * then this {@link Iterator} will advance to the next char
     * and return {@code true}. Otherwise, will NOT advance to the next
     * char and will return {@code false}.
     *
     * @param charToEat The char to check for.
     * @return {@code true} if the current char equals {@code charToEat}.
     */
    public boolean eat(int charToEat) {
        if (nextEnded()) return false;
        if (skipSpace) while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            ateNext = true;
            return true;
        }
        return false;
    }

    /**
     * The same as {@link #eat(int)}, but in the reversed direction.
     *
     * @param charToEat The char to check for.
     * @return {@code true} if the current char equals {@code charToEat}.
     */
    public boolean reverseEat(int charToEat) {
        if (previousEnded()) return false;
        if (skipSpace) while (ch == ' ') previousChar();
        if (ch == charToEat) {
            previousChar();
            atePrevious = true;
            return true;
        }
        return false;
    }

    @Override
    public Character next() {
        if (nextEnded()) {
            throw new NoSuchElementException("There are no elements left.");
        }
        int ret = ch;
        if (!ateNext) {
            nextChar();
            if (ret != ch && !nextEnded()) {
                ret = ch;
            }
        }
        ateNext = false;
        return (char) ret;
    }

    @Override
    public boolean hasNext() {
        if (ateNext) return true;
        return hasNextChar();
    }

    public boolean nextEnded() {
        if (ch == -1) {
            ateNext = false;
            return true;
        }
        return false;
    }

    public Character previous() {
        if (previousEnded()) {
            throw new NoSuchElementException("There are no elements left.");
        }
        int ret = ch;
        if (!atePrevious) {
            previousChar();
            if (ret != ch && !previousEnded()) {
                ret  = ch;
            }
        }
        atePrevious = false;
        return (char) ret;
    }

    public boolean hasPrevious() {
        if (atePrevious) return true;
        return hasPreviousChar();
    }

    public boolean previousEnded() {
        if (ch == -2) {
            atePrevious = false;
            return true;
        }
        return false;
    }
}
