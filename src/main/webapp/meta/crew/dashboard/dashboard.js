const months = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec"
];
const cookies = parseCookie(document.cookie);
const token = cookies['auth_token'];
const account_id = cookies['account_id'];
console.log("account id: " + account_id);
let currentTheme = document.getElementById("currentCSS");
currentTheme.setAttribute('href', 'crewDashboardStyles.css');
let booking_list = [];

console.log("initial theme: " + currentTheme.getAttribute('href'))

function addCrew(id) {
        const url = window.location.origin+`/shotmaniacs2/api/admin/booking/${id}/crew`; //TODO confirm that crew have permission to see this as well

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let crewList = document.getElementById("ul_list");
            let adminList= document.getElementById("users");

            crewList.innerHTML = "";
            adminList.innerHTML = "";

            data.forEach(crew => {
                console.log("crew name: " + crew.username + ", type: " + crew.accountType);


                switch (crew.accountType) {
                    case "Crew": {
                        crewList.innerHTML += `
                            <li>${crew.username}</li>
                        `;
                        break;
                    }
                    case "Administrator": {
                        adminList.innerHTML += `
                            <i class="fas fa-user user-icon" style="color: #000000;"></i>
                            <div class="user-box" id="user1">
                                <span>${crew.username}</span>
                            </div>
                        `;
                        break;
                    }
                    default: {
                        break;
                    }
                }
            })

            addProductManager(id);
        })
}


function addProductManager(id) {
    const url = window.location.origin+`/shotmaniacs2/api/admin/booking/${id}/productmanager`

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let productManager = document.getElementById("product-Manager");

            productManager.innerHTML = `<p><strong>Product Manager: </strong>${data.username}</p>`;

            setLabel(id);
        })
}

function setLabel(id) {
    const url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/booking/${id}/label`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.text())
        .then(data => {
            let label = data.replace(" ", "-");
            let toReplace = document.getElementById("label");
            console.log("label for " + id + ": " + label);
            if (label !== null) {
                toReplace.innerHTML = `
                        <p>${label.toUpperCase()}</p>
                        <i class="fas fa-caret-down label-arrow"></i>`;

               toReplace.classList.replace(toReplace.classList.item(1), label.toUpperCase());
            }
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}

function toggleStyleAndPage(element) {
    currentTheme.setAttribute('href', 'singleEventHomePage.css');
    console.log('new theme changed to: ' + currentTheme.getAttribute('href'));


    const url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/booking/${element.id}`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(booking => {
            /*Display booking as latest booking*/
            document.getElementById("Latest_Booking").innerHTML = `
            <p id="booking_name"> ${booking.name}</p>
            <p id="booking_description">${booking.description}</p>
            `;

            /*Display Single event information*/
            document.getElementById("heading").innerHTML = booking.name;

            document.getElementById("navigation").innerHTML = `
                        <p>
                            <span id="backToBooking" onclick="addLabel([${booking.id}]); setTheme('crewDashboardStyles.css')"> 
                                My Bookings
                            </span>
                            /
                            <span id="link" onclick="setTheme('singleEventHomePage.css'); toggleStyleAndPage(${booking.id})">
                                ${booking.name}
                            </span>
                        </p>`;

            document.getElementById("label_content").innerHTML = `
                <div class="item" id="todo" onclick="updateLabel(this, ${booking.id})">
                    <p>Todo</p>
                </div>
                    
                <div class="item" id="in-progress" onclick="updateLabel(this, ${booking.id})">
                    <p>In Progress</p>
                </div>
                
                <div class="item" id="review" onclick="updateLabel(this, ${booking.id})">
                    <p>Review</p>
                </div>
                
                <div class="item" id="done" onclick="updateLabel(this, ${booking.id})">
                    <p>Done</p>
                </div>
            `;

            document.getElementById("event").innerHTML = `
                <div class="event-info" id="event-name">
                    <p><strong>Name: </strong>${booking.name}</p>
                </div>
                
                <div class="event-info" id="event-type">
                    <p><strong>Event Type: </strong>${booking.eventType}</p>
                </div> 
                   
                <div class="event-info" id="date"> <!-- TODO CHANGE THESE TWO ID's WITH event-{id} incl. JS -->
                    <p><strong>Date: </strong>
                        ${new Date(booking.date).getDate()}
                        ${months[new Date(booking.date).getMonth()]}
                        ${new Date(booking.date).getFullYear()}</p>
                </div>
                
                <div class="event-info" id="location">
                    <p><strong>Location: </strong>${booking.location}</p>
                </div>
                
                <div class="event-info" id="duration">
                    <p><strong>Duration: </strong>${booking.duration}</p>
                </div>
                
                <div class="event-info" id="booking-type">
                    <p><strong>Booking Type: </strong>${booking.bookingType}</p>
                </div>
                
                <div class="event-info" id="product-Manager">
                    <p><strong>Product Manager: </strong>${booking.product_manager}</p>
                </div>
                
                <div class="event-info" id="crew">
                    <p><strong>Crew:</strong>
                        <span><ul id="ul_list"></ul></span>
                    </p>
                </div>
            `;

            addCrew(booking.id);
        })
}

