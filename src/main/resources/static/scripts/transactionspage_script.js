//event listener to retrieve the username after the full HTML document has loaded
document.addEventListener("DOMContentLoaded", async function () {
    let usernameElement = document.querySelector(".user-info span");  //select user info element
    let username = usernameElement.innerText.trim();  //extract the username from the user-info

    if (!username || username === "Guest") {  //handling in case no username, or Guest login
        console.warn("User not logged in. Transactions cannot be fetched."); //set warning instead of error - better practice
        return;
    }

    await fetchUserTransactions(username);  //call function to fetch the transactions and await it because it is asynchronous
});


//function to fetch transactions from the controller using AJAX and update the table
async function fetchUserTransactions(username) {
    try {
        let response = await fetch(`/api/transactions/user?username=${encodeURIComponent(username)}`); //AJAX request

        if (!response.ok) {
            alert("Failed to fetch transactions."); //handle bad response
        }

        let transactions = await response.json();
        populateTransactionTable(transactions);  //call function to populate the table, passing in the transactions list
    } catch (error) {
        console.error("Error fetching transactions:", error); //error handling
    }
}

//function to populate the transactions table
function populateTransactionTable(transactions) {
    let tableBody = document.querySelector(".transactions-table tbody");
    tableBody.innerHTML = ""; //clear all previous content in the table

    if (transactions.length === 0) { //check if there's no transactions for the user
        tableBody.innerHTML = "<tr><td colspan='6'>No transactions found.</td></tr>"; //add in a message
        return;
    }

    transactions.forEach(transaction => {  //iterate for each transaction
        let row = document.createElement("tr");  //create a row

        //separating out date and time
        let transactionDate = new Date(transaction.transactionDate);
        let formattedDate = transactionDate.toLocaleDateString();
        let formattedTime = transactionDate.toLocaleTimeString();
        
        //add in all the transaction data
        row.innerHTML = `
            <td>${transaction.id}</td>
            <td>${formattedDate}</td>
            <td>${formattedTime}</td>
            <td>£${transaction.totalCost.toFixed(2)}</td>
            <td>£${transaction.paymentReceived.toFixed(2)}</td>
            <td>£${transaction.changeGiven.toFixed(2)}</td>
            <td><button onclick="viewReceipt(${transaction.id})">View</button></td>
        `;

        tableBody.appendChild(row);  //append the row to the table
    });
}

//function to view receipt for the specific transaction (in separate tab) when the button is clicked
function viewReceipt(transactionId) {
    window.location.href = `/receipts/${transactionId}`;  //for now navigate to new page, potentially could change this part later
}
