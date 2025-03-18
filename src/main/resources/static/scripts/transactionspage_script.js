const usernameElement = document.querySelector(".user-info span");  //select user info element
const username = usernameElement.innerText.trim();  //extract the username from the user-info


//event listener to retrieve the username after the full HTML document has loaded
document.addEventListener("DOMContentLoaded", async function () {

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
        tableBody.innerHTML = "<tr><td colspan='6'>No Transactions Found.</td></tr>"; //add in a message
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
    window.location.href = `/receipts/${transactionId}`;  //navigate to receipt page with specific transaction id
}

//function to apply filters by querying to the backend and displaying the response using AJAX
function applyFilters() {
    //retrieve filter values from the HTML elements
    let transactionId = document.getElementById("transactionId").value;
    let startDate = document.getElementById("startDate").value;
    let endDate = document.getElementById("endDate").value;
    let minTotalCost = document.getElementById("minTotalCost").value;
    let maxTotalCost = document.getElementById("maxTotalCost").value;
    let minPayment = document.getElementById("minPayment").value;
    let maxPayment = document.getElementById("maxPayment").value;
    let minChange = document.getElementById("minChange").value;
    let maxChange = document.getElementById("maxChange").value;

    //create the URL parameters for the query
    let queryParams = new URLSearchParams({
        transactionId, startDate, endDate, minTotalCost, maxTotalCost,
        minPayment, maxPayment, minChange, maxChange, username
    });

    //send an AJAX fetch request to the controller method endpoint with the query parameters sent over
    fetch(`api/transactions/filter?${queryParams.toString()}`)
        .then(response => response.json())  //parse the response
        .then(data => populateTransactionTable(data))  //call function to populate the table, using the response data
        .catch(error => console.error("Error fetching transactions:", error));  //handle errors
}

//function to reset filters
function resetFilters() {
    //clear the filter HTML elements
    document.getElementById("transactionId").value = "";
    document.getElementById("startDate").value = "";
    document.getElementById("endDate").value = "";
    document.getElementById("minTotalCost").value = "";
    document.getElementById("maxTotalCost").value = "";
    document.getElementById("minPayment").value = "";
    document.getElementById("maxPayment").value = "";
    document.getElementById("minChange").value = "";
    document.getElementById("maxChange").value = "";

    applyFilters(); //reload all transactions by applying with no filters on
}




