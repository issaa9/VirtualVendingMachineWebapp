let provisionalChanges = {};  //hold all temporary stock changes before saving/applying them
let productList = [];  //hold the list of products fetched from backend


//function to render the full products table or update only rows that need it
function renderProducts(products, forceUpdate = false) {
    const productRows = document.getElementById('productRows'); //retrieve the product row HTML elements
    if (forceUpdate) {
        productRows.innerHTML = '';  //clear the rows for full re-render if force update
    }

    products.forEach(product => updateRow(product, forceUpdate)); //update each product row
    updateSaveButtonState(); //update save button state after rendering
}

//function to update (or render) a single product row, either replacing it or adding it in if not present
function updateRow(product, forceUpdate = false) {  //forceUpdate is false by default, unless true is passed in. forceUpdate specifies whether to always update the row even if there are no changes
    const provisional = provisionalChanges[product.id];  //find the provisional stock for the product
    const existingRow = findRowByProductId(product.id);  //find the product row

    if (existingRow && !forceUpdate) {   //if the row exists and not forcing an update

        //checking if we need to update
        const currentStockDisplay = existingRow.children[3].innerHTML;
        const newStockDisplay = getStockDisplay(product, provisional);
        if (currentStockDisplay === newStockDisplay) return;  //if what is currently displayed is the same as what needs to be displayed, return from the function (no update needed)


        updateStockCell(existingRow, product, provisional); //update stock display
        updateActionButtons(existingRow, product, provisional);  //update button states
        return;
    }

    //force updating
    const newRow = createRow(product, provisional); //create new row
    if (existingRow) {
        replaceRow(product, newRow);  //if a row already exists, replace it (because we are force updating)
    } else {
        document.getElementById('productRows').appendChild(newRow); //otherwise add in the new row (first run)
    }
}

//function to check if a row element containing a product ID exists
function findRowByProductId(productId) {
    return [...document.getElementById('productRows').children] //converts all children of productRows into an array
        .find(tr => tr.children[0].textContent.trim() === productId);  //searches for the product ID in the first column of each row
    //will return true or false based on if the row with the passed in ID was found or not
}

//function to generate HTML for the stock display
function getStockDisplay(product, provisional) {
    const provisionalText = provisional !== undefined  //checks if the provisional value is defined ir not
        ? ` <span class="${provisional === 0 ? 'provisional-zero' : 'provisional-stock'}">(${provisional}*)</span>`  //generated the HTML for the styled span provisional text if exists
        : ''; //or empty string if not exists
    return `${product.stock}${provisionalText}`;  //return the current stock with the provisional stock value (if one)
}

//function to update the stock cell in the table
function updateStockCell(row, product, provisional) {
    row.children[3].innerHTML = getStockDisplay(product, provisional);  //update the stock cell in the table using the generated HTML current and provisional stock values
}

//function to update the states of the elements in the actions column
function updateActionButtons(row, product, provisional) {
    const effectiveStock = provisional !== undefined ? provisional : product.stock;  //use provisional stock, or current stock if it doesn't exist
    row.querySelector(`#decrease-${product.id}`).disabled = effectiveStock === 0;  //disable the decrease button if stock is 0
    row.querySelector(`#empty-${product.id}`).disabled = effectiveStock === 0;   //disable the empty button if stock is 0

    const inputField = row.querySelector(`#manual-input-${product.id}`); //select the input field element
    const setBtn = row.querySelector(`#set-btn-${product.id}`); //select the set button
    inputField.value = '';  //clear the input field
    setBtn.disabled = true;  //disable the set button
}

//function to set up a listener for manual stock inputting
function attachSetButtonListener(row, product) {
    const inputField = row.querySelector(`#manual-input-${product.id}`);  //select the input field
    const setBtn = row.querySelector(`#set-btn-${product.id}`);  //select the set button
    inputField.addEventListener('input', () => {  //add event listener to input field
        const val = inputField.value.trim();  //retrive the input value
        setBtn.disabled = val === '' || isNaN(val) || parseInt(val) < 0;  //enable the set button for valid inputs, disable it for invalid/no inputs
    });
}

//function to create a table row
function createRow(product, provisional) {
    const stockDisplay = getStockDisplay(product, provisional); //generate the HTML stock display cell content

    const row = document.createElement('tr');  //create a HTMl table row

    //populate the inner HTML of the row
    row.innerHTML = `
        <td>${product.id}</td>
        <td>${product.name}</td>
        <td>${product.category}</td>
        <td>${stockDisplay}</td>
        <td>
            <button id="increase-${product.id}" onclick="adjustStock('${product.id}', 1)">Increase Stock</button>
            <button id="decrease-${product.id}" onclick="adjustStock('${product.id}', -1)">Decrease Stock</button>
            <button id="empty-${product.id}" onclick="emptyStock('${product.id}')">Empty Stock</button>
            <input type="number" id="manual-input-${product.id}" class="manual-stock-input" placeholder="Set Stock" min="0">
            <button id="set-btn-${product.id}" onclick="submitManualStock('${product.id}')" disabled>Set</button>
        </td>
        <td>
            <label class="switch">
                <input type="checkbox" id="autoUpdate-${product.id}">
                <span class="slider"></span>
            </label>
        </td>
        <td><button class="undo-button" onclick="undoChanges('${product.id}')">Undo</button></td>
    `;

    updateActionButtons(row, product, provisional);     //update the button action button states if needed
    attachSetButtonListener(row, product);              //attach input listener to manage the state of the set button

    return row;  //return the row
}

