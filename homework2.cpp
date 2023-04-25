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
            break;
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
            throw runtime_error("syntax error!!");
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

bool isoperator(char c)
{
    return c == '!' || c == '=' || c == '>' || c == '<' || c == '+' || c == '-' || c == '*' || c == '/';
}

bool isbracket(char c)
{
    return c == '(' || c == ')';
}

string remove_space(string str)
{
    string ret = "";
    for (int i = 0; i < str.size(); i++)
    {
        if (!isdigit(str[i]) && !isoperator(str[i]) && !isbracket(str[i]) && !isspace(str[i]))
            throw runtime_error("syntax error!!");
        if (!isspace(str[i]))
            ret += str[i];
    }
    return ret;
}

int main()
{
    while (1)
    {
        cout << ">> ";
        getline(cin, input);
        if (input.size() == 0)
            break;
        idx = 0;
        try
        {
            input = remove_space(input);
            int result = expr();
            cout << ">> " << result << '\n';
        }
        catch (runtime_error e)
        {
            cout << ">> " << e.what() << '\n';
        }
    }
    return 0;
}
