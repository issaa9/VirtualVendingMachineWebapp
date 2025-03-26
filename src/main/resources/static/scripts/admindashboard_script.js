let provisionalChanges = {};  //hold all temporary stock changes before saving/applying them
let productList = [];  //hold the list of products fetched from backend

//function to dynamically add rows of products to the table, and update them and their changes
function renderProducts(products) {
    const productRows = document.getElementById('productRows'); //retrieve the product row HTML elements
    productRows.innerHTML = '';  //clear the rows
    products.forEach(product => {  //iterate for every product
        const provisional = provisionalChanges[product.id]; //retrieve the provisional stock value for the product (if exists)
        const provisionalText = provisional !== undefined //only if the provisional value is defined
            ? ` <span class="${provisional === 0 ? 'provisional-zero' : 'provisional-stock'}">(${provisional}*)</span>`  //create the provisional text, adding in the value and style it using the CSS classes
            : '';

        const row = document.createElement('tr'); //create a HTML row
        //add in all the required HTML
        row.innerHTML = `   
            <td>${product.id}</td>
            <td>${product.name}</td>
            <td>${product.category}</td>
            <td>${product.stock}${provisionalText}</td>
            <td>
                <button onclick="adjustStock('${product.id}', 1)">Increase</button>
                <button onclick="adjustStock('${product.id}', -1)">Decrease</button>
                <button onclick="emptyStock('${product.id}')">Empty Stock</button>
                <input type="number" class="manual-stock-input" placeholder="Set Stock" oninput="setManualStock('${product.id}', this.value)">
            </td>
            <td>
                <label class="switch">
                    <input type="checkbox" id="autoUpdate-${product.id}">
                    <span class="slider"></span>
                </label>
            </td>
            <td><button class="undo-button" onclick="undoChanges('${product.id}')">Undo</button></td>
        `;
        productRows.appendChild(row); //add the row in to the table
    });
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

    renderProducts(productList);  //render the products
}

//function to empty the stock for a product (set it to 0)
function emptyStock(productId) {
    provisionalChanges[productId] = 0;  //set the provisional stock to 0
    renderProducts(productList);  //render the products
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

    renderProducts(productList); //render the products
}


//function to undo changes to a product row
function undoChanges(productId) {
    delete provisionalChanges[productId];  //delete the provisional value
    renderProducts(productList);  //render the products
}

//function to reset all changes
function resetAllChanges() {
    provisionalChanges = {};  //clear all provisional values
    renderProducts(productList);  //render the products
}


//function to save all changes
function saveChanges() {

    const updates = [];
    for (const productId in provisionalChanges) { //for every id in provisionalChanages
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
            fetchProducts();  //refresh the products
        });
}

//function to fetch all products from backend using AJAX
function fetchProducts() {
    fetch('/admin/products') //send fetch request to controller endpoint
        .then(res => res.json())  //parse response as JSON
        .then(products => {
            productList = products;   //save products into list
            renderProducts(productList);  //render the products list into the table
        });
}

//event listener to fetch products when the page loads
window.addEventListener('DOMContentLoaded', fetchProducts);

