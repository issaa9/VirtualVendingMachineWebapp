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
    if (currentValue.length === 0 && /^[A-F]$/.test(value)) {
        display.innerText = value;
    }
    //RegEx to check second character is a number (1-4)
    else if (currentValue.length === 1 && /^[1-4]$/.test(value)) {
        display.innerText += value;
    }
    enterBtn.disabled = !/^[A-F][1-4]$/.test(display.innerText);  //enable the CLR button if there is a valid code
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

    if (!/^[A-F][1-4]$/.test(enteredCode)) {  //validation of entered code
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
        await addItemToCart(product,false);  //call function to add item to cart and await it

    } catch (error) {
        console.error("Error:", error);  //log error in console
        alert("Error fetching product. Please try again."); //error handling alert
    }

    clearDisplay(); //always clear keypad display after submitting a code
}


//function to dynamically add an item to the cart
async function addItemToCart(product, allItems) {
    let enoughStock = await checkStock(product);  //call stock check function and await it
    let skipAlerts = allItems;  //extra variable to allow skipping the alerts if every item is being added to cart (developer feature)

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

    if (!skipAlerts) {
        alert(`${product.id} - ${product.name} added to cart successfully!`); //successful alert
    }

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
                category: this.getAttribute("data-category"),
                imageUrl: this.getAttribute("data-image")
            };

            //store selected product object for adding to cart later
            selectedProduct = product;

            //populate modal with product details
            document.getElementById("modalProductImage").src = product.imageUrl;
            document.getElementById("modalProductName").innerText = product.name;
            document.getElementById("modalProductCode").innerText = product.id;
            document.getElementById("modalProductCategory").innerText = product.category;
            document.getElementById("modalProductPrice").textContent = parseFloat(product.price).toFixed(2);
            document.getElementById("modalProductStock").innerText = product.stock;

            modal.style.display = "block";  //display the modal
        });
    });

    //function to add the selected item to the cart
    window.addItemToCartFromModal = function () {
        if (selectedProduct) {   //only if the selected product exists
            addItemToCart(selectedProduct,false);  //call existing function and pass in currently selected product, and false to ensure alerts ae displayed as normal
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


//event listeners and functions to handle category viewing and filtering
document.addEventListener("DOMContentLoaded", function () {
    //define constants for use
    const dropdownButton = document.querySelector(".dropdown-button");
    const dropdown = document.querySelector(".dropdown");
    const checkboxes = document.querySelectorAll(".category-checkbox");
    const allItemsCheckbox = document.querySelector('input[value="all"]');

    //shows the dropdown when clicking the button
    dropdownButton.addEventListener("click", function () { //listen for a click only on the dropdown button
        dropdown.classList.toggle("active"); //when the dropdown button is clicked, toggle the active class to show the dropdown
    });

    //hides dropdown when clicking outside of it
    document.addEventListener("click", function (event) { //listen for a click anywhere in the document
        if (!dropdown.contains(event.target)) {  //checks if the click was outside the dropdown
            dropdown.classList.remove("active");  //remove the active toggle to hide the dropdown
        }
    });

    checkboxes.forEach(checkbox => {
        checkbox.addEventListener("change", function () { //adds a listener for any change (checking or unchecking) to every checkbox
            handleCheckboxSelection(this);  //calls the function to handle changes for checkboxes
        });
    });

    //function to process the checkbox selections
    function handleCheckboxSelection(selectedCheckbox) {
        let selectedCategories = Array.from(document.querySelectorAll(".category-checkbox:checked")) //selects all checkboxes that have been checked
            .map(cb => cb.value.toLowerCase());  //creates an array of the values (in lowercase) of all the checked checkboxes

        //if "All Items" is selected, uncheck everything else and show all categories
        if (selectedCheckbox.value === "all" && selectedCheckbox.checked) { //checks if the 'All Items' category is checked
            checkboxes.forEach(cb => {
                if (cb.value !== "all") cb.checked = false;  //unchecks all other boxes
            });
            showAllCategories(); //call function to show all categories
            return;
        } else {
            allItemsCheckbox.checked = false; //else automatically uncheck 'All Items' if another category is selected
        }

        //if no categories are selected, re-check "All Items" and show all items again which is the default (prevent an empty vending machine)
        if (selectedCategories.length === 0) {  //check if the selected categories array is empty
            allItemsCheckbox.checked = true; //if empty, check the 'All Items' box
            showAllCategories();  //also call the function to show all categories
            return;
        }

        updateCategoryFilter(selectedCategories);
    }

    function showAllCategories() {
        //select all product row and category heading elements
        const productRows = document.querySelectorAll(".product-row");
        const categoryHeadings = document.querySelectorAll(".category-heading-container");

        //show all product rows and category headings
        productRows.forEach(row => row.classList.remove("hidden")); //for every product row remove the hidden class
        categoryHeadings.forEach(heading => heading.classList.remove("hidden"));  //for every category heading remove the hidden class
    }

    function updateCategoryFilter(selectedCategories) {
        //select all product row and category heading elements
        const productRows = document.querySelectorAll(".product-row");
        const categoryHeadings = document.querySelectorAll(".category-heading-container");

        //add or remove each product row based on if it is selected or not
        productRows.forEach(row => { //iterate through each product row
            const rowCategory = row.getAttribute("data-category").trim().toLowerCase(); //retrieve the category of the row in lowercase (remember each row has a different category)
            row.classList.toggle("hidden", !selectedCategories.includes(rowCategory)); //toggle the hidden class to be added or removed based on if the retrieved category is in selectedCategories
        });

        //do the same with each category heading
        categoryHeadings.forEach(heading => { //iterate through each category heading
            const headingCategory = heading.getAttribute("data-category").trim().toLowerCase(); //retrieve the category value as lowercase
            heading.classList.toggle("hidden", !selectedCategories.includes(headingCategory)); //add or remove hidden based on if the category is selected
        });
    }
});

//new developer function button to add all items to cart simply
function addAllItemsToCart() {
    let allItems = document.querySelectorAll(".item");

    for (let item of allItems) {
        let product = {
            id: item.getAttribute("data-code"),
            name: item.getAttribute("data-name"),
            price: parseFloat(item.getAttribute("data-price")),
            stock: parseInt(item.getAttribute("data-stock")),
            imageUrl: item.getAttribute("data-image"),
        };

        if (product.stock > 0) { //only add if in stock
            addItemToCart(product,true); //pass in true so alerts for items added to cart are skipped (saves time)
        }
    }
}










