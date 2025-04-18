//function to display the analytics data
function renderAnalyticsSummary(data) {
    document.getElementById("totalPurchases").innerText = data.totalPurchases;
    document.getElementById("totalSpent").innerText = `£${data.totalSpent.toFixed(2)}`;
    document.getElementById("activeDay").innerText = data.mostActiveDay;
    document.getElementById("uniqueItems").innerText = data.uniqueItemsPurchased;
}

//function to retrieve the summary data from the controller endpoint using AJAX
function loadAnalyticsSummary() {
    fetch('/analytics/summary')
        .then(response => response.json())
        .then(data => renderAnalyticsSummary(data))
        .catch(err => console.error("Error loading analytics summary:", err));
}



//function to create and display the purchase frequency chart (bar chart)
function renderPurchaseFrequencyChart(data) {
    const canvas = document.getElementById('purchaseFrequencyChart');
    const ctx = canvas.getContext('2d');

    const msg = document.getElementById('noFrequencyDataMsg');
    const chart = document.getElementById('purchaseFrequencyChart');

    if (handleNoChartData(data, msg, chart)) return; //call to check and handle empty chart data and if returns true, stop rendering (no data)


    const labels = data.map(entry =>
        new Date(entry.month).toLocaleString('default', { year: 'numeric', month: 'short' })
    );
    const counts = data.map(entry => entry.count);

    const container = canvas.parentElement;
    if (data.length > 12) {
        container.style.overflowX = 'auto';
        container.style.width = '100%';
        canvas.width = data.length * 80;
    }

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Purchases per Month',
                data: counts,
                backgroundColor: 'rgba(0, 255, 255, 0.3)',
                borderColor: 'cyan',
                borderWidth: 2,
                hoverBackgroundColor: 'rgba(0, 255, 255, 0.7)',
                hoverBorderColor: '#0ff',
                barThickness: 40
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Month',
                        color: 'cyan',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    ticks: {
                        color: '#0ff',
                        font: {
                            size: 14
                        }
                    },
                    grid: {
                        display: true,
                        color: 'rgba(0, 255, 255, 0.1)',
                        lineWidth: 1
                    }
                },
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Number of Purchases',
                        color: 'cyan',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    ticks: {
                        color: '#0ff',
                        font: {
                            size: 14
                        }
                    },
                    grid: {
                        display: true,
                        color: 'rgba(0, 255, 255, 0.1)',
                        lineWidth: 1
                    }
                }
            },
            plugins: {
                legend: {
                    labels: {
                        color: '#00ffff',
                        font: {
                            size: 14
                        }
                    }
                },
                tooltip: {
                    backgroundColor: '#000',
                    borderColor: '#0ff',
                    borderWidth: 1,
                    titleColor: '#0ff',
                    bodyColor: '#fff',
                    titleFont: {
                        size: 16,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 14
                    },
                    displayColors: false,
                    intersect: true,
                    mode: 'index',
                    caretSize: 6
                }
            },
            animation: {
                duration: 1000,
                easing: 'easeOutCubic'
            }
        }
    });
}

//function to load the purchase frequency data from backend through the endpoint (AJAX)
function loadPurchaseFrequency() {
    fetch('/analytics/frequency')
        .then(response => response.json())
        .then(data => renderPurchaseFrequencyChart(data))
        .catch(err => console.error("Error loading purchase frequency:", err));
}


//function to create and display the spending trend chart (line graph)
function renderSpendingTrendChart(data) {
    const ctx = document.getElementById('spendingTrendChart').getContext('2d');

    const msg = document.getElementById('noSpendingDataMsg');
    const chart = document.getElementById('spendingTrendChart');

    if (handleNoChartData(data, msg, chart)) return; //call to check and handle empty chart data and if returns true, stop rendering (no data)



    const labels = data.map(entry => {
        const [year, month] = entry.month.split("-");
        return new Date(`${year}-${month}-01`).toLocaleString('default', {
            month: 'short', year: 'numeric'
        });
    });

    const amounts = data.map(entry => entry.totalSpent);

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Monthly Spending (£)',
                data: amounts,
                borderColor: '#00ffff',
                backgroundColor: 'rgba(0, 255, 255, 0.1)',
                tension: 0.3,
                fill: true,
                pointRadius: 5,
                pointHoverRadius: 8,
                pointBackgroundColor: '#0ff',
                pointBorderColor: '#fff',
                pointHoverBorderColor: '#0ff',
                pointHoverBackgroundColor: '#000'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Month',
                        color: 'cyan',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    ticks: {
                        color: '#0ff',
                        font: {
                            size: 14
                        }
                    },
                    grid: {
                        display: true,
                        color: 'rgba(0, 255, 255, 0.1)',
                        lineWidth: 1
                    }
                },
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Amount (£)',
                        color: 'cyan',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    ticks: {
                        color: '#0ff',
                        font: {
                            size: 14
                        }
                    },
                    grid: {
                        display: true,
                        color: 'rgba(0, 255, 255, 0.1)',
                        lineWidth: 1
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.dataset.label || '';
                            const value = context.parsed.y;
                            return `${label}: £${value.toFixed(2)}`;
                        }
                    },
                    backgroundColor: '#000',
                    borderColor: '#0ff',
                    borderWidth: 1,
                    titleColor: '#0ff',
                    bodyColor: '#fff',
                    titleFont: {
                        size: 16,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 14
                    },
                    displayColors: false,
                    intersect: true,
                    mode: 'index',
                    caretSize: 6
                },
                legend: {
                    labels: {
                        color: '#00ffff',
                        font: {
                            size: 14
                        }
                    }
                }
            },
            animation: {
                duration: 1000,
                easing: 'easeOutCubic'
            }
        }
    });
}


