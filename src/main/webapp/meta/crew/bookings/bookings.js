const booking_container = document.getElementById("booking_container");
const popup = document.getElementById("hiddenpop");
const maincontainer = document.getElementById("main-container");
const days = [
    "Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday"
];

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
let role;

console.log("account id: " + account_id);

function calcAvailableSlots(slots, id) {
    const url = `http://localhost:8080/shotmaniacs2/api/admin/booking/${id}/crew`;
    console.log("slots: " + slots);
    console.log("id: " + id);


    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let slotCount = 0;
            data.forEach(crew => {
                if (crew.accountType === "Crew") slotCount++;
            })
            console.log("slotCount: " + slotCount);
            document.getElementById("slot").innerHTML = ` 
                <strong>Available slots: </strong>${slots - slotCount}/${slots}`;
        })
}


function openInfo(element) {
    booking_container.style.opacity = "0.2";
    popup.style.visibility = "visible";
    maincontainer.style.pointerEvents = "none";


    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/booking/${element.id}`
    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(booking => {
            let info = "";
            info += `
                    <img class="icon" src="">
                    <p>${days[new Date(booking.date).getDay()]}, 
                       ${new Date(booking.date).getDate()} 
                       ${months[new Date(booking.date).getMonth()]} 
                       ${new Date(booking.date).getFullYear()},
                       ${new Date(booking.date).getHours()}:${new Date(booking.date).getMinutes()}
                    </p>
                    
                    <img class="icon" src="">
                    <p id="slot"></p> <!-- TODO: subtract number of crew in the event -->
                    
                    <img class="icon" src="">
                    <p>${booking.description}</p>
                `;

            document.getElementById("top-tab").innerHTML = `<h4 id = "heading"> ${booking.name}</h4>
                <span id="back">
                        <img src="./../../Brand_Identity/images/icons/X-icon.png" alt="close" id="back-img" onclick="closeInfo()">
                </span>`;
            document.querySelector(".bookinginfo").id = booking.id;
            document.getElementById("popup-main").innerHTML = info;

            calcAvailableSlots(booking.slots, booking.id);
        })
}


function closeInfo() {
    booking_container.style.opacity = "1";
    popup.style.visibility = "hidden";
    maincontainer.style.pointerEvents = "auto"
}


/*Cookies*/
function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}

function getEnrolled() {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/mybooking/enrolled`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {
                console.log("get enrolled started")
                console.log(booking.id);
                if (document.getElementById(booking.id)) {
                    console.log(booking.name);
                    document.getElementById(booking.name + booking.id).style.backgroundColor = "rgba(31, 31, 31, 0.35)";
                    document.getElementById(booking.name + booking.id).style.border = "1px solid rgba(31, 31, 31, 0.35)";
                    document.getElementById(booking.id).style.pointerEvents = "none";
                    document.getElementById(booking.id).style.border = "1px solid rgba(82, 82, 82, 0.5)";
                }
            })
        })
}


/*Get all bookings*/
function performQueryAndUpdateBookings(input) {
    let url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`;

    if (input !== " ") {console.log(input); url = input;}

    console.log(url);
    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let element = "";
            data.forEach(booking => {
                //TODO: Calculate amount of slots already taken.
                console.log("booking: " + booking);
                element += `
                    <div class="booking" id="${booking.id}" onclick="openInfo(this)">
                        <div class="bookings-name" id="${booking.name}${booking.id}">
                    
                            <p>${booking.name}</p>
                    
                        </div>
                        <div class="short-descri">
                            <p>${booking.description}</p>
                        </div>
                    </div>`;
            });
            console.log("element: " + element);
            document.getElementById("booking_container").innerHTML = element;
            getEnrolled()
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}

function enroll() {
    console.log("enroll clicked");
    const id = document.querySelector(".bookinginfo").id;
    console.log("crew role: " + role);
    const url = `http://localhost:8080/shotmaniacs2/api/admin/booking/${id}/crew/${account_id}/enrol?role=${role}`;

    booking_container.style.opacity = "1";
    popup.style.visibility = "hidden";
    maincontainer.style.pointerEvents = "auto"

    fetch(url, {
        method: 'PUT',
        headers: {
            'Authorization': `${token}`
        }
    }).catch(error => {
        // Handle any errors
        console.error(error);
    });
    performQueryAndUpdateBookings(" ")
}


/*Search function*/
let bookings = [];
let inputElement = document.getElementById("search-input");

inputElement.addEventListener("input", onInputChange);
function getAllBookings() {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            bookings = data.map((booking) => {
                return booking.name;
            })
        })
}

function  onInputChange() {
    removeAutocompleteDropdown();

    const value = inputElement.value.toLowerCase();

    if (value.length === 0) return;

    const filteredNames = [];


    bookings.forEach((BookingName) => {
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

    if (inputElement.value.length === 0) performQueryAndUpdateBookings(" ");
    else {
        const url = `http://localhost:8080/shotmaniacs2/api/admin/bookings/search?searchtext=${inputElement.value}`
        performQueryAndUpdateBookings(url);
    }
}

/*To load at the start of the page*/
getAccount();

function getAccount() {
    const url = `http://localhost:8080/shotmaniacs2/api/admin/account/${account_id}`;
    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            role = data.role;
            console.log("Role: " + role);
        })
    performQueryAndUpdateBookings(" ");
}