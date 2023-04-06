#include <iostream>
#include <string>
#include <stdexcept>
using namespace std;

string input;
int idx;

int factor();
int term();
int aexpr();
int bexpr();
int number();
int dec();

int expr()
{
    return bexpr();
}

int bexpr()
{
    int l = aexpr();
    if (idx < input.size())
    {
        char op = input[idx];
        if (op == '=' || op == '!' || op == '<' || op == '>')
        {
            if (idx + 1 < input.size() && input[idx + 1] == '=')
                idx += 2;
            else
                idx++;
            int r = aexpr();
            switch (op)
            {
            case '=':
                return l == r;
            case '!':
                return l != r;
            case '<':
                if (input[idx - 1] == '=')
                    return l <= r;
                else
                    return l < r;
            case '>':
                if (input[idx - 1] == '=')
                    return l >= r;
                else
                    return l > r;
            }
        }
    }
    return l;
}

int aexpr()
{
    int l = term();
    while (idx < input.size())
    {
        char op = input[idx];
        if (op == '*' || op == '/')
        {
            idx++;
            int r = term();
            if (op == '*')
                l *= r;
            else
                l /= r;
        }
        else
            break;
    }
    return l;
}

int term()
{
    int l = factor();
    while (idx < input.size())
    {
        char op = input[idx];
        if (op == '+' || op == '-')
        {
            idx++;
            int r = factor();
            if (op == '+')
                l += r;
            else
                l -= r;
        }
        else
        {
            break;
        }
    }

    return l;
}

int factor()
{
    if (input[idx] == '(')
    {
        idx++;
        int result = aexpr();
        if (input[idx] == ')')
        {
            idx++;
            return result;
        }
        else
        {
            throw runtime_error("Mismatched parentheses");
        }
    }
    return number();
}

int number()
{
    int res = 0;
    while (idx < input.size() && isdigit(input[idx]))
        res = res * 10 + dec();
    return res;
}

int dec()
{
    return input[idx++] - '0';
}

string remove_space(string str)
{
    string ret = "";
    for (int i = 0; i < str.size(); i++)
    {
        if (!isspace(str[i]))
            ret += str[i];
        //수식에 나올수 없는 문자가 나올경우 예외처리 해줘야할듯
    }
    return ret;
}

int main()
{
    cout << ">> ";
    getline(cin, input);
    input = remove_space(input);
    idx = 0;

    cout << ">> ";
    try
    {
        int result = expr();
        cout << result << '\n';
    }
    catch (runtime_error e)
    {
        cout << e.what() << '\n';
    }
    return 0;
}
