//global variables
let provisionalChanges = {};  //hold all temporary stock changes before saving/applying them
let productList = [];         //hold the list of products fetched from backend


    //HELPER FUNCTIONS

//function to return the threshold value for a row
function getThresholdValue(row, product, provisional) {
    //get threshold value from input field if visible and valid, otherwise use provisional or database
    const thresholdInput = row.querySelector('.threshold-input'); //select the threshold input element
    const inputVal = thresholdInput?.value?.trim();  //retrieve its value
    let threshold;
    if (inputVal !== '' && !isNaN(parseInt(inputVal))) {  //if the input value exists and is valid
        threshold = parseInt(inputVal);   //parse it as in integer and store it
    } else if (provisional?.stockThreshold != null) { //else try to retrieve a threshold value from provisionalChanges
        threshold = provisional.stockThreshold;
    } else if (product.stockThreshold != null) {  //else try to retrieve threshold value from backend (database)
        threshold = product.stockThreshold;
    } else {
        threshold = null;  //if no threshold values could be found set it as null
    }
    return threshold;  //return threshold value
}

//retrieves auto-stock update related settings from the row.
function getAutoSettings(row, product) {
    //select the auto update checkbox and threshold input elements
    const autoCheckbox = row.querySelector('.auto-update');
    const thresholdInput = row.querySelector('.threshold-input');

    const threshold = parseInt(thresholdInput?.value ?? "0"); //attempt to parse the threshold input value as an integer or fallback to 0
    const updateInput = row.querySelector('.update-amount-input'); //select update amount input element
    const updateAmount = parseInt(updateInput?.value ?? "0"); //attempt to parse this too, else fallback to 0, same as with threshold
    return { autoCheckbox, threshold, updateInput, updateAmount }; //return the values inside an object
}

//function to update the provisional changes for a product
function updateProvisional(productId, newStock, autoSettings) {
    const product = productList.find(p => p.id === productId); //find the product in the global list
    const currentStock = product.stock;  //set the current stock
    const changeObj = provisionalChanges[productId] || {}; //gets the object storing the changes for that product, or creates it if not one

    //set the changes for the object
    changeObj.stock = newStock;  //set the new stock for the object
    changeObj.autoStockEnabled = autoSettings.autoCheckbox.checked; //set the checkbox state
    changeObj.stockThreshold = autoSettings.threshold; //set the threshold
    changeObj.updateAmount = autoSettings.updateAmount; //set the update amount

    if (newStock === currentStock) {  //if new and current stock are equal
        delete provisionalChanges[productId];  //delete the change object
    } else {
        provisionalChanges[productId] = changeObj;  //else add the new change object in
    }
    updateSaveButtonState();  //update the save button's state accordingly
}

    //HANDLING DISPLAY

//function to render the full products table or update only rows that need it
function renderProducts(products, forceUpdate = false) {
    const container = document.getElementById('productRows'); //retrieve the product row HTML container
    if (forceUpdate) {
        container.innerHTML = '';  //clear the rows for full re-render if force update, so all rows can be re-created
    }

    products.forEach(product => {
        const existingRow = findRowByProductId(product.id);  //find the product row
        if (existingRow && !forceUpdate) {  //if row exists and no force update
            updateRow(product, existingRow); //update the existing row
        } else {
            //else create new row
            const newRow = createRow(product, provisionalChanges[product.id]);
            container.appendChild(newRow); //append the new row to the container
        }
    });
    updateSaveButtonState(); //update save button state after rendering products
}

//function to update a single product row
function updateRow(product, row) {
    const provisional = provisionalChanges[product.id];  //find the provisional stock for the product
    const currentStockDisplay = row.querySelector('.stock-cell').innerHTML; //select the current stock being displayed
    const newStockDisplay = getStockDisplay(product, provisional);   //generate the HTML for the new stock display
    if (currentStockDisplay === newStockDisplay) {  //if the current stock display is the same as the newly generated one
        updateActionButtons(row, product, provisional); //only update the action buttons, don't need to change the stock display
        return;  //return from the function (no more updates needed)
    }
    //update stock display and button states
    row.querySelector('.stock-cell').innerHTML = newStockDisplay;  //update the stock display using the generated HTML
    updateActionButtons(row, product, provisional);  //update button states
}

//function to check if a row element containing a product ID exists
function findRowByProductId(productId) {
    return [...document.getElementById('productRows').children] //converts all children of productRows into an array
        .find(tr => tr.children[0].textContent.trim() === productId);  //searches for the product ID in the first column of each product row
    //will return true or false based on if the row with the passed in ID was found or not
}

