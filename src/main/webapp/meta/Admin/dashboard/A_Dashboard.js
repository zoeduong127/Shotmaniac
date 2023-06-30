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
        url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/allbookings`;
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
    const url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/booking/${id}`;

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



        })
}


function addUpcomingEvents() {
    const url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/allbookings`;


    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            //TODO FIX THIS BS, its supposed to sort and filter but it does neither
            data.sort(function (a, b) {
                return a.timestamp - b.timestamp;
            });
            console.log(data);
            data.filter(date => {
                return date >= new Date()
            });
            console.log(data);
            const earliestEvents = data.slice(0, 5);
            console.log("Earliest Events:");
            let output = "<h1>Upcoming Events</h1>";
            earliestEvents.forEach(function (event) {
                output += `
                    <div class="booking" id="B1">
                        ${event.name}
                        <span> 
                            ${new Date(event.date).getDate()}
                            ${months[new Date(event.date).getMonth()]}
                            ${new Date(event.date).getFullYear()}
                        </span>
                    </div>
                `;
            })
            document.getElementById("upcoming-container").innerHTML = output;
        })
}


function fillAnnouncements(input) {
    let url;
    if (input === " ") {
        url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/news`;
    } else {
        url = input;
    }
    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            data.sort(function (a, b) {
                return a.id - b.id;
            }).reverse();


            let element = "<h1>Latest News</h1>";
            data.forEach(item => {
                element += `
                <div id="L${item.id}" class="announcement">${item.title}</div>
            `;
            })
            document.getElementById("announcement-wrapper").innerHTML = element;
        })
}

/*Search for crew (single event)*/
let crewMemberList = [];
let productManagerElement = document.getElementById("product-manager"); //TODO WHY IS THIS NULL
productManagerElement.addEventListener("input", onSingleInputChange);


function getAllCrew() {
    console.log("get all crew called")
    const url = window.location.origin + `/shotmaniacs2/api/admin/allcrew`;

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
    const url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/allbookings`;

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
        const url = window.location.origin + `/shotmaniacs2/api/admin/bookings/search?searchtext=${inputElement.value}`
        performQueryAndUpdateBookings(url);
    }
}


/*Announcement search*/

let Announcements = [];
let AnnouncementInputElement = document.getElementById("search-input");

AnnouncementInputElement.addEventListener("input", Announcement_onInputChange);
function getAllAnnouncements() {
    const url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/news`; //TODO CHANGE THE URL TO ALL ANNOUNCEMENTS

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            Announcements = data.map((announcement) => {
                return announcement.title;
            })
        })
}

function  Announcement_onInputChange() {
    removeAutocompleteDropdown();

    const value = AnnouncementInputElement.value.toLowerCase();

    if (value.length === 0) return;

    const filteredNames = [];


    Announcements.forEach((announcement) => {
        if (announcement.substring(0, value.length).toLowerCase() === value) {
            filteredNames.push(announcement);
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

    if (AnnouncementInputElement.value.length === 0) fillAnnouncements(" ");
    else {
        const url = window.location.origin + `/shotmaniacs2/api/admin/announcements/search?searchtext=${AnnouncementInputElement.value}`
        fillAnnouncements(url);
    }
}


/*Common search functions*/
function removeAutocompleteDropdown() {
    const listEl = document.querySelector("#autocomplete-list");
    if (listEl) listEl.remove(); //checks if it exists and then removes it
}


//TODO change filter to incoming bookings or pending cancelled approved or something to do with admin
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
            const canvas = document.getElementById('chart');
            const ctx = canvas.getContext('2d');

            // Chart configuration
            const chartConfig = {
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
            const chart = new Chart(ctx, chartConfig);
        })
}

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


/*Startup*/
performQueryAndUpdateBookings(" ")
addUpcomingEvents(" ");
fillAnnouncements(" ");
graph();