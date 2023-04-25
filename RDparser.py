class Lexeme:
    def __init__(self, type, data):
        self.type = type
        self.data = data


charClass = None
nextToken = None
nextChar = None
lexeme = None

EOF = -1
DIGIT = 0
LOGIC = 1
ARITH = 2
EQUAL = 3
PAREN = 4
UNKNOWN = 99

INT_LIT = 10
ADD_OP = 21
SUB_OP = 22
MULT_OP = 23
DIV_OP = 24
LEFT_PAREN = 25
RIGHT_PAREN = 26
EQUAL_OP = 27
NOT_EQUAL_OP = 28
GREATER_OP = 29
LESS_OP = 30
GREATER_EQUAL_OP = 31
LESS_EQUAL_OP = 32
UNKNOWN_OP = 98

isBinary = None
idx = None
lexemes = []

inputStr = None
inputIdx = None


def factor():
    global idx
    if lexemes[idx].type == LEFT_PAREN:
        idx += 1
        res = aexpr()
        if lexemes[idx].type == RIGHT_PAREN:
            idx += 1
            return res
        else:
            raise Exception("Syntax error!!")
    return number()


def term():
    global idx
    # print("enter term")
    l = factor()
    while True:
        if lexemes[idx].type == ADD_OP or lexemes[idx].type == SUB_OP:
            tmp = lexemes[idx].type
            idx += 1
            r = factor()
            if tmp == ADD_OP:
                l += r
            else:
                l -= r
        else:
            break
    return l


def aexpr():
    global idx
    # print("enter aexpr")
    l = term()
    while True:
        if lexemes[idx].type == MULT_OP or lexemes[idx].type == DIV_OP:
            tmp = lexemes[idx].type
            idx += 1
            r = term()
            if tmp == MULT_OP:
                l *= r
            else:
                l //= r
        else:
            break
    return l


def number():
    global idx
    if lexemes[idx].type == INT_LIT:
        num = int(lexemes[idx].data)
        idx += 1
        return num
    else:
        raise Exception("syntax error!!")


def bexpr():
    global idx, isBinary
    # print("enter bexpr")
    l = aexpr()
    if lexemes[idx].type >= EQUAL_OP and lexemes[idx].type <= LESS_EQUAL_OP:
        tmp = lexemes[idx].type
        idx += 1
        isBinary = True
        r = aexpr()
        if tmp == EQUAL_OP:
            return l == r
        elif tmp == NOT_EQUAL_OP:
            return l != r
        elif tmp == GREATER_OP:
            return l > r
        elif tmp == LESS_OP:
            return l < r
        elif tmp == GREATER_EQUAL_OP:
            return l >= r
        elif tmp == LESS_EQUAL_OP:
            return l <= r
    return l


def expr():
    global isBinary, idx
    isBinary = False
    idx = 0
    res = bexpr()
    if isBinary:
        if res:
            return "true"
        else:
            return "false"
    else:
        return str(res)


def inputExpr():
    global inputStr, inputIdx, lexemes, nextToken
    lexemes.clear()
    inputStr = input(">> ")
    inputStr += '\n'
    inputIdx = 0
    getChar()
    while True:
        lex()
        lexemes.append(Lexeme(nextToken,lexeme))
        if nextToken == EOF or nextToken == UNKNOWN_OP:
            break


def getChar():
    global charClass, nextChar, inputStr, inputIdx
    nextChar = inputStr[inputIdx]
    inputIdx += 1
    if nextChar != '\n':
        if nextChar.isdigit():
            charClass = DIGIT
        elif nextChar == '=':
            charClass = EQUAL
        elif nextChar == '!' or nextChar == '>' or nextChar == '<':
            charClass = LOGIC
        elif nextChar == '*' or nextChar == '/' or nextChar == '+' or nextChar == '-':
            charClass = ARITH
        elif nextChar == '(' or nextChar == ')':
            charClass = PAREN
        else:
            charClass = UNKNOWN
    else:
        charClass = EOF


def addChar():
    global lexeme
    lexeme += nextChar


def getNonBlank():
    global nextChar
    while nextChar.isspace() and nextChar != '\n':
        getChar()


def lex():
    global nextToken, lexeme, charClass, nextChar
    lexeme = "";
    getNonBlank()
    if charClass == DIGIT:
        addChar()
        getChar()
        while charClass == DIGIT:
            addChar()
            getChar()
        nextToken = INT_LIT
    elif charClass == LOGIC or charClass == EQUAL:
        addChar()
        getChar()
        while charClass == EQUAL:
            addChar()
            getChar()
        nextToken = lookup()
    elif charClass == ARITH or charClass == PAREN:
        addChar()
        getChar()
        nextToken = lookup()
    elif charClass == UNKNOWN:
        addChar()
        getChar()
        nextToken = UNKNOWN_OP
    elif charClass == EOF:
        lexeme = 'EOF'
        nextToken = EOF

def lookup():
    if lexeme == "+":
        return ADD_OP
    elif lexeme == "-":
        return SUB_OP
    elif lexeme == "/":
        return DIV_OP
    elif lexeme == "*":
        return MULT_OP
    elif lexeme == "==":
        return EQUAL_OP
    elif lexeme == "!=":
        return NOT_EQUAL_OP
    elif lexeme == ">":
        return GREATER_OP
    elif lexeme == ">=":
        return GREATER_EQUAL_OP
    elif lexeme == "<":
        return LESS_OP
    elif lexeme == "<=":
        return LESS_EQUAL_OP
    elif lexeme == "(":
        return LEFT_PAREN
    elif lexeme == ")":
        return RIGHT_PAREN
    else:
        return UNKNOWN_OP

while True:
    inputExpr()
    if lexemes[0].type == EOF:
        break
    try:
        res = expr()
        print(">>", res);
    except Exception as e:
        print(">>", e)