//function to generate HTML for the stock display
function getStockDisplay(product, provisional) {
    let displayStock = product.stock; //use product's stock from the database
    let provisionalText = ''; //create provisional text, first as an empty string
    if (provisional !== undefined && provisional.stock !== undefined) {   //if there is a provisional stock value defined
        //create the provisional text span element, conditionally add a css class if the provisional stock is 0
        provisionalText = ` <span class="${provisional.stock === 0 ? 'provisional-zero' : 'provisional-stock'}">(${provisional.stock}*)</span>`;
    } else if (typeof provisional === 'number') { //if provisional is passed in as a number, directly use that instead of the stock attribute for it
        provisionalText = ` <span class="${provisional === 0 ? 'provisional-zero' : 'provisional-stock'}">(${provisional}*)</span>`;
    }
    return `${displayStock}${provisionalText}`; //return the current stock and provisional stock HTML to make up the stock display
}

//function to update the states of the elements in the actions column
function updateActionButtons(row, product, provisional) {
    const effectiveStock = provisional?.stock ?? product.stock; //determine stock using provisional stock if exists, otherwise use current stock

    //check if auto-stock enabled from provisional if not from the checkbox, or if not from the database
    const autoCheckbox = row.querySelector('.auto-update');
    const autoEnabled = autoCheckbox?.checked ?? provisional?.autoStockEnabled ?? product.autoStockEnabled;


    const threshold = getThresholdValue(row, product, provisional); //get threshold value

    //select the empty/decrease buttons
    const decreaseBtn = row.querySelector('.decrease-btn');
    const emptyBtn = row.querySelector('.empty-btn');

    //disable decrease button if stock <= 0 or if auto update is enabled and threshold exists and is greater than or equal to stock
    decreaseBtn.disabled = effectiveStock <= 0 || (autoEnabled && threshold != null && effectiveStock <= threshold);

    //disable empty button if stock <= 0 or if auto update is enabled and threshold exists
    emptyBtn.disabled = effectiveStock <= 0 || (autoEnabled && threshold != null);

    //select the input field and it's set button
    const manualInput = row.querySelector('.manual-stock-input');
    const setBtn = row.querySelector('.set-btn');

    manualInput.value = '';  // clear the input field
    setBtn.disabled = true; // disable the set button
}

//function to set up a listener for manual stock inputting
function attachSetButtonListener(row, product) {
    const inputField = row.querySelector('.manual-stock-input');  //select the input field
    const setBtn = row.querySelector('.set-btn');  //select the set button
    inputField.addEventListener('input', () => {  //add event listener to input field
        const val = inputField.value.trim();  //retrive the input value
        setBtn.disabled = val === '' || isNaN(val) || parseInt(val) < 0;  //enable the set button for valid inputs, disable it for invalid/no inputs
    });
}

//function to create a table row
function createRow(product, provisional) {
    const stockDisplay = getStockDisplay(product, provisional); //generate the HTML stock display cell content
    const row = document.createElement('tr');  //create a HTML table row

    //populate the row with HTML
    row.innerHTML = `
        <td>${product.id}</td>
        <td>${product.name}</td>
        <td>${product.category}</td>
        <td class="stock-cell">${stockDisplay}</td>
        <td>
            <button class="increase-btn" data-id="${product.id}" data-change="1">Increase Stock</button>
            <button class="decrease-btn" data-id="${product.id}" data-change="-1">Decrease Stock</button>
            <button class="empty-btn" data-id="${product.id}">Empty Stock</button>
            <input type="number" class="manual-stock-input" data-id="${product.id}" placeholder="Set Stock" min="0">
            <button class="set-btn" data-id="${product.id}" disabled>Set</button>
        </td>
        <td>
            <label class="switch">
                <input type="checkbox" class="auto-update" data-id="${product.id}">
                <span class="slider"></span>
            </label>
        </td>
        <td>
            <input type="number" class="threshold-input" data-id="${product.id}" style="display: none;" min="0" />
        </td>
        <td>
            <input type="number" class="update-amount-input" data-id="${product.id}" style="display: none;" min="1" />
        </td>
        <td>
            <button class="undo-btn" data-id="${product.id}">Undo</button>
        </td>
    `;
    //select auto-stock fields
    const autoCheckbox = row.querySelector('.auto-update');
    const thresholdInput = row.querySelector('.threshold-input');
    const updateAmountInput = row.querySelector('.update-amount-input');

    if (product.autoStockEnabled) { //if auto stock is enabled for the product in the database
        autoCheckbox.checked = true;  //check the box/flip the switch

        //display the input fields
        thresholdInput.style.display = 'inline-block';
        updateAmountInput.style.display = 'inline-block';

        //clear the input fields (in case theres anything from before)
        thresholdInput.value = product.stockThreshold ?? '';
        updateAmountInput.value = product.updateAmount ?? '';
    }
    attachSetButtonListener(row, product);  //set up the listener for set button
    return row;  //return the HTML row
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
        saveBtn.disabled = Object.keys(provisionalChanges).length === 0; //disable the save button if provisional changes is empty (don't allow saving if there's no changes to save)
    }
}

    //ADMIN ACTIONS

