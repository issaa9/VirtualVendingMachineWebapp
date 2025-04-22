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

    //hide general instructions and show payment instructions
    document.getElementById("generalInstructions").style.display = "none";
    document.getElementById("paymentInstructions").style.display = "block";
}

//function to simulate inserting a coin into the machine, using animations
function insertCoin(value, event) {
    const coinElement = event.target;  //get the coin element
    const animatedCoin = coinElement.cloneNode(true);  //clone the coin
    animatedCoin.classList.add("coin-fly");  //add the fly class to the clone
    animatedCoin.style.opacity = "1"; //ensure full visibility

    document.body.appendChild(animatedCoin);  //add the coin clone to page

    const coinRect = coinElement.getBoundingClientRect();

    //set the dimensions of the cloned coin based on the original coin's size
    animatedCoin.style.width = `${coinRect.width}px`;
    animatedCoin.style.height = `${coinRect.height}px`;

    //position the clone at the same position as the original
    animatedCoin.style.left = `${coinRect.left}px`;
    animatedCoin.style.top = `${coinRect.top}px`;

    //get the vending machine position
    const vendingMachine = document.querySelector(".vending-machine");
    const vendingRect = vendingMachine.getBoundingClientRect();

    //calculate x and y distance to the vending machine (add on small margins for error)
    const offsetX = vendingRect.left - coinRect.left + 10;
    const offsetY = vendingRect.top - coinRect.top + 20;

    //animate the coin movement by translating to the offset position
    setTimeout(() => {
        animatedCoin.style.transform = `translate(${offsetX}px, ${offsetY}px)`;
    }, 10);

    //when the coin reaches the desired position: make it disappear and trigger the machine glow
    setTimeout(() => {
        animatedCoin.remove(); //remove the coin
        vendingMachine.classList.add("glow-pink");  //add the glow
        setTimeout(() => vendingMachine.classList.remove("glow-pink"), 400);  //remove the glow after the timeout
    }, 800);

    //play the sound
    const sound = new Audio('/sounds/coin-sound.mp3');
    setTimeout(() => {
        sound.play();
    }, 1000);  //play after 2 seconds


    updateInsertedAmount(value); //update the payment values
}



//function to update the amounts after a coin is inserted for payment
function updateInsertedAmount(amount) {
    if (remainingAmount > 0) {      //only work if remaining amount exceeds 0
        insertedAmount = +(insertedAmount + amount).toFixed(2);  //increment inserted amount by the coin value and round to prevent Floating Point Error
        remainingAmount = +(remainingAmount - amount).toFixed(2);  //decrement remaining amount by the coin value and round to prevent Floating Point Error

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
        showConfirm(`Confirm payment? Total Paid: £${insertedAmount.toFixed(2)} Your change: £${changeAmount.toFixed(2)}.`, () => {
            processTransaction(); //on confirm process the transaction
        }, () => {
            console.log("User cancelled payment"); //on cancel, log the cancellation
        });

    }
}

//function to handle the transaction, by storing the transaction in DB, alerting the user and resetting the page
async function processTransaction() {
    let productQuantities = {};

    //extract product quantities
    for (let productId in cartItems) {
        productQuantities[productId] = cartItems[productId].quantity;
    }

    let username = document.querySelector(".user-info span").innerText; //retrieve the username from the page

    //ensure paymentReceived is separate and formatted correctly
    let transactionData = {
        productQuantities: productQuantities,  //nested under a separate key
        paymentReceived: insertedAmount,
        username: username  //now adding username to the transaction data request
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

            await updateStockForPurchasedItems();  //call method to handle proper stock updates and await it
            showSuccessModal(result.message);  //display the custom payment success modal
            resetModal();  //reset the payment modal
            closeModal();  //close the modal
            clearCart();   //clear the cart

            //reset smart recommendations toggle, highlights and tooltips after purchase
            const recommendToggle = document.getElementById("recommendToggle");
            if (recommendToggle && recommendToggle.checked) {
                recommendToggle.checked = false; //uncheck the toggle switch
                document.querySelectorAll(".item.highlight-recommend").forEach(item => { //for each recommended item
                    item.classList.remove("highlight-recommend"); //remove highlight
                    item.removeAttribute("data-tooltip"); //remove tooltip
                });
            }

            //if restock alert exists, display it
            if (result.hasOwnProperty("restockAlert")) {
                showAlert(result.restockAlert);
            }
        } else {
            let errorResponse = await response.json();
            console.error("Transaction Error:", errorResponse);
            showAlert(`Transaction Failed: ${errorResponse.error || "An unknown error occurred."}`);    //error message
        }
    } catch (error) {
        console.error("Error:", error);
        showAlert("Payment error. Please try again.");  //error handling
    }
}

