package com.airent.extendedjavafxnodes.utils;

import com.airent.extendedjavafxnodes.utils.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * The StringConvert class is a container for String handling.
 *
 * <h1 id="title">Title</h1>
 * <p>
 *     The information for all things related to title casing.
 * </p>
 * <h2 id="titleStyles">Styles</h2>
 * Information about the style types allowed for title casing.
 * <h4>Same across all types</h4>
 * <p>
 *     Most style guides recommend capitalizing
 *     nouns, verbs, pronouns, adjectives, and adverbs
 *     in the titles of books, articles, and songs.
 *     Most guides also recommend that you capitalize
 *     the first and last words in any title,
 *     no matter what part of speech those words are.
 * </p>
 * <p>
 *     All style guides agree that "to" shouldn't be
 *     capitalized unless it’s the first or last word of a title.
 * </p>
 * <p>
 *     In English, there are three articles: "a," "an," and "the."
 *     These small words normally don't need to be capitalized in titles.
 *     However, if the article falls at the beginning of the title,
 *     then it should be capitalized.
 * </p>
 * <p>
 *     Every style guide agrees that prepositions with
 *     three letters or less shouldn't be capitalized.
 *     Only capitalize prepositions if they have four or more letters.
 * </p>
 * <p>
 *     Regardless of which style guide you’re using, the conjunctions
 *     "but," "and," "nor," "or," "for," "so," "as," "if," and "yet"
 *     are to not be capitalized unless they are the first or last word in a title.
 * </p>
 * The following are the style types:
 * <br>
 * <h4 id="sAP">ap - The Associated Press Stylebook</h4>
 * <p>Capitalize the first and last words.</p>
 * <p>
 *     The AP style guide recommends that prepositions
 *     longer than four letters should be capitalized.
 * </p>
 * <p>AP style requires that conjunctions with three letters or fewer are lowercase.</p>
 * <p>Lowercase the articles the, a, and an.</p>
 * <p>Capitalize the "to" in infinitives.</p>
 * <hr>
 * <h4 id="sCM">cm - Chicago Manual of Style</h4>
 * <p>Always capitalize the first and last words of titles and subtitles.</p>
 * <p>
 *     Always capitalize "major" words
 *     (nouns, pronouns, verbs, adjectives, adverbs, and some conjunctions).
 * </p>
 * <p>Lowercase the conjunctions and, but, for, or, and nor.</p>
 * <p>Lowercase the articles the, a, and an.</p>
 * <p>
 *     Lowercase prepositions, of four letters or less,
 *     except when they are stressed, are used adverbially or adjectivally,
 *     or are used as conjunctions.
 * </p>
 * <p>Lowercase the words to and as.</p>
 * <p>Lowercase the second part of Latin species names.</p>
 * <p>
 *     Lowercase the second word after a hyphenated prefix
 *     (e.g., Mid-, Anti-, Super-, etc.) in compound modifiers
 *     (e.g., Mid-year, Anti-hero, etc.).
 * </p>
 * <hr>
 * <h4 id="sMLA">mla - Modern Language Association Handbook</h4>
 * <p>
 *     Do not capitalize articles, prepositions (regardless of length),
 *     and coordinating conjunctions.
 * </p>
 * <p>
 *     According to the MLA, the first and last word of a title
 *     should be capitalized.
 * </p>
 * <p>
 *     Capitalize all major words
 *     (nouns, verbs including phrasal verbs such as "play with",
 *     adjectives, adverbs, and pronouns) in the title/heading,
 *     including the second part of hyphenated major words
 *     (e.g., Self-Report not Self-report).
 * </p>
 * <p>
 *     If a title has a subtitle, MLA suggests using the same
 *     capitalization rules that you would use for the main title,
 *     including capitalizing the first and last word of the subtitle.
 *     If a preposition, conjunction, or article comes directly after a colon,
 *     they should also be capitalized.
 * </p>
 * <p>
 *     You should capitalize the first word of the hyphenated word.
 *     Only capitalize the word after the hyphen if it's a noun,
 *     a proper adjective, or equal in importance to the first word.
 *     <br>
 *     If a title contains a hyphenated word,
 *     and the second word within the hyphenation is an adjective,
 *     then that second word shouldn't be capitalized.
 * </p>
 * <p>Do not capitalize "to" in infinitives.</p>
 * <hr>
 * <h4 id="sAPA">apa - APA Style</h4>
 * <p>Capitalize the first word of the title/heading and of any subtitle/subheading.</p>
 * <p>
 *     Capitalize all major words
 *     (nouns, verbs including phrasal verbs such as "play with",
 *     adjectives, adverbs, and pronouns) in the title/heading,
 *     including the second part of hyphenated major words
 *     (e.g., Self-Report not Self-report).
 * </p>
 * <p>Capitalize all words of four letters or more.</p>
 * <p>
 *     Lowercase the second word after a hyphenated prefix
 *     (e.g., Mid-, Anti-, Super-, etc.) in compound modifiers
 *     (e.g., Mid-year, Anti-hero, etc.).
 * </p>
 * <hr>
 * <h4 id="sAMA">ama - American Medical Association Manual of Style Capitalization Rules</h4>
 * <p>Capitalize the first and the last word of titles and subtitles.</p>
 * <p>
 *     Capitalize nouns, pronouns, adjectives, verbs
 *     (including phrasal verbs such as "play with"), adverbs,
 *     and subordinate conjunctions (major words).
 * </p>
 * <p>
 *     Lowercase articles (a, an, the), coordinating conjunctions,
 *     and prepositions of three letters or fewer.
 * </p>
 * <p>Lowercase "to" in infinitives.</p>
 * <p>
 *     Lowercase the second word in a hyphenated compound
 *     when it is a prefix or suffix (e.g., "Anti-itch", "World-wide")
 *     or part of a single word.
 *     <br>
 *     Capitalize the second word in a hyphenated compound
 *     if both words are equal and not suffixes or prefixes
 *     (e.g., "Cost-Benefit").
 * </p>
 * <p>Capitalize the genus but not the species epithet.</p>
 * <hr>
 * <h4 id="sBB">bb - The Bluebook</h4>
 * <p>Capitalize the first and the last word.</p>
 * <p>
 *     Capitalize nouns, pronouns, adjectives, verbs
 *     (including phrasal verbs such as "play with"),
 *     adverbs, and subordinate conjunctions.
 * </p>
 * <p>
 *     Lowercase articles (a, an, the), coordinating conjunctions,
 *     and prepositions of four letters or fewer.
 * </p>
 * <p>Lowercase "to" in infinitives (though not defined in the stylebook).</p>
 * <hr>
 * <h4 id="sALL">all</h4>
 * <p>A mix of all the above style types.</p>
 */
