let cartItems = {};  //global variable for cart

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
    let enteredCode = display.innerText.trim();

    if (!/^[A-D][1-4]$/.test(enteredCode)) {
        alert("Invalid item code detected :(  Please try again.");
        return;
    }

    //fetch product details using AJAX
    fetch("/api/cart/getProduct/" + enteredCode)
        .then(response => {
            if (!response.ok) {
                throw new Error("Product not found");
            }
            return response.json();
        })
        .then(product => {
            addItemToCart(product);
            alert(`${product.id} - ${product.name} added to cart successfully!`);
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Error fetching product. Please try again.");
        });

    clearDisplay();
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
            price: parseFloat(product.price),
            quantity: 1
        };
    }

    updateCartDisplay();
}

//function to update cart display
function updateCartDisplay() {
    let cartList = document.getElementById("cartList");

    cartList.innerHTML = "";

    for (let itemCode in cartItems) {
        let item = cartItems[itemCode];
        let itemTotalPrice = (item.price * item.quantity).toFixed(2);

        let listItem = document.createElement("li");
        listItem.textContent = `${item.quantity}x ${item.name} (${itemCode}) - £${itemTotalPrice}`;
        cartList.appendChild(listItem);
    }

    updateTotal();
}

//function to update the total price as items are added to cart
function updateTotal() {
    let totalPriceElement = document.getElementById("totalPrice");
    let total = 0;

    for (let itemCode in cartItems) {
        let item = cartItems[itemCode];
        total += item.price * item.quantity;
    }

    totalPriceElement.textContent = `Total: £${total.toFixed(2)}`; // Format total to 2 decimal places
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