//function to increase/decrease stock with the buttons
function adjustStock(productId, change) {
    const product = productList.find(p => p.id === productId);
    const currentStock = product.stock;
    const row = findRowByProductId(productId);
    const provisional = provisionalChanges[productId]?.stock ?? currentStock;
    let newStock = provisional + change;
    //block stock from going negative
    if (newStock < 0) {
        alert("Stock cannot go below 0.");
        return;
    }
    const autoSettings = getAutoSettings(row, product);
    if (autoSettings.autoCheckbox.checked && !isNaN(autoSettings.threshold) && newStock < autoSettings.threshold) {
        alert("Stock cannot be set below threshold while auto-stock is enabled.");
        return;
    }
    updateProvisional(productId, newStock, autoSettings);
    updateRow(product, row);
}

//function to empty the stock for a product (set it to 0)
function emptyStock(productId) {
    const product = productList.find(p => p.id === productId);
    const row = findRowByProductId(productId);
    const autoSettings = getAutoSettings(row, product);
    updateProvisional(productId, 0, autoSettings);
    updateRow(product, row);
}

//function to submit the manual stock value when the 'Set' button is clicked
function submitManualStock(productId) {
    const input = document.querySelector(`.manual-stock-input[data-id="${productId}"]`);
    const value = parseInt(input.value);
    setManualStock(productId, value);
}

//function to manually set the stock (using the input box)
function setManualStock(productId, value) {
    const stockValue = parseInt(value);  //retrieve the value from the input box
    const product = productList.find(p => p.id === productId);  //find the current stock for the product
    const currentStock = product.stock;
    const row = findRowByProductId(productId);
    const autoCheckbox = row.querySelector('.auto-update');
    const thresholdInput = row.querySelector('.threshold-input');
    const threshold = parseInt(thresholdInput.value);
    if (autoCheckbox.checked && !isNaN(threshold) && stockValue < threshold) {
        alert("Stock cannot be set below threshold while auto-stock is enabled.");
        return;
    }
    const change = provisionalChanges[productId] || {};
    change.stock = stockValue;
    if (!isNaN(stockValue) && stockValue >= 0) { //validates if the value is a number and greater than 0
        if (stockValue === currentStock) {  //if value is the same as current stock value
            delete provisionalChanges[productId];   //delete the provisional value
        } else {
            provisionalChanges[productId] = change;  //else add the provisional value
        }
    } else {
        delete provisionalChanges[productId]; //if fails the validation, delete the provisional value
    }
    updateRow(product, row);
    updateSaveButtonState();
}

//function to undo changes to a product row
function undoChanges(productId) {
    delete provisionalChanges[productId];  //delete the provisional value
    //reset inputs and checkbox
    const product = productList.find(p => p.id === productId);
    const row = findRowByProductId(productId);
    const autoCheckbox = row.querySelector('.auto-update');
    const thresholdInput = row.querySelector('.threshold-input');
    const updateInput = row.querySelector('.update-amount-input');
    //update checkbox state
    autoCheckbox.checked = product.autoStockEnabled;
    //clear threshold and updateAmount values
    thresholdInput.value = product.stockThreshold ?? '';
    updateInput.value = product.updateAmount ?? '';
    //update visibility of input fields
    thresholdInput.style.display = product.autoStockEnabled ? 'inline-block' : 'none';
    updateInput.style.display = product.autoStockEnabled ? 'inline-block' : 'none';
    updateRow(product, row);
    updateSaveButtonState();
}

//function to reset all changes
function resetAllChanges() {
    provisionalChanges = {};  //clear all provisional values
    renderProducts(productList, true);  //fully re-render the table
}