//function to ensure stock display is updated for every item in the cart
async function updateStockForPurchasedItems() {
    for (let productId in cartItems) {  //for each item in the cart
        await updateStockDisplay(productId);  //call function to update the display and await it
    }
}

//function to handle the stock display updates, including making an item unavailable if there is no stock
async function updateStockDisplay(productId) {
    try {
        const response = await fetch(`/api/cart/checkStock/${productId}`);
        const stock = await response.json();
        const itemElement = document.getElementById(`product-${productId}`);

        //update stock data attribute in HTML page
        itemElement.setAttribute('data-stock', stock);

        //select the overlay element (the X to cover items)
        const overlay = itemElement.querySelector('.out-of-stock-overlay');

        //update the visual display
        if (stock === 0) { //if stock is now 0
            itemElement.classList.add('out-of-stock');  //add out of stock class to block the item from being accessed
            overlay.classList.remove('hidden'); //make sure overlay is not in hidden state
            overlay.classList.add('visible'); //make sure overlay is visible
        } else {
            itemElement.classList.remove('out-of-stock');//ensure out of stock class is not present
            overlay.classList.remove('visible'); //prevent visible state
            overlay.classList.add('hidden'); //hide the overlay
        }
    } catch (error) {
        console.error('Error updating stock for product:', productId, error); //error handling
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

    //hide payment instructions and show general instructions
    document.getElementById("paymentInstructions").style.display = "none";
    document.getElementById("generalInstructions").style.display = "block";
}

//function to display the payment success modal
function showSuccessModal(message) {
    document.getElementById("modalOverlay").style.display = "block";
    document.getElementById("successMessage").innerText = message;
    document.getElementById("successModal").style.display = "block";
    displayPurchasedItems();
}

//function to close the payment success modal
function closeSuccessModal() {
    document.getElementById("modalOverlay").style.display = "none";
    document.getElementById("successModal").style.display = "none";
}

//function to display purchased item images in the success modal
function displayPurchasedItems() {
    let purchasedItemsDiv = document.querySelector(".purchased-items-display");
    purchasedItemsDiv.innerHTML = ""; //clear display ready to re-use

    for (let itemCode in cartItems) {
        let item = cartItems[itemCode];

        //create container for the item
        let itemContainer = document.createElement("div");
        itemContainer.classList.add("purchased-item");

        //create image element
        let itemImage = document.createElement("img");
        itemImage.src = cartItems[itemCode].imageUrl;
        itemImage.alt = item.name;
        itemImage.classList.add("purchased-item-image");

        //create p element
        let itemText = document.createElement("p");
        itemText.textContent = `${item.quantity}x ${item.name}`;
        itemText.classList.add("purchased-item-text");

        //add elements to the container
        itemContainer.appendChild(itemImage);
        itemContainer.appendChild(itemText);

        //append container to purchased-items-display div
        purchasedItemsDiv.appendChild(itemContainer);
    }
}
//function to move to receipt page to view the receipt after payment
function viewReceipt() {
    if (!latestTransactionId) {
        showAlert("No recent transaction found!");
        return;
    }
    window.location.href = `/receipts/${latestTransactionId}`;
    closeSuccessModal();
}

//function to exit to homepage
function exitToHome() {
    window.location.href = "/home"; //endpoint redirects to the home page
}

//function to download the receipt
async function downloadReceipt() {
    const receiptElement = document.getElementById('receiptBox'); //retrieve receipt box element

    if (!receiptElement) {
        showAlert("Receipt content not found.");  //in case of an error trying to retrieve the receipt box
        return;
    }

    const transactionId = receiptElement.querySelector('span[th\\:text="${transaction.id}"]')
        ? receiptElement.querySelector('span[th\\:text="${transaction.id}"]').innerText  //retrieve the transaction ID
        : document.querySelector('.receipt-box').innerText.match(/Transaction ID:\s*(\d+)/)[1]; //if the ID can't be found fallback to use RegEx to try extract it from the receipt box

    const canvas = await html2canvas(receiptElement, { scale: 2 });  //render the receipt element into a canvas
    const imgData = canvas.toDataURL('image/png'); //convert the canvas into an image

    const pdf = new jspdf.jsPDF('p', 'mm', 'a4'); //initialise new jsPDF document and set the parameters (portrait mode, millimeters unit, a4 size)

    const imgProps = pdf.getImageProperties(imgData);  //fetch the dimensions of the canvas image
    const pdfWidth = pdf.internal.pageSize.getWidth(); //set the page width
    const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width; //calculate a proportional page height

    pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight); //add in the image with the set width and height from above

    pdf.save(`Transaction_${transactionId}_Receipt.pdf`); //save the file with the transaction ID in the name
}



//function for back button to navigate back to previous page
function goBack() {
    if (document.referrer) {  //if came from another page
        window.history.back();  //go back to the page
    } else {
        window.location.href = '/home'; //else fallback to homepage
    }
}




