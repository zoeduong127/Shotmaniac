const left = document.getElementById("container1");
const right = document.getElementById("information");
const popup = document.getElementById("popup-container");
const cancel = document.getElementById("cancel-container");

const cookies = parseCookie(document.cookie);
const token = cookies['auth_token'];
const account_id = cookies['account_id'];
console.log("account id: " + account_id);

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

const days = [
    "Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday"
];


function toggleStyle(id) {
    left.style.opacity = "0.2";
    left.style.pointerEvents = "none";
    right.style.opacity = "0.2";
    right.style.pointerEvents = "none";

    popup.style.visibility = "visible";

    displayInformation(id);
}

function toggleOff() {
    left.style.opacity = "1";
    left.style.pointerEvents = "auto";

    right.style.opacity = "1";
    right.style.pointerEvents = "auto";

    popup.style.visibility = "hidden";
}

function callCancel() {
    popup.style.visibility = "hidden";
    popup.style.zIndex = "auto";

    cancel.style.visibility = "visible";
}

function toggleCancel() {
    left.style.opacity = "1";
    right.style.opacity = "1";

    cancel.style.visibility = "hidden";
}


function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}


function performQueryAndUpdateBookings(input) {
    let url;
    if (input === " ") {
        url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`;
    } else {
        url = input;
    }
    let booking_list = [];
    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {
                booking_list.push(booking);
            });
            booking_list.sort(function (a, b) {
                return a.id - b.id
            }).reverse(); //should sort the booking backwards,
            // assuming the id is auto-incrementing

            displaySortedBookings(booking_list);
        })
}

function displaySortedBookings(list) {
    let element = "";

    list.forEach(booking => {
        element += `
            <div class="event" onclick="toggleStyle(${booking.id})">
                <div class="booking-info-grid">
                    <div class="booking-large-text">
                        <p class="event_name">
                            ${booking.name}
                        </p>
                    </div>
                    
                    <div class="booking-small">
                        <p class="location">${booking.description}</p>
                    </div>
                </div>
                
                <div class="innerBooking left">
                    <p class="state ${booking.state}-state">${booking.state}</p>
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
    })
    document.getElementById("event-container").innerHTML = element;
}

function displayInformation(id) {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/booking/${id}`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(booking => {

            /*Display Single event information*/
            document.getElementById("heading-name").innerHTML = booking.name;


            document.getElementById("information-section").innerHTML = `
                <div class="section" id="date">
                    <strong>Date: </strong>
                        ${days[new Date(booking.date).getDay()]}, 
                        ${new Date(booking.date).getDate()} 
                        ${months[new Date(booking.date).getMonth()]} 
                        ${new Date(booking.date).getFullYear()},
                        ${new Date(booking.date).getHours()}:${new Date(booking.date).getMinutes()}
                </div>
                
                <div class="section" id="Event-Type">
                    <strong>Event Type: </strong> ${booking.eventType}
                </div>
                <div class="section" id="location">
                    <strong>Location: </strong>${booking.location}
                </div>
                <div class="section" id="Extra">
                    <span><strong>Booking Type: </strong>${booking.bookingType}</span>
                    <span><strong>Duration: </strong>${booking.duration}</span>
                    <span><strong>Description: </strong>${booking.description}
                </div>
                
                <div class="section" id="Client">
                    <span><strong>Name: </strong>${booking.clientName}</span>
                    <span><strong>Email: </strong>${booking.clientEmail}</span>
                    <span><strong>Phone: </strong>${booking.phoneNumber}</span>
                </div>
            `;

            document.getElementById("input-area").innerHTML = `
                <h4>Number of workers needed</h4>
                
                <div class="input-section">
                    <label for="crew-needed">Crew Members</label>
                    <input id="crew-needed" type="number" required>
                </div>
                
                <div class="input-section" id="searchbar"> 
                    <label for="product-manager">Prod. Manager</label>
                    <input type="text" id="product-manager" onfocus="getAllCrew()" required>
                    <button style="visibility: hidden" id="fakeButton"></button>
                </div>
            `;
        })
}


function addUpcomingEvents() {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`;
    let booking_list = [];
    let day = "" + new Date().getDate() +
        new Date().getMonth() +
        new Date().getFullYear();


    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {


                let BackDay = new Date(booking.date).getDate();
                let BackMonth = new Date(booking.date).getMonth();
                let BackYear = new Date(booking.date).getFullYear();

                let bookingDay = "" + BackDay + BackMonth + BackYear;


                if (day === bookingDay) {
                    booking_list.push(booking);

                } else if (String(Number(day + 1)) === bookingDay || String(Number(day - 1)) === bookingDay) {

                }

            });
            booking_list.sort(function (a, b) {
                return a.id - b.id
            }).reverse();
        })
}

/*Search for crew (single event)*/
let crewMemberList = [];
let productManagerElement = document.getElementById("product-Manager");

