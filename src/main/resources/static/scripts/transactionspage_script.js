//global variables
const usernameElement = document.querySelector(".user-info span");  //select user info element
const username = usernameElement.innerText.trim();  //extract the username from the user-info

let currentSortColumn = null;   //store currently sorted column to keep track
let currentSortDirection = 'asc';   //store current sorting direction to know when to switch to 'desc'
let currentTransactions = [];  //store fetched transactions, so they can be easily sorted

//create a mapping of column header ids to their text
const headersMap = {
    id: { id: "transactionIdHeader", text: "Transaction ID" },
    date: { id: "dateHeader", text: "Date" },
    time: { id: "timeHeader", text: "Time" },
    totalCost: { id: "totalCostHeader", text: "Total Cost" },
    paymentReceived: { id: "paymentReceivedHeader", text: "Payment Received" },
    changeGiven: { id: "changeGivenHeader", text: "Change Given" }
};


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
            showAlert("Failed to fetch transactions."); //handle bad response
        }

        let transactions = await response.json();
        populateTransactionTable(transactions);  //call function to populate the table, passing in the transactions list
        currentTransactions = transactions; //store the transactions so they can be sorted
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
    if (!validateFilters()) return; //if any invalid data dont apply filters

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
        .then(data => {

            currentTransactions = data;  //store the filtered transactions in case they need to be sorted

            if (currentSortColumn) {   //check if there is currently a sort active
                sortTableData(currentSortColumn, currentSortDirection); //apply active sort to filtered data
            } else {
                populateTransactionTable(currentTransactions);  //otherwise display the filtered transactions in the table in default order
            }

        })
        .catch(error => console.error("Error fetching transactions:", error));  //handle errors
}

//function to ensure all filters have valid data in
function validateFilters() {
    const transactionId = document.getElementById("transactionId").value.trim();
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;

    //Transaction ID validation: must be a positive integer
    if (transactionId && (!/^\d+$/.test(transactionId) || parseInt(transactionId) <= 0)) {
        showAlert("Transaction ID must be a Positive Integer.");
        document.getElementById("transactionId").value = ""; //clear the box
        return false;
    }

    //Date range validation: start date must be before end date
    if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
        showAlert("Date From cannot be later than Date To.");

        //clear the boxes
        document.getElementById("startDate").value = "";
        document.getElementById("endDate").value = "";

        return false;
    }

    //Money fields validation: money can't be negative
    const moneyInputs = ['minTotalCost', 'maxTotalCost', 'minPayment', 'maxPayment', 'minChange', 'maxChange'];
    for (const id of moneyInputs) {
        const value = document.getElementById(id).value;
        if (value && parseFloat(value) < 0) {
            showAlert(`Value for ${id.replace(/([A-Z])/g, ' $1')} cannot be negative.`);
            document.getElementById(id).value = "";
            return false;
        }
    }

    return true;
}

//event listener to block invalid input in money fields
document.addEventListener("DOMContentLoaded", () => {
    const moneyInputs = document.querySelectorAll('.money-filter-input');
    moneyInputs.forEach(input => {
        input.addEventListener('input', () => {
            //if a negative value or minus sign appears, remove it immediately
            if (parseFloat(input.value) < 0 || input.value.includes('-')) {
                input.value = '';
            }
        });
    });
});


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


//event listener to ensure money values are always displayed as 2 DP
document.addEventListener("DOMContentLoaded", () => {
    const moneyInputs = document.querySelectorAll('.money-filter-input');
    moneyInputs.forEach(input => {
        input.addEventListener('blur', () => {
            if (input.value) {
                input.value = parseFloat(input.value).toFixed(2);
            }
        });
    });
});



//function to handle the sorting of a column when it is clicked
function sortTableByColumn(column) {
    if (currentTransactions.length === 0) return;  //if no transactions don't attempt to sort

    let previousSortColumn = currentSortColumn; //store previous column, so it can be reset in case the column changes

    if (currentSortColumn === column) { //if we are sorting the same column, switch the direction between ascending and descending
        currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';  //toggle between 'asc' and 'desc' based on the current direction
    } else {     //if sorting a new column
        currentSortColumn = column;  //store the column as the one being currently sorted
        currentSortDirection = 'asc';   //set the direction to ascending which is the default
    }

    sortTableData(column, currentSortDirection)


    highlightSortedColumn(column, previousSortColumn);  //call function to handle highlighting the column and adding/changing the arrow
}

function sortTableData(column, direction) {
    currentTransactions.sort((a, b) => {
        let valA, valB;   //define two variables for comparison

        if (column === 'date') {  //if sorting by date
            valA = new Date(a.transactionDate);   //convert values from strings to dates to enable comparison
            valB = new Date(b.transactionDate);
        } else if (column === 'time') {  //if sorting by date
            valA = new Date(a.transactionDate).getTime();  //convert to dates and retrieve the time to enable comparison
            valB = new Date(b.transactionDate).getTime();
        } else if (!isNaN(a[column])) {   //if sorting with numeric values
            valA = parseFloat(a[column]);   //parse the values as floats
            valB = parseFloat(b[column]);
        } else {   //for all other cases
            valA = a[column];    //leave as strings
            valB = b[column];
        }

        if (valA < valB) return direction === 'asc' ? -1 : 1;  //if value A is less than value B, return -1 if direction is ascending or 1 for descending
        if (valA > valB) return direction === 'asc' ? 1 : -1;  //if value A is greater than value B, return 1 if direction is ascending or -1 for descending
        return 0;  //if equal return 0

        //the column will sort itself based on the values returned from each comparison: -1 will put A above B, 1 puts A below B and 0 keeps the order as it is
    });

    populateTransactionTable(currentTransactions);
}

//function to handle the visual effects for sorting (highlighting, arrow direction and tooltip)
function highlightSortedColumn(column, previousColumn) {

    //reset visuals on the previous sorted column
    if (previousColumn && headersMap[previousColumn]) {
        resetVisuals(headersMap[previousColumn]);
    }

    //add relevant arrow, highlight the selected column and change the tooltip (if required)
    const headerEl = document.getElementById(headersMap[column].id);  //select the highlighted header element
    if (headerEl) {
        headerEl.classList.add('sorted-column');   //add the sorted class list
        const arrow = currentSortDirection === 'asc' ? ' ▲' : ' ▼';  //set the correct arrow direction based on the current sort direction
        headerEl.innerText = headersMap[column].text + arrow;  //add the arrow to the header text
        headerEl.title = currentSortDirection === 'asc' ? "Click to sort descending" : "Click to sort ascending";   //change the tooltip accordingly
    }
}

//function to reset visuals(highlight, arrow text and tooltip) for a header
function resetVisuals(header) {
    const el = document.getElementById(header.id);
    el.classList.remove('sorted-column');   //remove sorted class
    el.innerText = header.text;    //reset text which takes out the arrow
    el.title = "Click to sort ascending";  //set the default tooltip
}

//first define an array of all header IDs using the values from the map
const headers = Object.values(headersMap).map(header => header.id);


//event listener to reset all sorting functionality when clicking outside the headers
document.addEventListener('dblclick', function(event) {


    if (!headers.includes(event.target.id)) {  //only if the click is NOT on any of the headers

        //reset sorting

        sortTableData('id','asc'); //reset the table back to normal (sorted by transactions_id ascending)

        //reset the visuals for the currently sorting column
        if (currentSortColumn && headersMap[currentSortColumn]) {
            resetVisuals(headersMap[currentSortColumn]);
        }

        //reset the variables
        currentSortColumn = '';  //empty current sort column
        currentSortDirection = 'asc';   //set default sort direction
    }
});