function setTheme(href) {
    currentTheme.setAttribute('href', href);
}


function reloadEvent() {
    //TODO: figure out a way to call the current event page
}


/*Filter*/

function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}

function performQueryAndUpdateBookings(url) {
    let event_booking = "";

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {

                event_booking += `
                <div class="booking" id="${booking.id}" onclick="toggleStyleAndPage(this)">
                    <div class="booking-info-grid">
                        <div class="booking-large-text">
                            <p id="event_name">
                                ${booking.name}
                            </p>
                        </div>
                    
                        <div class="booking-small">
                            <p class="location">
                                ${booking.location}
                            </p>
                        </div>
                    
                        <div class="booking-small">
                            <p id="event_type">
                                ${booking.eventType}
                            </p>
                        </div>
                    </div>
                    
                    <div class="innerBooking left">
                        <p class="state NOBOOKING" id="booking_${booking.id}">
                        </p>
                    </div>
                    
                    <div class="innerBooking right">
                        <p class="date">
                            ${new Date(booking.date).getDate()}
                            ${months[new Date(booking.date).getMonth()]}
                            ${new Date(booking.date).getFullYear()}
                        </p>
                    </div>
                </div>
            `;
                booking_list.push(booking.id);
            });
            document.getElementById("booking_container").innerHTML = event_booking;
            addLabel(booking_list)
        })
}

function addLabel(list) {

    for (let i = 0; i < list.length; i++) {
        let id = list[i];
        const url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/booking/${id}/label`;
        fetch(url, {
            headers: {
                'Authorization': `${token}`
            }
        })
            .then(response => response.text())
            .then(data => {
                let label = data.replace(" ", "-");
                console.log("label for " + id + ": " + label);
                const element = document.getElementById("booking_" + id);
                if (label !== null) {
                    element.classList.replace(element.classList.item(1), label.toUpperCase());
                    element.innerHTML = label.toUpperCase();
                }
            })
            .catch(error => {
                // Handle any errors
                console.error(error);
            });
    }
    console.log("bookingList: " + booking_list)
    booking_list = [];
    console.log("bookingList after Clear: " + booking_list);
}


function updateBookings(filterType) {
    console.log("update bookings started");
    console.log("filter type: " + filterType.toLowerCase());
    const filterButton = document.getElementById("filter_button_text");

    let filter;
    let url;

    switch (filterType) {
        case 'in_progress': {
            filter = "IN_PROGRESS";
            filterButton.textContent = "Filter: In progress";
            url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybooking/labelfilter/${filter}`;
            break;
        }
        case "todo": {
            filter = "TODO";
            filterButton.textContent = "Filter: TODO";
            url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybooking/labelfilter/${filter}`;
            break;
        }
        case "review": {
            console.log("Entered Review Section");
            filter = "REVIEW";
            filterButton.textContent = "Filter: REVIEW";
            url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybooking/labelfilter/REVIEW`;
            break;
        }
        case "done": {
            filter = "DONE";
            filterButton.textContent = "Filter: DONE";
            url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybooking/labelfilter/${filter}`;
            break;
        }
        case 'future': {
            filter = "ongoing";
            filterButton.textContent = "Filter: Upcoming";
            url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybooking/timefilter/${filter}`;
            break;
        }
        case 'past': {
            filter = "past";
            filterButton.textContent = "Filter: Past";
            url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybooking/timefilter/${filter}`;
            break;
        }
        default: {
            filterButton.textContent = "All Bookings";
            url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybookings`;
        }
    }



    performQueryAndUpdateBookings(url);

}

/*
function searchBookings(searchText) {

    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/search?searchtext=\"${searchText}\"`;
    performQueryAndUpdateBookings(url);
}
*/

function updateLabel(label, id) {
    console.log("label: " + label.innerText)
    let state = document.getElementById("label");
    let replace = label.innerText.replace(" ", "-").toUpperCase();
    switch (replace) {

        case "TODO":
            state.classList.replace(state.classList.item(1), replace);
            state.innerHTML = `<p>${label.innerText}</p>
            <i class="fas fa-caret-down label-arrow"></i>`;
            break;
        case "IN-PROGRESS":
            state.classList.replace(state.classList.item(1), replace);
            state.innerHTML = `<p>${label.innerText}</p>
            <i class="fas fa-caret-down label-arrow"></i>`;
            break;
        case "REVIEW":
            state.classList.replace(state.classList.item(1), replace);
            state.innerHTML = `<p>${label.innerText}</p>
            <i class="fas fa-caret-down label-arrow"></i>`;
            break;
        case "DONE":
            state.classList.replace(state.classList.item(1), replace);
            state.innerHTML = `<p>${label.innerText}</p>
            <i class="fas fa-caret-down label-arrow"></i>`;
            break;
        default:
            state.classList.replace(state.classList.item(1), "NORMAL");
            state.innerHTML = `<p>Set Label </p>
            <i class="fas fa-caret-down label-arrow"></i>`;
    }


    const url = window.location.origin +`/shotmaniacs2/api/crew/${account_id}/booking/${id}/label?label=${label.innerText.replace("-"," ").toUpperCase()}`;
    fetch(url, {
            method: 'PUT',
            headers: {
                'Authorization': `${token}`
            }
        }).catch(error => {
            // Handle any errors
            console.error(error);
        });
}
/*
//Event listeners
const searchbox = document.getElementById("search-input");
searchbox.addEventListener("keydown", function (e) {
    if (e.code === "Enter") {  //checks whether the pressed key is "Enter"
        searchBookings(searchbox.value);
    }
});
*/


