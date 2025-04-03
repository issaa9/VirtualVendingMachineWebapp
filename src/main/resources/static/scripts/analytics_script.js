//event listener to fetch required data upon page load and display it using AJAX
document.addEventListener("DOMContentLoaded", function () {
    fetch('/analytics/summary') //fetch using controller endpoint
        .then(response => response.json())
        .then(data => { //dynamically update the display
            document.getElementById("totalPurchases").innerText = data.totalPurchases;
            document.getElementById("totalSpent").innerText = `£${data.totalSpent.toFixed(2)}`;
            document.getElementById("activeDay").innerText = data.mostActiveDay;
        })
        .catch(err => {
            console.error("Error loading analytics summary:", err); //error handling
        });
});