function getAllCrew() {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`; //TODO CHANGE LINK TO GET ALL CREW

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            crewMemberList = data.map((crew) => {
                return crew.username;
            })
        })
    productManagerElement.addEventListener("input", onSingleInputChange);
}

function  onSingleInputChange() {
    removeAutocompleteDropdown();

    const value = productManagerElement.value.toLowerCase();

    if (value.length === 0) return;

    const filteredNames = [];


    crewMemberList.forEach((crewName) => {
        if (crewName.substring(0, value.length).toLowerCase() === value) {
            filteredNames.push(crewName);
        }
    })
    createSingleAutocompleteDropdown(filteredNames);
}

function createSingleAutocompleteDropdown(list) {
    const listEl = document.createElement("ul");
    listEl.className = "autocomplete-list";
    listEl.id =  "autocomplete-list";
    list.forEach((crew) => {
        const listItem = document.createElement("li");

        const bookingButton = document.createElement("button");
        bookingButton.innerHTML = crew;
        bookingButton.addEventListener("click", onSingleBookingButtonClick)
        listItem.appendChild(bookingButton);

        listEl.appendChild(listItem);
    })
    document.querySelector("#searchbar").appendChild(listEl);
}

function onSingleBookingButtonClick(event) {
    event.preventDefault(); //cancels default event (submitting the form)

    const buttonEl = event.target; //element that triggered the event (the button itself)
    productManagerElement.value = buttonEl.innerHTML; //should be a string of the booking name

    removeAutocompleteDropdown();
}


/*Search all events*/
let AllBookings = [];
let inputElement = document.getElementById("search");

inputElement.addEventListener("input", ALL_onInputChange);

function getAllBookings() {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            AllBookings = data.map((booking) => {
                return booking.name;
            })
        })
}

function  ALL_onInputChange() {
    removeAutocompleteDropdown();

    const value = inputElement.value.toLowerCase();

    if (value.length === 0) return;

    const filteredNames = [];


    AllBookings.forEach((BookingName) => {
        if (BookingName.substring(0, value.length).toLowerCase() === value) {
            filteredNames.push(BookingName);
        }
    })
    ALL_createAutocompleteDropdown(filteredNames);
}

function ALL_createAutocompleteDropdown(list) {
    const listEl = document.createElement("ul");
    listEl.className = "All-autocomplete-list";
    listEl.id =  "autocomplete-list";
    list.forEach((booking) => {
        const listItem = document.createElement("li");

        const bookingButton = document.createElement("button");
        bookingButton.innerHTML = booking;
        bookingButton.addEventListener("click", ALL_onBookingButtonClick)
        listItem.appendChild(bookingButton);

        listEl.appendChild(listItem);
    })
    document.querySelector("#search_ALL").appendChild(listEl);
}

function ALL_onBookingButtonClick(event) {
    event.preventDefault(); //cancels default event (submitting the form)


    document.querySelector("#search-icon-btn").addEventListener("click", ALL_sendInput);

    const buttonEl = event.target; //element that triggered the event (the button itself)
    inputElement.value = buttonEl.innerHTML; //should be a string of the booking name

    removeAutocompleteDropdown();

}

function ALL_sendInput(event) {
    console.log("input called")
    event.preventDefault();

    if (inputElement.value.length === 0) performQueryAndUpdateBookings(" ");
    else {
        const url = `http://localhost:8080/shotmaniacs2/api/admin/bookings/search?searchtext=${inputElement.value}`
        performQueryAndUpdateBookings(url);
    }
}


/*Announcement search*/

let Announcements = [];
let AnnouncementInputElement = document.getElementById("search-input");

AnnouncementInputElement.addEventListener("input", Announcement_onInputChange);
function getAllAnnouncements() {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`; //TODO CHANGE THE URL TO ALL ANNOUNCEMENTS

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            Announcements = data.map((announcement) => {
                return announcement.name;
            })
        })
}

function  Announcement_onInputChange() {
    removeAutocompleteDropdown();

    const value = AnnouncementInputElement.value.toLowerCase();

    if (value.length === 0) return;

    const filteredNames = [];


    Announcements.forEach((BookingName) => {
        if (BookingName.substring(0, value.length).toLowerCase() === value) {
            filteredNames.push(BookingName);
        }
    })
    Announcement_createAutocompleteDropdown(filteredNames);
}

function Announcement_createAutocompleteDropdown(list) {
    const listEl = document.createElement("ul");
    listEl.className = "Announcement-autocomplete-list";
    listEl.id =  "autocomplete-list";
    list.forEach((booking) => {
        const listItem = document.createElement("li");

        const bookingButton = document.createElement("button");
        bookingButton.innerHTML = booking;
        bookingButton.addEventListener("click", Announcement_onBookingButtonClick)
        listItem.appendChild(bookingButton);

        listEl.appendChild(listItem);
    })
    document.querySelector("#announcement-search").appendChild(listEl);
}

function Announcement_onBookingButtonClick(event) {
    event.preventDefault(); //cancels default event (submitting the form)


    document.querySelector("#announcement-search-button").addEventListener("click", Announcement_sendInput);

    const buttonEl = event.target; //element that triggered the event (the button itself)
    AnnouncementInputElement.value = buttonEl.innerHTML; //should be a string of the booking name

    removeAutocompleteDropdown();

}

function Announcement_sendInput(event) {
    console.log("input called")
    event.preventDefault();

    if (AnnouncementInputElement.value.length === 0) performQueryAndUpdateBookings(" ");
    else {
        const url = `http://localhost:8080/shotmaniacs2/api/admin/bookings/search?searchtext=${AnnouncementInputElement.value}`
        performQueryAndUpdateBookings(url);
    }
}


/*Common search functions*/
function removeAutocompleteDropdown() {
    const listEl = document.querySelector("#autocomplete-list");
    if (listEl) listEl.remove(); //checks if it exists and then removes it
}



/*Startup*/
performQueryAndUpdateBookings(" ")
