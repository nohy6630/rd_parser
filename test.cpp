#include<stdio.h>

int main()
{
    char c;
    while((c=getc(stdin))!='\n')
    {
        printf("%c",c);
    }
}