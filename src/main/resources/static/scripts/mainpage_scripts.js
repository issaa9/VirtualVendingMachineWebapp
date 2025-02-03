let cartItems = {};  //global object for cart to store items dynamically

//function to update the keypad display
function updateDisplay(value) {
    let display = document.getElementById("keypadDisplay");
    let enterBtn = document.getElementById("enterBtn");

    //get current display value
    let currentValue = display.innerText === "_" ? "" : display.innerText;

    //if there's already 2 characters, block more input
    if (currentValue.length >= 2) {
        return;
    }

    //RegEx to check first character is a letter (A-D)
    if (currentValue.length === 0 && /^[A-D]$/.test(value)) {
        display.innerText = value;
    }
    //RegEx to check second character is a number (1-4)
    else if (currentValue.length === 1 && /^[1-4]$/.test(value)) {
        display.innerText += value;
    }
    enterBtn.disabled = !/^[A-D][1-4]$/.test(display.innerText);  //enable the CLR button if there is a valid code
}

//function to clear the keypad display
function clearDisplay() {
    document.getElementById("keypadDisplay").innerText = "_";  //clear display back to default value
    document.getElementById("enterBtn").disabled = true; //always ensure the CLR button is disabled when clearing the display
}


//function for DEL button to remove last character
function deleteLastCharacter() {
    let display = document.getElementById("keypadDisplay");
    let currentText = display.innerText;

    //only delete if there is text(letter or letter+number)
    if (currentText.length > 0 && currentText !== "_") {
        display.innerText = currentText.slice(0, -1);
    }

    //if display becomes empty, put placeholder "_" back in
    if (display.innerText.length === 0) {
        display.innerText = "_";
    }

    document.getElementById("enterBtn").disabled = true; //disable CLR button
}


//function to validate and submit entered code
function submitCode() {
    let display = document.getElementById("keypadDisplay");
    let enteredCode = display.innerText.trim();  //extract entered code from display

    if (!/^[A-D][1-4]$/.test(enteredCode)) {  //validation of entered code
        alert("Invalid item code detected :(  Please try again.");
        return;
    }

    //fetch product details using an AJAX request
    fetch("/api/cart/getProduct/" + enteredCode)
        .then(response => {
            if (!response.ok) {
                throw new Error("Product not found"); //throw an error for a 'not ok' response
            }
            return response.json(); //else parse the response as JSON
        })
        .then(product => { //processing product data
            addItemToCart(product);  //call function to add item to cart
            alert(`${product.id} - ${product.name} added to cart successfully!`); //successful alert
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Error fetching product. Please try again."); //error handling alert
        });

    clearDisplay(); //always clear keypad display after submitting a code
}

//function to dynamically add an item to the cart
function addItemToCart(product) {
    let cartList = document.getElementById("cartList");

    //checking if the product already exists in the cart
    if (cartItems[product.id]) {
        cartItems[product.id].quantity += 1; //increment its quantity if  already in the cart
    } else {
        cartItems[product.id] = {  //add as new item if not already in cart
            name: product.name,
            price: parseFloat(product.price), //parse the price as a float
            quantity: 1
        };
    }

    updateCartDisplay();  //calling function to update the cart display after adding an item
}

//function to update cart display
function updateCartDisplay() {
    let cartList = document.getElementById("cartList");

    cartList.innerHTML = "";  //clears cart to avoid duplication

    for (let itemCode in cartItems) { //iterate through cart items
        let item = cartItems[itemCode];
        let itemTotalPrice = (item.price * item.quantity).toFixed(2); //handle item(s) price

        let listItem = document.createElement("li"); //create new html list tag
        listItem.classList.add("cart-item");

        listItem.textContent = `${item.quantity}x ${item.name} (${itemCode}) - £${itemTotalPrice}`; //formats text

        //create reduce button
        let reduceBtn = document.createElement("button");
        reduceBtn.innerText = "-";
        reduceBtn.classList.add("reduce-btn");
        reduceBtn.onclick = function() {
            reduceItem(itemCode); //call function to reduce item
        };

        //create remove button
        let removeBtn = document.createElement("button");
        removeBtn.innerText = "X";
        removeBtn.classList.add("remove-btn");
        removeBtn.onclick = function() {
            removeItem(itemCode); //call function to remove item
        };

        //append buttons to list item
        listItem.appendChild(reduceBtn);
        listItem.appendChild(removeBtn);

        cartList.appendChild(listItem); //append list item to cart list
    }

    updateTotal();  //call function to update total price
}

//function to update the total price as items are added to cart
function updateTotal() {
    let totalPriceElement = document.getElementById("totalPrice");
    let total = 0; //start at 0

    for (let itemCode in cartItems) { //iterate through each item code cart items
        let item = cartItems[itemCode];
        total += item.price * item.quantity; //update total based on each item's price and quantity
    }

    totalPriceElement.textContent = `Total: £${total.toFixed(2)}`; //format total to 2 decimal places and display it
}


//function to completely remove an item from the cart
function removeItem(itemCode) {
    delete cartItems[itemCode];  //remove the item from cart object
    updateCartDisplay();  //update the cart view
}

//function to reduce quantity by 1 (removes item if quantity is 0)
function reduceItem(itemCode) {
    if (cartItems[itemCode].quantity > 1) {
        cartItems[itemCode].quantity--;  //reduce quantity by 1, if greater than 1
    } else {
        delete cartItems[itemCode];  //remove item if quantity is 1
    }
    updateCartDisplay();  //update the cart view
}



//function to clear the cart
function clearCart() {
    let cartList = document.querySelector(".cart-list");
    let totalPriceElement = document.querySelector(".total");

    cartItems = {};
    cartList.innerHTML = "";
    totalPriceElement.innerText = "Total: £0.00";
}

//allows user to use their keyboard keys as alternatives to ENT and DEL buttons
document.addEventListener("keydown", function(event) {
    let enterButton = document.getElementById("enterBtn");
    let deleteButton = document.getElementById("deleteBtn");

    //if ENTER key is pressed and the ENT button is enabled, trigger an ENT button click
    if (event.key === "Enter" && !enterButton.disabled) {
        enterButton.click();
    }

    //if BACKSPACE or DELETE key is pressed, trigger a DEL button click
    if (event.key === "Backspace" || event.key === "Delete") {
        deleteButton.click();
    }
});