public class StringConvert {
    private static final List<String> articleWords = Arrays.asList(
            "a", "an", "the"
    );
    private static final List<String> coordinatingConjunctionWords = Arrays.asList(
            "for", "and", "nor", "but", "or", "yet", "so"
    );
    private static final List<String> conjunctionWords = Arrays.asList(
            "but", "and", "nor", "or", "for", "so", "as", "if", "yet",
            "although", "because", "since", "though", "unless", "until",
            "while", "whether", "either", "neither", "moreover",
            "furthermore", "nevertheless", "however", "consequently",
            "therefore", "hence", "thus", "meanwhile", "afterward",
            "before", "eventually", "likewise", "indeed", "notwithstanding",
            "otherwise", "similarly", "nonetheless", "whereas",
            "wherefore", "regardless", "accordingly", "besides",
            "despite", "once", "provided", "that", "even", "now",
            "supposing", "till", "what", "whatever", "when", "whenever",
            "where", "wherever", "which", "whichever", "who", "whoever",
            "whom", "whomever", "whose", "why", "after", "lest", "than",
            "whither", "also", "except", "instead", "still", "then",
            "both", "just", "rather", "how", "again", "anyway"
    );
    private static final List<String> prepositionWords = Arrays.asList(
            "a", "at", "by", "for", "in", "bar", "but", "cum", "ere", "of",
            "off", "on", "out", "per", "pre", "pro", "qua", "re", "sub",
            "to", "up", "via", "aft", "now", "as", "if", "so", "ago", "dot",
            "ex", "ben", "fae", "fro", "gin", "mid", "pan", "pon", "sen",
            "sin", "ter", "tiv", "upo", "wid", "wiv", "non", "aboard",
            "about", "above", "abreast", "absent", "across", "after",
            "against", "aloft", "along", "alongside", "amid", "amidst",
            "midst", "among", "amongst", "anti", "apropos", "around",
            "round", "aslant", "astride", "atop", "ontop", "barring",
            "before", "behind", "below", "beneath", "neath", "beside",
            "besides", "between", "tween", "beyond", "chez", "circa", "c",
            "ca", "come", "concerning", "contra", "counting", "despite",
            "spite", "down", "during", "effective", "except", "excepting",
            "excluding", "failing", "following", "including", "inside",
            "less", "like", "minus", "modulo", "mod", "near", "nearer",
            "nearest", "next", "notwithstanding", "o", "offshore", "onto",
            "opposite", "outside", "over", "oer", "pace", "past", "pending",
            "plus", "post", "regarding", "respecting", "sans", "save", "saving",
            "short", "since", "than", "through", "thru", "throughout", "thruout",
            "till", "times", "t", "touching", "toward", "towards", "under",
            "underneath", "unlike", "until", "unto", "upon", "versus", "vs", "v",
            "vice", "vis-à-vis", "vis-a-vis", "visavis", "visàvis", "wanting",
            "with", "w", "within", "wi", "without", "thout", "wo", "worth",
            "abroad", "adrift", "afterward", "afterwards", "ahead", "apart",
            "ashore", "aside", "away", "back", "backward", "backwards", "withal",
            "beforehand", "downhill", "downstage", "downstairs", "downstream",
            "downward", "downwards", "downwind", "east", "eastward", "eastwards",
            "forth", "forward", "forwards", "heavenward", "heavenwards", "hence",
            "henceforth", "here", "hereby", "herein", "hereof", "hereto", "herewith",
            "home", "homeward", "homewards", "indoors", "inward", "inwards", "leftward",
            "leftwards", "north", "northeast", "northward", "northwards", "northwest",
            "onward", "onwards", "outdoors", "outward", "outwards", "overboard",
            "overhead", "overland", "overseas", "rightward", "rightwards", "seaward",
            "seawards", "skyward", "skywards", "south", "southeast", "southward",
            "southwards", "southwest", "then", "thence", "thenceforth", "there",
            "thereby", "therein", "thereof", "thereto", "therewith", "together",
            "underfoot", "underground", "uphill", "upstage", "upstairs", "upstream",
            "upward", "upwards", "upwind", "west", "westward", "westwards", "when",
            "whence", "where", "whereby", "wherein", "whereof", "whereto", "wherewith",
            "although", "because", "considering", "from", "given", "granted", "into",
            "lest", "once", "provided", "providing", "seeing", "supposing", "though",
            "unless", "whenever", "whereas", "wherever", "while", "whilst", "abaft",
            "abating", "abeam", "ablow", "aboon", "abouts", "acrost", "adown", "a-eastell",
            "aeastell", "afore", "afornent", "afront", "afterhand", "again", "ahind",
            "ajax", "alength", "alongst", "aloof", "alow", "amell", "amidmost", "anear",
            "aneath", "anent", "anewst", "anunder", "askant", "asklent", "astern",
            "athwart", "atour", "atter", "atween", "atwixt", "a-weather", "aweather",
            "a-west", "awest", "awestell", "ayond", "ayont", "bating", "bedown",
            "be-east", "beeast", "beforrow", "behither", "benorth", "besouth",
            "betwixt", "twixt", "bewest", "bongre", "bout", "bove", "cept", "contrair",
            "contrary", "cross", "dehors", "durante", "effore", "emong", "emonges",
            "emongest", "endlong", "endlonges", "endlongs", "enduring", "ensuing",
            "even-forth", "evenforth", "excepted", "extra", "forby", "forbye", "forbe",
            "fornent", "fornenst", "foregain", "foregains", "foregainst", "forne",
            "forout", "forouten", "forrow", "forth", "fromward", "fromwards", "froward",
            "furth", "gain", "gainst", "gainward", "half-way", "halfway", "hent",
            "inboard", "incontrair", "indurand", "inmid", "inmiddes", "inter",
            "inthrough", "intil", "intill", "inwith", "ith", "long", "longs", "longst",
            "longways", "malgrado", "malgré", "malgre", "mang", "maugre", "midmost",
            "mids", "midward", "midway", "mong", "mongst", "more", "moreover", "moyening",
            "natheless", "nathless", "nearabout", "nearbout", "nearby", "nearhand",
            "neath", "nigh", "anigh", "anighst", "nigh-hand", "nighhand", "nobbut",
            "non-obstant", "nonobstant", "notwithstand", "noughtwithstanding", "offa",
            "offen", "only", "or", "otherside", "outcept", "outen", "out-over", "outover",
            "outta", "out-taken", "outtaken", "out-taking", "outtaking", "out-through",
            "outthrough", "outwith", "overcross", "over-right", "overright", "overthorter",
            "overthwart", "overtop", "pass", "quoad", "reserved", "reserving", "sauf",
            "seen", "senza", "side", "sidelings", "sidelong", "sides", "sineth", "sith",
            "sithen", "sithence", "thorough", "thorter", "thwart", "thwart-over",
            "thwartover", "touchant", "transverse", "traverse", "twel", "twell", "twill",
            "ultra", "umbe", "unneath", "upside", "upsy", "upsees", "uptill", "utouth",
            "withinside", "withoutside", "ymong", "yond", "yonside", "aground", "bush",
            "hereat", "herefrom", "hereon", "hither", "thereat", "therefrom", "thereon",
            "thither", "whereat", "wherefrom", "whereof", "whereon", "whither", "yonder"
    );
    private static final List<String> constantWords = Arrays.asList(
            "into", "onto", "from", "both", "with", "a", "and", "as",
            "at", "but", "by", "down", "for", "if", "in", "like",
            "near", "nor", "of", "off", "on", "once", "or", "over",
            "past", "so", "than", "that", "to", "upon", "when", "yet"
    );
    private static final List<String> titleCaseStyles = Arrays.asList(
            "ap", "cm", "mla", "apa", "ama", "bb", "all"
    );

