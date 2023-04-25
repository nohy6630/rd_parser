import java.util.Scanner;

class Lexeme {
    public int type;
    public String data;
}

public class RDparser {
    private static int charClass;
    private static int nextToken;
    private static char nextChar;
    static String lexeme;

    // 문자 1개의 의미
    private static final int EOF = -1;
    private static final int DIGIT = 0;
    private static final int LOGIC = 1;
    private static final int ARITH = 2;
    private static final int EQUAL = 3;
    private static final int PAREN = 4;
    private static final int UNKNOWN = 99;
    //어휘 1개의 의미
    private static final int INT_LIT = 10;
    private static final int ADD_OP = 21;
    private static final int SUB_OP = 22;
    private static final int MULT_OP = 23;
    private static final int DIV_OP = 24;
    private static final int LEFT_PAREN = 25;
    private static final int RIGHT_PAREN = 26;
    private static final int EQUAL_OP = 27;
    private static final int NOT_EQUAL_OP = 28;
    private static final int GREATER_OP = 29;
    private static final int LESS_OP = 30;
    private static final int GREATER_EQUAL_OP = 31;
    private static final int LESS_EQUAL_OP = 32;
    private static final int UNKNOWN_OP = 98;

    // syntax analysis
    private static boolean isBinary;
    private static int idx;
    private static Lexeme[] lexemes = new Lexeme[1000];

    static String inputStr;
    static int inputIdx;

    private static int factor() {
        //System.out.println("enter factor");
        if (lexemes[idx].type == LEFT_PAREN) {
            idx++;
            int res = aexpr();
            if (lexemes[idx].type == RIGHT_PAREN) {
                idx++;
                return res;
            } else
                throw new RuntimeException("syntax error!!");
        }
        return number();
    }

    private static int term() {
        //System.out.println("enter term");
        int l = factor();
        while (true) {
            if (lexemes[idx].type == ADD_OP || lexemes[idx].type == SUB_OP) {
                int tmp = lexemes[idx].type;
                idx++;
                int r = factor();
                if (tmp == ADD_OP)
                    l += r;
                else
                    l -= r;
            } else
                break;
        }
        return l;
    }

    private static int aexpr() {
        //System.out.println("enter aexpr");
        int l = term();
        while (true) {
            if (lexemes[idx].type == MULT_OP || lexemes[idx].type == DIV_OP) {
                int tmp = lexemes[idx].type;
                idx++;
                int r = term();
                if (tmp == MULT_OP)
                    l *= r;
                else
                    l /= r;
            } else
                break;
        }
        return l;
    }

    private static int number() {
        if (lexemes[idx].type == INT_LIT)
            return Integer.parseInt(lexemes[idx++].data);
        else
            throw new RuntimeException("syntax error!!");
    }

    private static int bexpr() {
        int l = aexpr();
        if (lexemes[idx].type >= EQUAL_OP && lexemes[idx].type <= LESS_EQUAL_OP) {
            int tmp = lexemes[idx].type;
            idx++;
            isBinary = true;
            int r = aexpr();
            switch (tmp) {
                case EQUAL_OP:
                    return l == r ? 1 : 0;
                case NOT_EQUAL_OP:
                    return l != r ? 1 : 0;
                case GREATER_OP:
                    return l > r ? 1 : 0;
                case LESS_OP:
                    return l < r ? 1 : 0;
                case GREATER_EQUAL_OP:
                    return l >= r ? 1 : 0;
                case LESS_EQUAL_OP:
                    return l <= r ? 1 : 0;
            }
        }
        return l;
    }

    private static String expr() {
        isBinary = false;
        idx = 0;
        int res = bexpr();
        if (isBinary) {
            if (res != 0) {
                return "true";
            } else {
                return "false";
            }
        } else {
            return String.valueOf(res);
        }
    }


    static void input() {
        int i = 0;
        System.out.print(">> ");
        Scanner sc=new Scanner(System.in);
        inputStr=sc.nextLine();
        inputStr+='\n';
        inputIdx=0;
        getChar();
        do {
            lex();
            lexemes[i] = new Lexeme();
            lexemes[i].type = nextToken;
            lexemes[i].data = lexeme;
            i++;
        } while (nextToken != EOF && nextToken != UNKNOWN_OP);
    }

    public static void main(String[] args) {
        while (true) {
            input();
//            int i = 0;
//            while (lexemes[i].type != EOF && lexemes[i].type != UNKNOWN_OP) {
//                System.out.println("type: " + lexemes[i].type + "   data: " + lexemes[i].data);
//                i++;
//            }
            if (lexemes[0].type == EOF)
                break;
            try {
                String res = expr();
                if(lexemes[idx].type!=EOF)
                    throw new RuntimeException("syntax error!!");
                System.out.println(">> " + res);
            } catch (RuntimeException e) {
                System.out.println(">> " + e.getMessage());
            }
        }
    }

    private static void addChar() {
        lexeme+=nextChar;
    }

    static void getChar() {
        nextChar=inputStr.charAt(inputIdx++);
        if (nextChar != '\n') {
            if (Character.isDigit(nextChar))
                charClass = DIGIT;
            else if (nextChar == '=')
                charClass = EQUAL;
            else if (nextChar == '!' || nextChar == '>' || nextChar == '<')
                charClass = LOGIC;
            else if (nextChar == '*' || nextChar == '/' || nextChar == '+' || nextChar == '-')
                charClass = ARITH;
            else if (nextChar == '(' || nextChar == ')')
                charClass = PAREN;
            else
                charClass = UNKNOWN;
        } else
            charClass = EOF;
    }

    static void getNonBlank() {
        while (Character.isWhitespace(nextChar) && nextChar != '\n')
            getChar();
    }

    static void lex() {
        lexeme="";
        getNonBlank();
        switch (charClass) {
            case DIGIT:
                addChar();
                getChar();
                while (charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                nextToken = INT_LIT;
                break;
            case LOGIC:
            case EQUAL:
                addChar();
                getChar();
                while (charClass == EQUAL) {
                    addChar();
                    getChar();
                }
                nextToken = lookup();
                break;
            case ARITH:
            case PAREN:
                addChar();
                getChar();
                nextToken = lookup();
                break;
            case UNKNOWN:
                addChar();
                getChar();
                nextToken = UNKNOWN_OP;
                break;
            case EOF:
                lexeme="EOF";
                nextToken = EOF;
        }
    }

    static int lookup() {
        if (lexeme.equals("+"))
            return ADD_OP;
        if (lexeme.equals("-"))
            return SUB_OP;
        if (lexeme.equals("/"))
            return DIV_OP;
        if (lexeme.equals("*"))
            return MULT_OP;
        if (lexeme.equals("=="))
            return EQUAL_OP;
        if (lexeme.equals("!="))
            return NOT_EQUAL_OP;
        if (lexeme.equals(">"))
            return GREATER_OP;
        if (lexeme.equals(">="))
            return GREATER_EQUAL_OP;
        if (lexeme.equals("<"))
            return LESS_OP;
        if (lexeme.equals("<="))
            return LESS_EQUAL_OP;
        if (lexeme.equals("("))
            return LEFT_PAREN;
        if (lexeme.equals(")"))
            return RIGHT_PAREN;
        return UNKNOWN_OP;
    }

}