//function to replace an existing row with a new one
function replaceRow(product, newRow) {
    const oldRow = findRowByProductId(product.id);  //fetch the existing row
    if (oldRow) oldRow.replaceWith(newRow);  //if the existing row is found/exists, replace with the new one
}

//function to update the Save Changes button state based on provisional changes
function updateSaveButtonState() {
    const saveBtn = document.getElementById('saveChangesBtn'); //retrieve the button element
    if (saveBtn) {
        saveBtn.disabled = Object.keys(provisionalChanges).length === 0; //disable the save button if provisional changes is empty
    }
}

//function to increase/decrease stock with the buttons
function adjustStock(productId, change) {
    const currentStock = productList.find(p => p.id === productId).stock; //find the stock of the product passed in
    const provisional = provisionalChanges[productId] ?? currentStock; //if there is already a provisional value for the item use that, otherwise use the current stock
    let newProvisional = provisional + change;  //add the change (1 or -1) to the provisional value to get a new one

    //ensure stock doesn't go below zero
    if (newProvisional < 0) {
        newProvisional = 0;
    }

    //if provisional stock becomes equal to current stock, remove the provisional change (redundant)
    if (newProvisional === currentStock) {
        delete provisionalChanges[productId];
    } else {
        provisionalChanges[productId] = newProvisional;  //otherwise add in the new provisional value
    }

    updateRow(productList.find(p => p.id === productId));  //update only that row
    updateSaveButtonState();
}

//function to empty the stock for a product (set it to 0)
function emptyStock(productId) {
    provisionalChanges[productId] = 0;  //set the provisional stock to 0
    updateRow(productList.find(p => p.id === productId));  //update only that row
    updateSaveButtonState();
}

//function to submit the manual stock value when the 'Set' button is clicked
function submitManualStock(productId) {
    const input = document.getElementById(`manual-input-${productId}`);
    const value = parseInt(input.value);
    setManualStock(productId, value);
}

//function to manually set the stock (using the input box)
function setManualStock(productId, value) {
    const stockValue = parseInt(value);  //retrieve the value from the input box
    const currentStock = productList.find(p => p.id === productId).stock;  //find the current stock for the product

    if (!isNaN(stockValue) && stockValue >= 0) { //validates if the value is a number and greater than 0
        if (stockValue === currentStock) {  //if value is the same as current stock value
            delete provisionalChanges[productId];   //delete the provisional value
        } else {
            provisionalChanges[productId] = stockValue;  //else add the provisional value
        }
    } else {
        delete provisionalChanges[productId]; //if fails the validation, delete the provisional value
    }

    updateRow(productList.find(p => p.id === productId)); //update only that row
    updateSaveButtonState();
}

//function to undo changes to a product row
function undoChanges(productId) {
    delete provisionalChanges[productId];  //delete the provisional value
    updateRow(productList.find(p => p.id === productId));  //update only that row
    updateSaveButtonState();  //update the state of the save button
}

//function to reset all changes
function resetAllChanges() {
    provisionalChanges = {};  //clear all provisional values
    renderProducts(productList, true);  //fully re-render the table
}

//function to save all changes
function saveChanges() {
    if (Object.keys(provisionalChanges).length === 0) {  //check if there are no current provisional changes
        alert("No changes to save.");
        return;  //return from the function if there are no changes
    }

    const updates = [];
    for (const productId in provisionalChanges) { //for every id in provisionalChanges
        updates.push({ //push the id and stock to updates
            id: productId,
            stock: provisionalChanges[productId]
        });
    }

    //send POST request using AJAX to update the stock in the backend (and database)
    fetch('/admin/update-stock', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(updates)
    })
        .then(response => response.text())
        .then(message => {
            alert(message); //display the message as an alert
            provisionalChanges = {};  //clear all provisional changes
            fetchProducts();
            updateSaveButtonState();
        });
}

//function to fetch all products from backend using AJAX
function fetchProducts() {
    fetch('/admin/products') //send fetch request to controller endpoint
        .then(res => res.json())  //parse response as JSON
        .then(products => {
            productList = products;   //save products into list
            renderProducts(productList, true);  //fully render the products list into the table
        });
}

//event listener to fetch products when the page loads
window.addEventListener('DOMContentLoaded', fetchProducts);