//function to save all changes
async function saveChanges() {
    if (Object.keys(provisionalChanges).length === 0) {  //check if there are no current provisional changes
        alert("No changes to save.");
        return;
    }
    //validation for auto stock settings
    for (const [productId, change] of Object.entries(provisionalChanges)) {
        const product = productList.find(p => String(p.id) === productId);
        const stock = change.stock !== undefined ? change.stock : product?.stock ?? 0;
        const threshold = change.stockThreshold;
        const updateAmount = change.updateAmount;
        const auto = change.autoStockEnabled;
        if (auto) {
            if (threshold === null || isNaN(threshold)) {  //validation in case threshold is missing
                alert(`Cannot save: Stock Threshold is missing for product ${productId}.`);
                return;
            }
            if (updateAmount === null || isNaN(updateAmount)) {  //validation in case update amount is missing
                alert(`Cannot save: Update Amount is missing for product ${productId}.`);
                return;
            }
            if (stock < threshold) {  //validation in case stock is set below the threshold
                alert(`Cannot save: Stock for product ${productId} is below its Threshold (${stock} < ${threshold}). Update the Stock or set a higher Threshold.`);
                return;
            }
        }
    }
    const updates = [];
    for (const productId in provisionalChanges) {
        const change = provisionalChanges[productId];
        const product = productList.find(p => String(p.id) === productId);
        const fallbackStock = product?.stock ?? 0;
        updates.push({
            id: productId,
            stock: change.stock !== undefined ? change.stock : fallbackStock, //use current stock if not changed
            autoStockEnabled: change.autoStockEnabled === true,
            stockThreshold: change.stockThreshold ?? null,
            updateAmount: change.updateAmount ?? null
        });
    }
    try {
        const response = await fetch('/admin/update-stock', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updates)
        });
        const message = await response.text();
        alert(message); //display the message as an alert
        provisionalChanges = {};  //clear all provisional changes
        await fetchProducts();
        updateSaveButtonState();
    } catch (error) {
        console.error("Error saving changes:", error);
    }
}


    //DATA RETRIEVAL

//function to fetch all products from backend using AJAX
async function fetchProducts() {
    try {
        const res = await fetch('/admin/products'); //send fetch request to controller endpoint
        productList = await res.json();  //parse response as JSON
        renderProducts(productList, true);  //fully render the products list into the table
    } catch (err) {
        console.error("Error fetching products:", err);
    }
}



    //EVENT LISTENERS

//click listener to handle button clicks
document.getElementById('productRows').addEventListener('click', function(event) {
    const target = event.target;
    const productId = target.getAttribute('data-id');
    if (!productId) return; //ensure product ID

    if (target.classList.contains('increase-btn') || target.classList.contains('decrease-btn')) {
        const change = parseInt(target.getAttribute('data-change'));
        adjustStock(productId, change);
    } else if (target.classList.contains('empty-btn')) {
        emptyStock(productId);
    } else if (target.classList.contains('undo-btn')) {
        undoChanges(productId);
    } else if (target.classList.contains('set-btn')) {
        submitManualStock(productId);
    }
});

//handle input events for manual stock, threshold, and update amount inputs.
document.getElementById('productRows').addEventListener('input', function(event) {
    const target = event.target;
    const productId = target.getAttribute('data-id');
    if (!productId) return;
    const row = findRowByProductId(productId);
    if (target.classList.contains('manual-stock-input')) {
        const setBtn = row.querySelector('.set-btn');
        const val = target.value.trim();
        setBtn.disabled = val === '' || isNaN(val) || parseInt(val) < 0;
    } else if (target.classList.contains('threshold-input')) {
        const value = parseInt(target.value);
        const change = provisionalChanges[productId] || {};
        change.stockThreshold = value;
        const product = productList.find(p => p.id === productId);
        const currentStock = provisionalChanges[productId]?.stock ?? product.stock;
        if (!isNaN(value) && currentStock < value) {
            change.stock = value;
        }
        provisionalChanges[productId] = change;
        updateRow(product, row);
        updateSaveButtonState();
    } else if (target.classList.contains('update-amount-input')) {
        const change = provisionalChanges[productId] || {};
        change.updateAmount = parseInt(target.value);
        provisionalChanges[productId] = change;
        updateSaveButtonState();
    }
});

//change events for auto-update checkbox
document.getElementById('productRows').addEventListener('change', function(event) {
    const target = event.target;
    if (target.classList.contains('auto-update')) {
        const productId = target.getAttribute('data-id');
        const row = findRowByProductId(productId);
        const thresholdInput = row.querySelector('.threshold-input');
        const updateInput = row.querySelector('.update-amount-input');
        const checked = target.checked;
        thresholdInput.style.display = checked ? 'inline-block' : 'none';
        updateInput.style.display = checked ? 'inline-block' : 'none';
        const change = provisionalChanges[productId] || {};
        change.autoStockEnabled = checked;
        if (!checked) {
            change.stockThreshold = null;
            change.updateAmount = null;
        }
        provisionalChanges[productId] = change;
        updateSaveButtonState();
    }
});

//event listener to fetch products when the page loads
window.addEventListener('DOMContentLoaded', fetchProducts);

