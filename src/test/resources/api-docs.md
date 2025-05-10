# FinQA API Documentation

This document provides details on the API endpoints used in the FinQA application, which are tested in the automation framework.

## Base URL

- Development: `https://dev-api.finqa-demo.com`
- QA: `https://qa-api.finqa-demo.com`
- Staging: `https://staging-api.finqa-demo.com`
- Production: `https://api.finqa-demo.com`

## Authentication

All API endpoints require basic authentication with valid user credentials.

Example Authentication Header:
```
Authorization: Basic base64(username:password)
```

## Endpoints

### Account Management

#### Get All Accounts

```
GET /accounts
```

Returns all accounts belonging to the authenticated user.

**Response Body Example:**
```json
[
  {
    "id": "800000",
    "name": "Checking Account",
    "type": "checking",
    "balance": 2547.89,
    "currency": "USD",
    "status": "active",
    "createdAt": "2023-01-15T08:30:00Z",
    "lastUpdated": "2023-05-10T14:20:00Z"
  },
  {
    "id": "800001",
    "name": "Savings Account",
    "type": "savings",
    "balance": 15782.50,
    "currency": "USD",
    "status": "active",
    "createdAt": "2023-01-15T08:35:00Z",
    "lastUpdated": "2023-05-09T11:15:00Z"
  }
]
```

**Status Codes:**
- 200 OK: Request successful
- 401 Unauthorized: Invalid credentials
- 403 Forbidden: User lacks permission to access accounts
- 500 Internal Server Error: Server error

#### Get Account by ID

```
GET /accounts/{accountId}
```

Returns details for a specific account by ID.

**Path Parameters:**
- `accountId` (required): The unique identifier for the account

**Response Body Example:**
```json
{
  "id": "800000",
  "name": "Checking Account",
  "type": "checking",
  "balance": 2547.89,
  "currency": "USD",
  "status": "active",
  "createdAt": "2023-01-15T08:30:00Z",
  "lastUpdated": "2023-05-10T14:20:00Z",
  "accountNumber": "1234567890",
  "routingNumber": "987654321",
  "interestRate": 0.01,
  "owner": {
    "id": "123456",
    "firstName": "John",
    "lastName": "Smith"
  }
}
```

**Status Codes:**
- 200 OK: Request successful
- 401 Unauthorized: Invalid credentials
- 403 Forbidden: User lacks permission to access the account
- 404 Not Found: Account not found
- 500 Internal Server Error: Server error

#### Get Account Transactions

```
GET /accounts/{accountId}/transactions
```

Returns the transaction history for a specific account.

**Path Parameters:**
- `accountId` (required): The unique identifier for the account

**Query Parameters:**
- `startDate` (optional): Filter transactions from this date (format: YYYY-MM-DD)
- `endDate` (optional): Filter transactions to this date (format: YYYY-MM-DD)
- `page` (optional): Page number for pagination (default: 1)
- `limit` (optional): Number of records per page (default: 20, max: 100)
- `type` (optional): Filter by transaction type (deposit, withdrawal, transfer)

**Response Body Example:**
```json
{
  "accountId": "800000",
  "transactions": [
    {
      "id": "tx12345",
      "date": "2023-05-10T14:20:00Z",
      "description": "Monthly savings transfer",
      "amount": -500.00,
      "type": "transfer",
      "category": "savings",
      "balance": 2547.89
    },
    {
      "id": "tx12344",
      "date": "2023-05-05T10:15:00Z",
      "description": "Payroll Deposit",
      "amount": 2000.00,
      "type": "deposit",
      "category": "income",
      "balance": 3047.89
    }
  ],
  "pagination": {
    "currentPage": 1,
    "totalPages": 10,
    "totalTransactions": 186,
    "limit": 20
  }
}
```

**Status Codes:**
- 200 OK: Request successful
- 401 Unauthorized: Invalid credentials
- 403 Forbidden: User lacks permission to access the account
- 404 Not Found: Account not found
- 500 Internal Server Error: Server error

### Funds Transfer

#### Transfer Funds

```
POST /transfers
```

Transfers funds between two accounts owned by the authenticated user.

**Request Body Example:**
```json
{
  "fromAccountId": "800000",
  "toAccountId": "800001",
  "amount": 500.00,
  "description": "Monthly savings transfer"
}
```

**Response Body Example:**
```json
{
  "transferId": "tr67890",
  "status": "completed",
  "fromAccountId": "800000",
  "toAccountId": "800001",
  "amount": 500.00,
  "description": "Monthly savings transfer",
  "timestamp": "2023-05-10T14:20:00Z",
  "fromAccountBalance": 2547.89,
  "toAccountBalance": 16282.50
}
```

**Status Codes:**
- 200 OK: Transfer completed successfully
- 400 Bad Request: Invalid request (e.g., insufficient funds, invalid amount)
- 401 Unauthorized: Invalid credentials
- 403 Forbidden: User lacks permission to transfer funds
- 404 Not Found: One or both accounts not found
- 500 Internal Server Error: Server error

## Error Responses

All error responses follow this format:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient funds in account 800000",
  "timestamp": "2023-05-10T14:20:00Z",
  "path": "/transfers"
}
```

## Rate Limiting

API endpoints are subject to rate limiting to prevent abuse:
- 100 requests per minute for general endpoints
- 10 requests per minute for sensitive operations (e.g., transfers)

When rate limits are exceeded, the API responds with status code 429 (Too Many Requests).

## Test Data

The following test accounts are available in the QA environment:

| Account ID | Name | Type | Balance |
|------------|------|------|---------|
| 800000 | Checking Account | checking | 2,547.89 |
| 800001 | Savings Account | savings | 15,782.50 |
| 800002 | Investment Account | investment | 35,125.75 |
| 800003 | Credit Card | credit | -1,250.30 |

Test credentials:
- Username: `testuser`
- Password: `Test1234!`
