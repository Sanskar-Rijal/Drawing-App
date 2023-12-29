#include<stdio.h>
#include<stdlib.h>
#include<conio.h>
struct node
{
    int info;
    struct node *next;
};
struct node *head=NULL,*ptr,*newptr;
struct node* getnode()
{
    struct node* np;
    np=(struct node*)malloc(sizeof(struct node));
    printf("Enter the data");
    scanf("%d",&np->info);
    np->next =NULL;
    return np;
};
void create()
{
    struct node *last;
    char ch;
    do 
    {
        newptr= getnode();
        if(head== NULL)
        head=newptr;
        else
        {
            last->next=newptr;
        }
        last=newptr;
        printf("do you want to add moer(y/n)");
        scanf(" %c",&ch);
    }
     while(ch=='Y' || ch=='y');
}
void display()
{
    ptr=head;
    printf("the elemlens are \n");
    while(ptr !=NULL)
    {
        printf("%d",ptr->info);
        ptr=ptr->next;
    }
}
void insert()
{

}
void delete()
{

}
int main()
{
    int choice;
    system("cls");
     create();
    while(1){
         printf("Linked list \n1 insert\n2 delete\n3 display \n4 exit");
    scanf("%d",&choice);
    switch (choice)
    {
    case 1:  insert();
        break;
        case 2: delete();
        break;
        case 3: display();
        break;
        case 4: exit(0);
        break;
    default:printf("invalid data");
    }
    getch();
    }
    return 0;
}