/*Search function*/
let allBookings = [];
let inputElement = document.getElementById("search-input");

inputElement.addEventListener("input", onInputChange);

//TODO double check filter use (link is mybookings, name is all bookings)
function getAllBookings() {
    const url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybookings`

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            allBookings = data.map((booking) => {
                return booking.name;
            })
        })
}

function  onInputChange() {
    removeAutocompleteDropdown();

    const value = inputElement.value.toLowerCase();

    if (value.length === 0) return;

    const filteredNames = [];


    allBookings.forEach((BookingName) => {
        if (BookingName.substring(0, value.length).toLowerCase() === value) {
            filteredNames.push(BookingName);
        }
    })
    createAutocompleteDropdown(filteredNames);
}

function createAutocompleteDropdown(list) {
    const listEl = document.createElement("ul");
    listEl.className = "autocomplete-list";
    listEl.id =  "autocomplete-list";
    list.forEach((booking) => {
        const listItem = document.createElement("li");

        const bookingButton = document.createElement("button");
        bookingButton.innerHTML = booking;
        bookingButton.addEventListener("click", onBookingButtonClick)
        listItem.appendChild(bookingButton);

        listEl.appendChild(listItem);
    })
    document.querySelector("#searchbar").appendChild(listEl);
}

function removeAutocompleteDropdown() {
    const listEl = document.querySelector("#autocomplete-list");
    if (listEl) listEl.remove(); //checks if it exists and then removes it
}

function onBookingButtonClick(event) {
    event.preventDefault(); //cancels default event (submitting the form)


    document.querySelector("#search-button").addEventListener("click", sendInput);

    const buttonEl = event.target; //element that triggered the event (the button itself)
    inputElement.value = buttonEl.innerHTML; //should be a string of the booking name

    removeAutocompleteDropdown();

}


function sendInput(event) {
    console.log("input called")
    event.preventDefault();

    if (inputElement.value.length === 0) updateBookings("");
    else {
        const url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybooking/search?searchtext="${inputElement.value}"`
        performQueryAndUpdateBookings(url);
    }
}


updateBookings("");
function logout(){
    const token = cookies['auth_token'];
    const url = window.location.origin+`/shotmaniacs2/api/login`
    fetch(url, {
        method: 'DELETE',
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => {
            if (response.ok) {
                document.cookie = "account_id =;  Path=/";
                document.cookie = "account_username =;  Path=/";
                document.cookie = "auth_token =;  Path=/";
                window.location.href="http://localhost:8080/shotmaniacs2/";
            } else if (response.status === 304) {
                throw new Error('The given account was not logged in.');
            } else {
                throw new Error('Failed to log out. Status: ' + response.status);
            }
        })
        .then(data => {
            console.log(data); // Logged out successfully
        })
        .catch(error => {
            console.error(error);
        });

}
function graph(){
    const filter = "DONE";
    const url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/mybooking/labelfilter/${filter}`;
    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(bookings => {
            var bookingCounts = {};

            bookings.forEach(function (booking) {
                var timestamp = new Date(parseInt(booking.date));

                if (!isNaN(timestamp)) { // Check if timestamp is valid
                    var date = new Date(timestamp); // Convert to Date object

                    var monthh = date.getFullYear() + '-' + (date.getMonth() + 1).toString().padStart(2, '0');

                    if (bookingCounts[monthh]) {
                        bookingCounts[monthh]++;
                    } else {
                        bookingCounts[monthh] = 1;
                    }
                }});

            // Convert the booking counts object to arrays
            var monthss = Object.keys(bookingCounts);
            var counts = monthss.map(function(month) {
                return bookingCounts[month];
            });

            // Get the canvas element
            var canvas = document.getElementById('chart');
            var ctx = canvas.getContext('2d');

            // Chart configuration
            var chartConfig = {
                type: 'line',
                data: {
                    labels: monthss,
                    datasets: [{
                        label: 'Number of Bookings',
                        data: counts,
                        borderColor: 'blue',
                        backgroundColor: 'transparent'
                    }]
                },
                options: {
                    responsive: true,
                    title: {
                        display: true,
                    },
                    scales: {
                        x: {
                            display: true,
                            title: {
                                display: true,
                            },
                            min: monthss[0],  // Specify the minimum value on the x-axis
                            max: monthss[4]
                        },
                        y: {
                            display: true,
                            title: {
                                display: true,
                            },
                        }
                    }
                }
            };

            // Create the chart graph
            var chart = new Chart(ctx, chartConfig);})
}