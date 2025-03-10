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

    document.getElementById("enterBtn").disabled = true; //disable ENT button
}


//function to validate and submit entered code
async function submitCode() {
    let display = document.getElementById("keypadDisplay");
    let enteredCode = display.innerText.trim();  //extract entered code from display

    if (!/^[A-D][1-4]$/.test(enteredCode)) {  //validation of entered code
        alert("Invalid item code detected :(  Please try again.");
        return;
    }

    try {
        //fetch product details using an AJAX request
        let response = await fetch("/api/cart/getProduct/" + enteredCode);
        if (!response.ok) {
            return; //return from the function if there's a 'not ok' response
        }

        let product = await response.json(); //else parse the response as JSON
        await addItemToCart(product);  //call function to add item to cart and await it

    } catch (error) {
        console.error("Error:", error);  //log error in console
        alert("Error fetching product. Please try again."); //error handling alert
    }

    clearDisplay(); //always clear keypad display after submitting a code
}


//function to dynamically add an item to the cart
async function addItemToCart(product) {
    let enoughStock = await checkStock(product);  //call stock check function and await it

    if (!enoughStock) return;  //if not enough stock return from function, the alert will already be sent by the checkStock function

    //if enough stock proceed with adding item to cart (or updating quantity)
    if (cartItems[product.id]) {  //checking if the product already exists in the cart
        cartItems[product.id].quantity += 1; //increment its quantity if  already in the cart
    } else {
        cartItems[product.id] = {  //add as new item if not already in cart
            name: product.name,
            price: parseFloat(product.price), //parse the price as a float
            quantity: 1,
            imageUrl: product.imageUrl  //now also store image url
        };
    }
    alert(`${product.id} - ${product.name} added to cart successfully!`); //successful alert
    updateCartDisplay();  //calling function to update the cart display after adding an item
}

async function checkStock(product) {
    let response = await fetch(`/api/cart/checkStock/${product.id}`);

    if (!response.ok) {
        return false;  //return false if stock check fails
    }

    let stock = await response.json();  //await the response

    //retrieve product quantity from cartItems
    let currentQuantity = cartItems[product.id] ? cartItems[product.id].quantity : 0;

    //retrieve product name from cartItems
    let productName = cartItems[product.id] ? cartItems[product.id].name : "this item";

    if (currentQuantity + 1 > stock) {  //check if there's not enough stock
        alert(`Not enough stock available for ${productName}. Only ${stock} left.`); //alert message
        return false;  //if not enough return false to prevent item being added to cart
    }

    return true;  //if enough stock, proceed with adding to cart
}



