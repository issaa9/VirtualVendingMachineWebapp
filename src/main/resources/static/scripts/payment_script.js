//define global variables for payment
let insertedAmount = 0.00;
let remainingAmount = 0.00;
let changeAmount = 0.00;
let latestTransactionId = null;

//function to display the payment modal
function openModal() {
    //fetch the total price of items in the cart
    let cartTotalElement = document.getElementById("totalPrice");
    let cartTotalText = cartTotalElement.innerText.replace("Total: £", ""); //remove other text to extract only the price
    remainingAmount = parseFloat(cartTotalText); //parse total as a float

    //update the modal with the remaining and inserted amounts (and reset the change amount)
    document.getElementById("remainingAmount").innerText = `£${remainingAmount.toFixed(2)}`;
    document.getElementById("insertedAmount").innerText = `£0.00`;
    document.getElementById("changeAmount").innerText = `£0.00`;

    insertedAmount = 0.00; //reset inserted amount
    changeAmount = 0.00 //reset change amount

    //display the modal
    document.getElementById("paymentModal").style.display = "block";
}

//function to insert coins into machine for payment
function insertCoin(amount) {
    if (remainingAmount > 0) {      //only work if remaining amount exceeds 0
        insertedAmount += amount;  //increment inserted amount
        remainingAmount -= amount;  //decrement remaining amount

        //checking if payment can be completed
        if (remainingAmount < 0) {  //if overpaid
            changeAmount = Math.abs(remainingAmount); //convert to positive value
            remainingAmount = 0;

            //display change and confirm button elements
            document.getElementById("changeAmount").innerText = `£${changeAmount.toFixed(2)}`;
            document.getElementById("changeContainer").style.display = "block";
            document.getElementById("confirmPayment").style.display = "block";
        }
        else if (remainingAmount === 0) {  //if paid exactly display only the confirm button (no change)
            document.getElementById("confirmPayment").style.display = "block";
        }

        //dynamically updates inserted and remaining amounts in the UI
        document.getElementById("insertedAmount").innerText = `£${insertedAmount.toFixed(2)}`;
        document.getElementById("remainingAmount").innerText = `£${remainingAmount.toFixed(2)}`;

    }
}

//function to process the payment
function confirmPayment() {
    if (remainingAmount <= 0) {
        //confirmation popup before proceeding
        let paymentConfirmed = window.confirm(`Confirm payment? Total Paid: £${insertedAmount.toFixed(2)} Your change: £${changeAmount.toFixed(2)}.`);

        if (paymentConfirmed) {
            processTransaction(); //process the transaction when payment is confirmed
        }
    }
}

//function to store the transaction in DB, alert the user and reset the page
async function processTransaction() {
    let productQuantities = {};

    //extract product quantities
    for (let productId in cartItems) {
        productQuantities[productId] = cartItems[productId].quantity;
    }

    //ensure paymentReceived is separate and formatted correctly
    let transactionData = {
        productQuantities: productQuantities,  //nested under a separate key
        paymentReceived: insertedAmount
    };

    try {
        console.log("Transaction Data Sent:", JSON.stringify(transactionData)); //logging
        let response = await fetch("/api/transactions/create", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(transactionData)  //ensure JSON structure is correct
        });


        if (response.ok) {  //if response is ok
            let result = await response.json();  //await response properly

            //store transaction ID for receipt viewing
            latestTransactionId = result.transactionId;

            showSuccessModal(result.message);  //display the custom payment success modal
            resetModal();  //reset the payment modal
            closeModal();  //close the modal
            clearCart();   //clear the cart
        } else {
            let errorResponse = await response.json();
            console.error("Transaction Error:", errorResponse);
            alert(`Transaction Failed: ${errorResponse.error || "An unknown error occurred."}`);    //error message
        }
    } catch (error) {
        console.error("Error:", error);
        alert("Payment error. Please try again.");  //error handling
    }
}


//function to reset required elements when payment is made, or modal is closed
function resetModal() {
    //hide extra elements (change display and confirm button), everything else is reset when new modal opens
    document.getElementById("changeContainer").style.display = "none";
    document.getElementById("confirmPayment").style.display = "none";
}

//function to close the payment modal when the x button on it is pressed
function closeModal() {
    resetModal();
    document.getElementById("paymentModal").style.display = "none";
}

//function to display the payment success modal
function showSuccessModal(message) {
    document.getElementById("modalOverlay").style.display = "block";
    document.getElementById("successMessage").innerText = message;
    document.getElementById("successModal").style.display = "block";
}

//function to close the payment success modal
function closeSuccessModal() {
    document.getElementById("modalOverlay").style.display = "none";
    document.getElementById("successModal").style.display = "none";
}

//function to move to receipt page to view the receipt after payment
function viewReceipt() {
    if (!latestTransactionId) {
        alert("No recent transaction found!");
        return;
    }
    window.location.href = `/receipts/${latestTransactionId}`;
}

//function to exit to homepage
function exitToHome() {
    window.location.href = "/"; //endpoint redirects to the home page
}