    /**
     * Gives <a href="#title">title casing</a> to a given string.
     *
     * <br><br>
     * Each of the following is how this method splits the statement
     * for title casing:
     * (What is removed during the splitting process is added back.)
     * <br>
     * Firstly is the period (.), then the question mark (?),
     * then the exclamation mark (!), then the colon (:),
     * then the semicolon (;), and finally, to get each word,
     * it is split by the space.
     *
     * <br><br>
     * The following are the allowed style types:
     * <ol>
     *     <li><a href="#sAP">ap</a></li>
     *     <li><a href="#sCM">cm</a></li>
     *     <li><a href="#sMLA">mla</a></li>
     *     <li><a href="#sAPA">apa</a></li>
     *     <li><a href="#sAMA">ama</a></li>
     *     <li><a href="#sBB">bb</a></li>
     *     <li><a href="#sALL">all</a></li>
     * </ol>
     *
     * @param statement The string value to title case.
     * @param style The type of formatting for the title.
     * @return The title cased version of the provided statement.
     */
    @NotNull
    public static String toTitleCase(String statement, String style) {
        if (!titleCaseStyles.contains(style)) throw new RuntimeException("Unsupported title case style.");
        return stringSplitter(statement, ".", (dot, di) -> {
            return stringSplitter(dot, "?", (question, qi) -> {
                return stringSplitter(question, "!", (ex, ei) -> {
                    return stringSplitter(ex, ":", (colon, ci) -> {
                        return stringSplitter(colon, ";", (semicolon, si) -> {
                            return stringSplitter(semicolon, " ", (segment, spi) -> {
                                if (segment.isEmpty()) {
                                    return " ";
                                }
                                String[] hyph = segment.split("-"); // hyphenated
                                if (hyph.length == 2) {
                                    String together;
                                    if (style.equals("mla") || style.equals("apa") || style.equals("cm") || style.equals("all")) {
                                        together = hyph[0].substring(0, 1).toUpperCase()+
                                                hyph[0].substring(1)+"-";
                                        if (style.equals("mla") || style.equals("all")) {
                                            if (isWordTitleCaseable(hyph[1], 1, 2, style)) {
                                                together += hyph[1].substring(0, 1).toUpperCase()+
                                                        hyph[1].substring(1);
                                            } else {
                                                together += hyph[1];
                                            }
                                        } else {
                                            together += hyph[1].toLowerCase();
                                        }
                                        return together;
                                    }
                                }
                                if (isWordTitleCaseable(segment, spi.getKey(), spi.getValue()-1, style)) {
                                    return segment.substring(0, 1).toUpperCase()+
                                            segment.substring(1);
                                }
                                return segment;
                            });
                        });
                    });
                });
            });
        });
    }

