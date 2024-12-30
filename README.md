#  Olympic report application

## 👥 Team Name
-   Deadline

## 👥 Team Member

| Name         | CN-Name   | StudentId  |
|--------------|-----------|------------|
| LiaoZhenghua | 廖正铧    | 20222092   |
| HeHaonan     | 何浩楠    | 20222094   |
| ChenHao      | 陈豪      | 20222090   |

## 🚀 online access
-   [https://8.209.211.89](http://8.209.211.89/)

## 📋 Project Structure
-   0.Project URL  
        -Frontend: https://github.com/Masterhua/vue-frontend-app-project/tree/master  
        -Backend:  https://github.com/Masterhua/Olympic-backend  

-   1.HomeView  
        -1.1 RankList
        -1.2 Navigation Bar

-   2.CountryDetailview  
        -2.1 CountryMedal-Info  
        -2.2 CountrySportList  
        -2.3 CommentList  
        -2.4 Administrator edit data function  

-   3.SportList 
        -3.1 SportDetail  
        -3.2 Administrator edit data function  


-   4.Navigation Bar  
        -4.1 Search Box  
        -4.2 Home|About|Contact  
        -4.3 Login|Logout   
        -4.4 AdminView  
        -4.5 Add country info View  

-   5.CommentList
        -5.1 User submit comment  
        -5.2 CommentList  
        -5.3 Delete comment function  

-   6.AdminView
        -6.1 UserList  
        -6.2 Administrator edit user data function  


-   6.NotFoundView


---    
## 🚀 Component introduction

-   1：The navigation bar is applied to the whole project to realize the jump between each page and the home page.  
        The search box is applied in navigation to jump to the country details corresponding to the full name of the country you inputed.  
        When you typing in the search box, suggestions will be made below based on your input.

        
-   2：Ranklist instead of event lists used to enable you to view the country details, when you click on the country will be redirected to the country details page.  
        Ranklist offer pagesize, where you can enter the country you want to display on each page (min:1 to max:10 and NumberOnly!)  
        And click update, you will see the ranklist updated.   
        The ranklist also includes a page-turning function to see the list of countries on the previous and next page .


-   3: SportList is provided on the country details page, each country has a separate list of sports, the sports list is provided   
        with 1.SportName 2. The number of MEDALS in various sports in that country.  
        When the user clicks on the name of the sport, it will be redirected to the sport details page.


-   4: CommentList is displayed in CountryDetail view to Cheer to the athletes.  
        Users can type in the input box and click Submit, and you can see the user's comments below.
---
