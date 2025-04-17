
//function to display the modal for standard alerts
function showAlert(message) {

    //retrieve the elements
    const modal = document.getElementById("customModal");
    const msg = document.getElementById("customModalMessage");
    const btnOk = document.getElementById("customModalOk");

    //manage the display of the modal
    msg.innerText = message;
    document.body.classList.add("modal-lock");
    modal.classList.remove("hidden");

    btnOk.style.display = "inline-block";
    document.getElementById("customModalConfirm").style.display = "none";
    document.getElementById("customModalCancel").style.display = "none";

    //when clicking the OK button hide the modal and remove the outside lock on other elements
    btnOk.onclick = () => {
        modal.classList.add("hidden");
        document.body.classList.remove("modal-lock");
    };

    //simulate an OK button click if Enter or ESC are used by the user from their keyboard
    document.onkeydown = (e) => {
        if (e.key === "Enter" || e.key === "Escape") {
            btnOk.click();
        }
    };
}

//function to display the modal as a confirm window
function showConfirm(message, onConfirm, onCancel = null) {
    //select the elements
    const modal = document.getElementById("customModal");
    const msg = document.getElementById("customModalMessage");
    const btnConfirm = document.getElementById("customModalConfirm");
    const btnCancel = document.getElementById("customModalCancel");

    //manage the modal display
    msg.innerText = message;
    document.body.classList.add("modal-lock");
    modal.classList.remove("hidden");

    //confirm modal has cancel and confirm buttons, but no OK button
    btnConfirm.style.display = "inline-block";
    btnCancel.style.display = "inline-block";
    document.getElementById("customModalOk").style.display = "none";

    //when confirm button is clicked, hide the modal and remove the outside lock
    btnConfirm.onclick = () => {
        modal.classList.add("hidden");
        document.body.classList.remove("modal-lock");
        onConfirm();
    };

    //when the cancel button is clicked, also hide the modal and remove the lock
    btnCancel.onclick = () => {
        modal.classList.add("hidden");
        document.body.classList.remove("modal-lock");
        if (typeof onCancel === "function") onCancel(); //if a function is passed in for onCancel, then run it
    };

    //define keyboard shortcuts: Enter key for the confirm button and ESC key for the cancel button
    document.onkeydown = (e) => {
        if (e.key === "Enter") btnConfirm.click();
        if (e.key === "Escape") btnCancel.click();
    };
}

