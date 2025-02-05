//define global variables for payment
let insertedAmount = 0.00;
let remainingAmount = 0.00;

//function to display the payment modal
function openModal() {
    //fetch the total price of items in the cart
    let cartTotalElement = document.getElementById("totalPrice");
    let cartTotalText = cartTotalElement.innerText.replace("Total: £", ""); //remove other text to extract only the price
    remainingAmount = parseFloat(cartTotalText); //parse total as a float

    //update the modal with the remaining and inserted amounts
    document.getElementById("remainingAmount").innerText = `£${remainingAmount.toFixed(2)}`;
    document.getElementById("insertedAmount").innerText = `£0.00`;
    insertedAmount = 0.00; //reset inserted amount

    //display the modal
    document.getElementById("paymentModal").style.display = "block";
}

//function to insert coins into machine for payment
function insertCoin(amount) {
    if (remainingAmount > 0) {      //only work if remaining amount exceeds 0
        insertedAmount += amount;
        remainingAmount -= amount;

        //prevents negative remaining amount
        if (remainingAmount < 0) {
            remainingAmount = 0;
        }

        //dynamically updates inserted and remaining amounts in the UI
        document.getElementById("insertedAmount").innerText = `£${insertedAmount.toFixed(2)}`;
        document.getElementById("remainingAmount").innerText = `£${remainingAmount.toFixed(2)}`;
    }
}

//function to close the payment modal when the x button on it is pressed
function closeModal() {
    document.getElementById("paymentModal").style.display = "none";
}


