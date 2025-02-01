//function to update the keypad display
function updateDisplay(value) {
    let display = document.getElementById("keypadDisplay");

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
}

//function to clear the keypad display
function clearDisplay() {
    document.getElementById("keypadDisplay").innerText = "_";
}

//function to validate and submit entered code
function submitCode() {
    let display = document.getElementById("keypadDisplay");
    let enteredCode = display.innerText.trim();  // Get the entered code

    //validation of the code
    if (/^[A-D][1-4]$/.test(enteredCode)) {
        console.log("Valid code submitted:", enteredCode);
        alert("Item "+enteredCode+" has been added to Cart successfully!");
    } else {
        console.error("Invalid code submitted:", enteredCode);
        alert("Invalid item code detected :(  Please try again.");
    }
    clearDisplay();  //always clear the display after the code is submitted
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
}