    /**
     * Checks to see if the provided word can be made capital
     * in a title, given no context to the surrounding words.
     * <br>
     * This method is used it {@link #toTitleCase(String, String)}
     * to calculate whether a word can be capitalized in the title casing.
     * <br>
     * This method only takes into consideration if the statement
     * that the provided word is in was split by spaces.
     * <br><br>
     * The following are the style types:
     * <ol>
     *     <li><a href="#sAP">ap</a></li>
     *     <li><a href="#sCM">cm</a></li>
     *     <li><a href="#sMLA">mla</a></li>
     *     <li><a href="#sAPA">apa</a></li>
     *     <li><a href="#sAMA">ama</a></li>
     *     <li><a href="#sBB">bb</a></li>
     *     <li><a href="#sALL">all</a></li>
     * </ol>
     *
     * @param word The word to check.
     * @param index The index placement of the word.
     * @param lastIndex The index of the last word in the statement.
     * @param style The type of formatting for the title.
     * @return {@code true} if the word can be made capital.
     */
    public static boolean isWordTitleCaseable(@NotNull String word, int index, int lastIndex, String style) {
        if (!titleCaseStyles.contains(style)) throw new RuntimeException("Unsupported title case style.");
        if (style.equals("ap") ||
                style.equals("cm") ||
                style.equals("mla") ||
                style.equals("ama") ||
                style.equals("bb") ||
                style.equals("all")) {
            if (index == 0 || index == lastIndex) return true;
        } else {
            if (index == 0) return true;
        }
        return !isIn(word, style);
    }

