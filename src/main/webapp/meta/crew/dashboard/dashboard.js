const months = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
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
        const url = `http://localhost:8080/shotmaniacs2/api/admin/booking/${id}/crew`; //TODO confirm that crew have permission to see this as well

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

            setLabel(id);
        })
}

function setLabel(id) {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/booking/${id}/label`;

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


    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/booking/${element.id}`;

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
        const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/booking/${id}/label`;
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

    switch (filterType.toLowerCase()) {
        case 'in_progress': {
            filter = "IN_PROGRESS";
            filterButton.textContent = "Filter: In progress";
            url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/labelfilter/${filter}`;
            break;
        }
        case "todo": {
            filter = "TODO";
            filterButton.textContent = "Filter: TODO";
            url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/labelfilter/${filter}`;
            break;
        }
        case "review": {
            console.log("Entered Review Section");
            filter = "REVIEW";
            filterButton.textContent = "Filter: REVIEW";
            url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/labelfilter/REVIEW`;
            break;
        }
        case "done": {
            filter = "DONE";
            filterButton.textContent = "Filter: DONE";
            url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/labelfilter/${filter}`;
            break;
        }
        case 'future': {
            filter = "ongoing";
            filterButton.textContent = "Filter: Upcoming";
            url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/timefilter/${filter}`;
            break;
        }
        case 'past': {
            filter = "past";
            filterButton.textContent = "Filter: Past";
            url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/timefilter/${filter}`;
            break;
        }
        default: {
            filter = 'ongoing';
            url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/timefilter/${filter}`;
        }
    }



    performQueryAndUpdateBookings(url);

}

function searchBookings(searchText) {

    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/search?searchtext=\"${searchText}\"`;
    performQueryAndUpdateBookings(url);
}

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


    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/booking/${id}/label?label=${label.innerText.replace("-"," ").toUpperCase()}`;
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

//Event listeners
const searchbox = document.getElementById("search-input");
searchbox.addEventListener("keydown", function (e) {
    if (e.code === "Enter") {  //checks whether the pressed key is "Enter"
        searchBookings(searchbox.value);
    }
});

updateBookings("ongoing");