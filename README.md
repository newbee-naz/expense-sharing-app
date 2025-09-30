✅ Steps to Run the Application

1. Clone & Build Project

git clone https://github.com/newbee-naz/expense-sharing-app.git
cd expense-sharing-app
mvn clean install


2. Run Application
mvn spring-boot:run or from IDE run SplitMateApplication class as run as java application


If successful, you’ll see logs like:

Tomcat started on port(s): 8080
Started ExpenseSharingApp in 4.567 seconds

3. Login via Google

Open browser and go to:

http://localhost:8080/oauth2/authorization/google
http://localhost:8080/api/whoami

4. Access H2 Database

Go to:

http://localhost:8080/h2-console

*) there is already some code which generates dummy data in H2 DB

JDBC URL: jdbc:h2:mem:expshare

User: sa

Password: (leave blank)

You can see tables like User, Group, Expense, Settlement.

NOTE: Since OAuth2 is handled automatically in browser login, you can:

Use the session cookie from browser or directly you can use swagger to access api's 

Swagger URL: http://localhost:8080/swagger-ui/index.html#


5. Example API Workflow

Create a Group

POST /api/groups
{
  "name": "Trip to Dubai",
  "members": ["user1@gmail.com", "user2@gmail.com"]
}


Add an Expense

POST /api/expenses
{
  "groupId": 1,
  "amount": 100,
  "description": "Dinner",
  "paidBy": "user1@gmail.com",
  "splitType": "EQUAL"
}


→ Updates balances in the group.

Settle Balance

POST /api/settlements
{
  "payerId": 2,
  "receiverId": 1,
  "amount": 25
}

6. Recompute Balances

You can call:

GET /api/groups/{id}/balances


to recompute from all transactions (avoiding corruption).

7. Shut Down

Stop the app with:

CTRL + C



====================================

List of APIs:
--------------

GET /api/groups/{groupId}/members → List all members of a group with balances

DELETE /api/groups/{groupId}/members/{userId} → Remove a member from a group (owner or self)

DELETE /api/groups/{groupId}/leave → Leave a group (with balance & ownership checks)

POST /api/groups → Create a new group (adds creator as owner + member)

POST /api/groups/{groupId}/members/{userId} → Add a member to a group (only owner allowed)

GET /api/groups → List all groups the current user belongs to

POST /api/expenses → Add a new expense to a group (splits automatically, supports custom split).

GET /api/expenses/group/{groupId} → List all expenses for a specific group.

POST /api/settlements → Make a settlement between two users in a group (payer pays receiver, transactional update of balances).