//function to load the spending trend data and pass it into the render method
function loadSpendingTrend() {
    fetch('/analytics/spending')
        .then(response => response.json())
        .then(data => renderSpendingTrendChart(data))
        .catch(err => console.error("Error loading spending trend:", err));
}

//function to load the item breakdown data
function loadItemBreakdown() {
    fetch('/analytics/item-breakdown')
        .then(response => response.json())
        .then(data => {
            if (!Array.isArray(data)) {
                console.error("Unexpected response format for item breakdown:", data);
                return;
            }
            renderItemBreakdownChart(data);
        })
        .catch(error => {
            console.error("Error loading item breakdown:", error);
        });
}

//function to render the purchase item breakdown pie chart
function renderItemBreakdownChart(data) {
    const ctx = document.getElementById("itemBreakdownChart").getContext("2d");

    //sort the data by productId
    data.sort((a, b) => a.productId.localeCompare(b.productId, undefined, { numeric: true }));

    //prepare chart data
    const productIDs = data.map(item => item.productId);
    const quantities = data.map(item => item.quantity);
    const fullNames = data.map(item => item.productName);

    //colour palette (24 unique colors, 1 for each unique item)
    const backgroundColors = [
        "rgba(0,255,255,0.4)",   "rgba(0,255,127,0.4)", "rgba(255,255,0,0.4)",   "rgba(255,0,255,0.4)",
        "rgba(0,191,255,0.4)",   "rgba(255,105,180,0.4)","rgba(173,255,47,0.4)",  "rgba(255,165,0,0.4)",
        "rgba(138,43,226,0.4)",  "rgba(0,250,154,0.4)", "rgba(70,130,180,0.4)",  "rgba(255,20,147,0.4)",
        "rgba(124,252,0,0.4)",   "rgba(64,224,208,0.4)","rgba(255,215,0,0.4)",   "rgba(0,206,209,0.4)",
        "rgba(255,140,0,0.4)",   "rgba(147,112,219,0.4)","rgba(0,128,128,0.4)",   "rgba(152,251,152,0.4)",
        "rgba(255,99,71,0.4)",   "rgba(0,255,255,0.4)", "rgba(50,205,50,0.4)",   "rgba(199,21,133,0.4)"
    ];
    const borderColors = backgroundColors.map(c => c.replace('0.4', '1'));

    //destroy previous chart if exists to avoid stacking
    if (window.itemBreakdownChartInstance) {
        window.itemBreakdownChartInstance.destroy();
    }

    //create the chart
    window.itemBreakdownChartInstance = new Chart(ctx, {
        type: "pie",
        data: {
            labels: productIDs,
            datasets: [{
                data: quantities,
                backgroundColor: backgroundColors,
                borderColor: borderColors,
                borderWidth: 3,
                hoverBorderWidth: 6,
                hoverBorderColor: "#0ff"
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: "top",
                    labels: {
                        color: "#0ff",
                        boxWidth: 18,
                        font: { size: 15, weight: "bold", family: "Arial" },
                        padding: 16
                    }
                },
                tooltip: {
                    enabled: true,
                    backgroundColor: "#0a0a23",
                    titleColor: "#0ff",
                    bodyColor: "#fff",
                    borderColor: "#0ff",
                    borderWidth: 2,
                    callbacks: {
                        //show product name and quantity on hover
                        label: function(context) {
                            const idx = context.dataIndex;
                            return `${fullNames[idx]}: ${quantities[idx]}`;
                        }
                    }
                }
            },
            layout: {
                padding: { top: 40, bottom: 10, left: 10, right: 10 }
            }
        }
    });
}





//helper function to check if there is no chart data and handle this by hiding the chart and displaying a message instead
function handleNoChartData(data,msg,chart){
    if (!data || data.length === 0) {
        chart.style.display = 'none';
        msg.style.display = 'block';
        return true;
    } else {
        chart.style.display = 'block';
        msg.style.display = 'none';
        return false;
    }
}

//event listener to validate user is logged in, before loading all data
document.addEventListener("DOMContentLoaded", () => {
    const userText = document.querySelector(".user-info span").innerText; //select user info element text

    if (userText && userText.toLowerCase() !== "guest") {
        loadAllAnalytics(); //only load if user is not guest
    } else {
        console.warn("Analytics not loaded: user is guest or not logged in.");
    }
});

//function to load all required data (each load function will call their own render function)
function loadAllAnalytics() {
    loadAnalyticsSummary();
    loadPurchaseFrequency();
    loadSpendingTrend();
    loadItemBreakdown()
}