//function to update cart display
function updateCartDisplay() {
    let cartList = document.getElementById("cartList");
    let checkoutButton = document.getElementById("checkoutBtn");
    let clearCartButton = document.getElementById("clearCartBtn");

    cartList.innerHTML = "";  //clears cart to avoid duplication

    for (let itemCode in cartItems) { //iterate through cart items
        let item = cartItems[itemCode];
        let itemTotalPrice = (item.price * item.quantity).toFixed(2); //handle item(s) price

        let listItem = document.createElement("li"); //create new html list tag
        listItem.classList.add("cart-item");

        //create text container for product name & price
        let itemText = document.createElement("span");
        itemText.classList.add("cart-item-text");
        itemText.textContent = `${item.quantity}x ${item.name} (${itemCode}) - £${itemTotalPrice}`;

        //create button container
        let buttonContainer = document.createElement("div");
        buttonContainer.classList.add("cart-item-buttons");

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

        //create Increase button
        let increaseBtn = document.createElement("button");
        increaseBtn.innerText = "+";
        increaseBtn.classList.add("increase-btn");
        increaseBtn.onclick = function() {
            increaseItem(itemCode);
        };

        //append buttons to button container
        buttonContainer.appendChild(reduceBtn);
        buttonContainer.appendChild(removeBtn);
        buttonContainer.appendChild(increaseBtn);

        //append text and button container to list item
        listItem.appendChild(itemText);
        listItem.appendChild(buttonContainer);

        cartList.appendChild(listItem); //append list item to cart list
    }

    updateTotal();  //call function to update total price

    if (Object.keys(cartItems).length === 0) {  //if cart is empty
        checkoutButton.disabled = true;   //disable checkout button
        clearCartButton.disabled = true;  //disable clear cart button
    } else {                          //if cart is not empty
        checkoutButton.disabled = false;  //enable checkout button
        clearCartButton.disabled = false; //enable clear cart button
    }
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

//function to increase the quantity of an item in the cart
async function increaseItem(itemCode) {
    let enoughStock = await checkStock({ id: itemCode }); //check stock availability first

    if (!enoughStock) {
        return; //prevent increasing quantity if not enough stock
    }
    cartItems[itemCode].quantity += 1; //increment the quantity if enough stock
    updateCartDisplay(); //update the cart view
}




//function to check if the cart is not already empty and then clear it if the user confirms
function checkClearCart() {
    //if cart is already empty do nothing
    if (Object.keys(cartItems).length === 0) {
        return;
    }

    //confirm with user that they want to clear the cart
    let confirmClear = window.confirm("Are you sure you want to clear the cart?");

    if (confirmClear) {  //if they confirm then clear the cart
        clearCart();
    }
}

//function to clear the cart
function clearCart() {
    let cartList = document.querySelector(".cart-list");
    let totalPriceElement = document.querySelector(".total");

    cartItems = {};
    cartList.innerHTML = "";
    totalPriceElement.innerText = "Total: £0.00";
    document.getElementById("checkoutBtn").disabled = true;  //disable checkout button again
    document.getElementById("clearCartBtn").disabled = true; //disable clear cart button again
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


//event listener to allow enhanced product view when double-clicking an item in the VM
document.addEventListener("DOMContentLoaded", function () {
    const items = document.querySelectorAll(".item");
    const modal = document.getElementById("productModal");
    const closeButton = document.querySelector(".close-button");

    let selectedProduct = null;  //define product object

    //function to open the modal and add in the product data
    items.forEach(item => {
        item.addEventListener("dblclick", function () {

            //first check if there is no stock (if there isn't the modal won't be displayed)
            let stock = parseInt(this.getAttribute("data-stock"), 10);
            if (stock === 0) {
                return;
            }

            const product = {  //create product object
                id: this.getAttribute("data-code"),   //retrieving all product attributes
                name: this.getAttribute("data-name"),
                price: this.getAttribute("data-price"),
                stock: this.getAttribute("data-stock"),
                imageUrl: this.getAttribute("data-image")
            };

            //store selected product object for adding to cart later
            selectedProduct = product;

            //populate modal with product details
            document.getElementById("modalProductImage").src = product.imageUrl;
            document.getElementById("modalProductName").innerText = product.name;
            document.getElementById("modalProductCode").innerText = product.id;
            document.getElementById("modalProductPrice").textContent = parseFloat(product.price).toFixed(2);
            document.getElementById("modalProductStock").innerText = product.stock;

            modal.style.display = "block";  //display the modal
        });
    });

    //function to add the selected item to the cart
    window.addItemToCartFromModal = function () {
        if (selectedProduct) {   //only if the selected product exists
            addItemToCart(selectedProduct);  //call existing function and pass in currently selected product
            modal.style.display = "none";  //close the modal after adding to cart
        }
    };

    //close the modal when clicking the close button
    closeButton.addEventListener("click", function () {
        modal.style.display = "none";
    });

    //close the modal when clicking outside the modal
    window.addEventListener("click", function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
});



document.addEventListener("DOMContentLoaded", function () {
    const categorySelect = document.getElementById("categorySelect");

    categorySelect.addEventListener("change", function () {
        const selectedCategory = categorySelect.value;
        const productRows = document.querySelectorAll(".product-row");
        const categoryHeadings = document.querySelectorAll(".category-heading-container");

        productRows.forEach(row => {
            const rowCategory = row.getAttribute("data-category").trim().toLowerCase();

            if (selectedCategory === "all" || rowCategory === selectedCategory.toLowerCase()) {
                row.classList.remove("hidden");
            } else {
                row.classList.add("hidden");
            }
        });

        categoryHeadings.forEach(heading => {
            const headingCategory = heading.getAttribute("data-category").trim().toLowerCase();
            const matchingRow = document.querySelector(`.product-row[data-category="${headingCategory}"]`);

            if (selectedCategory === "all" || headingCategory === selectedCategory.toLowerCase()) {
                heading.classList.remove("hidden");
            } else {
                heading.classList.add("hidden");
            }
        });
    });
});