    private static boolean isIn(String word, @NotNull String style) {
        if (word == null || word.isBlank()) return false;
        boolean ret = false;
        if (style.equals("all")) {
            for (String s : titleCaseStyles) {
                if (s.equals("all")) {
                    ret = catchIn(word, "default");
                } else {
                    ret = catchIn(word, s);
                }
                if (ret) break;
            }
        } else {
            ret = catchIn(word, style);
        }
        if (!ret) {
            word = word.toLowerCase().replaceAll("[,.!?@#$%^&*(){}\\[\\];:/\\\\`~\\-_=+'\"]", "");
            ret = catchIn(word, style);
            if (!ret) {
                if (word.endsWith("s")) {
                    ret = catchIn(word.substring(0, word.length()-1), style);
                }
            }
        }
        return ret;
    }

    private static boolean catchIn(String word, @NotNull String style) {
        boolean ret = false;
        if (style.equals("apa")) {
            ret = word.length() >= 4;
        }
        if (!ret) {
            ret = articleWords.contains(word) || constantWords.contains(word);
        }
        if (!ret) {
            if (prepositionWords.contains(word)) {
                if (style.equals("ap") || style.equals("cm") || style.equals("bb")) {
                    if (word.length() <= 4) {
                        ret = true;
                    }
                } else if (style.equals("ama")) {
                    if (word.length() <= 3) {
                        ret = true;
                    }
                } else {
                    ret = true;
                }
            }
        }
        boolean mab = style.equals("mla") || style.equals("ama") || style.equals("bb");
        if (!ret) {
            if (mab) {
                if (coordinatingConjunctionWords.contains(word)) {
                    ret = true;
                }
            } else {
                if (conjunctionWords.contains(word)) {
                    if (style.equals("ap")) {
                        if (word.length() <= 3) {
                            ret = true;
                        }
                    } else {
                        ret = true;
                    }
                }
            }
        }
        if (word.startsWith("to") && !word.equals("to")) {
            if (style.equals("ap")) {
                ret = false;
            } else if (mab) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Capitalizes a section of a String.
     * <br>
     * Set endIndex to -1 for another way of setting endIndex to {@code string.length()}.
     * Using -1 should be a little faster.
     *
     * @param string The string to capitalize.
     * @param startIndex The index at witch capitalization should begin. (inclusive)
     * @param endIndex The index at witch capitalization should end. (exclusive)
     * @return The string with the desired section capitalized.
     */
    @NotNull
    public static String capitalize(String string, int startIndex, int endIndex) {
        if (endIndex != -1) {
            return (startIndex!=0?string.substring(0, startIndex):"")+
                    string.substring(startIndex, endIndex).toUpperCase()+
                    (endIndex<string.length()?string.substring(endIndex):"");
        } else {
            if (startIndex != 0) {
                return string.substring(0, startIndex)+
                        string.substring(startIndex).toUpperCase();
            } else {
                return string.toUpperCase();
            }
        }
    }

    /**
     * Sends a section of a String into lowercase.
     * <br>
     * Set endIndex to -1 for another way of setting endIndex to {@code string.length()}.
     * Using -1 should be a little faster.
     *
     * @param string The string to make into lowercase.
     * @param startIndex The index at witch lowercase should begin. (inclusive)
     * @param endIndex The index at witch lowercase should end. (exclusive)
     * @return The string with the desired section as lowercase.
     */
    @NotNull
    public static String lowerCase(String string, int startIndex, int endIndex) {
        if (endIndex != -1) {
            return (startIndex!=0?string.substring(0, startIndex):"")+
                    string.substring(startIndex, endIndex).toLowerCase()+
                    (endIndex<string.length()?string.substring(endIndex):"");
        } else {
            if (startIndex != 0) {
                return string.substring(0, startIndex)+
                        string.substring(startIndex).toLowerCase();
            } else {
                return string.toLowerCase();
            }
        }
    }

    /**
     * Splits and applies a function to each split segment of a String.
     * <br>
     * So to have parts skipped, i.e., not added, have the function return
     * {@code null}.
     *
     * @param string The string to split.
     * @param splitter The String value that is used to split the
     *                 provided string by, this value will be added
     *                 back in appropriate locations.
     * @param biFunction The function to handle the split,
     *                   this provides a String and Pair,
     *                   the String is a segment of the string,
     *                   the Pair has a key of Integer, the
     *                   key is the index location of the
     *                   segment. Also, the Pair
     *                   has a value of Integer;
     *                   the value is the count of total segments.
     * @return The put together String after split handling.
     */
    @NotNull
    public static String stringSplitter(@NotNull String string, @NotNull String splitter, BiFunction<String, Pair<Integer, Integer>, String> biFunction) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (splitter) {
            case "." -> splitter = "\\.";
            case "?" -> splitter = "\\?";
            case "$" -> splitter = "\\$";
            case "^" -> splitter = "\\^";
            case "*" -> splitter = "\\*";
            case "+" -> splitter = "\\+";
            case "[" -> splitter = "\\[";
            case "(" -> splitter = "\\(";
            case ")" -> splitter = "\\)";
            case "|" -> splitter = "\\|";
            case "\\" -> splitter = "\\\\";
        }
        String[] split = string.split(splitter);
        for (int i=0; i < split.length; i++) {
            String add = biFunction.apply(split[i], new Pair<>(i, split.length));
            if (add != null) {
                stringBuilder.append(add);
            }
            if (i != split.length-1) {
                stringBuilder.append(splitter);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Converts a boolean value to Yes or No.
     *
     * @param bool The boolean to convert.
     * @return Yes (true) or No (false).
     */
    @NotNull
    @Contract(pure = true)
    public static String booleanToYN(boolean bool) {
        if (bool) {
            return "Yes";
        }
        return "No";
    }

    public static String addSpace(String stringWithNoSpace) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : stringWithNoSpace.toCharArray()) {
            if (Character.toUpperCase(c) == c && Character.isAlphabetic(c)) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}
