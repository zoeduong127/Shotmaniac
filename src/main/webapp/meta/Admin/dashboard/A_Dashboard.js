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
    document.cookie = "booking_id=; path=/";
    left.style.opacity = "1";
    left.style.pointerEvents = "auto";

    right.style.opacity = "1";
    right.style.pointerEvents = "auto";

    popup.style.visibility = "hidden";
    performQueryAndUpdateBookings(" ");
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

    console.log(url);
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
    const url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/booking/${id}`;
    document.cookie = "booking_id=" + id + "; path=/";

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

            console.log(booking.state);
            if (booking.state === "APPROVED") {
                console.log("entered")
                document.getElementById("input-area").style.visibility = "visible";
                document.getElementById("crew-needed").placeholder = booking.slots;
                document.getElementById("crew-needed").style.pointerEvents = "none";

                console.log(document.getElementById("input-area").innerHTML);
                addProductManager(booking.id);
            } else if (booking.state === "PENDING") {
                document.getElementById("input-area").style.visibility = "visible";
                document.getElementById("product-manager").pointerEvents = "auto";
                document.getElementById("crew-needed").style.pointerEvents = "auto";
                document.getElementById("crew-needed").placeholder = "";

            } else {
                document.getElementById("input-area").style.visibility = "hidden";
            }
        })
}

function addProductManager(id) {
    const url = window.location.origin + `/shotmaniacs2/api/admin/booking/${id}/productmanager`;
    console.log(url);

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let productManager = document.getElementById("product-Manager");

            productManager.style.pointerEvents = "none";
            productManager.placeholder = data.username;
            console.log("product Manager: " + productManager);
        })
}
// function acceptBooking(){
//     const state = 'APPROVED'
//     const booking_id = cookies['booking_id'];
//     const url = window.location.origin+`/shotmaniacs2/api/admin/booking/`+booking_id+`/state?state=`+state;
//     fetch(url,{
//         method: 'PUT',
//         headers: {
//             'Authorization': `${token}`
//         }
//     })
//         .then(response => {
//             if(response.ok){
//                 document.cookie = "booking_id=; path=/";
//                 performQueryAndUpdateBookings(" ");
//                 toggleOff();
//             }else{
//                 alert("Something wrong! Please try again")
//             }
//         })
// }
//
// function cancelBooking(){
//     const state = 'CANCELED'
//     const booking_id = cookies['booking_id'];
//     const url = window.location.origin+`/shotmaniacs2/api/admin/booking/`+booking_id+`/state?state=`+state;
//     fetch(url,{
//         method: 'PUT',
//         headers: {
//             'Authorization': `${token}`
//         }
//     })
//         .then(response => {
//             if(response.ok){
//                 document.cookie = "booking_id=; path=/";
//                 performQueryAndUpdateBookings(" ");
//                 toggleOff();
//             }else{
//                 alert("Something wrong! Please try again")
//             }
//         })
// }
function acceptBooking(){
    var crewNeededInput = document.getElementById("crew-needed");
    var productManagerInput = document.getElementById("product-manager");

    if (!crewNeededInput.checkValidity()) {
        alert("Please enter the number of crew members.");
        return;
    }

    if (!productManagerInput.checkValidity()) {
        alert("Please enter the product manager's name.");
        return;
    }
    const state = 'APPROVED'
    const booking_id = cookies['booking_id'];
    console.log(booking_id)
    const url = window.location.origin+`/shotmaniacs2/api/admin/booking/`+booking_id+`/state?state=`+state;
    const url1 = window.location.origin+`/shotmaniacs2/api/admin/booking/`+booking_id+`/slots?slots=`+crewNeededInput.value;
    const url2 = window.location.origin+`/shotmaniacs2/api/admin/booking/`+booking_id+`/setproductmanager?username=`+productManagerInput.value;
    const options = {
        method: 'PUT',
        headers: {
            'Authorization': `${token}`
        }
    }
    fetch(url1,options)
        .then(response => {
            console.log(response)
            if(response.ok){
                fetch(url2,options)
                    .then(response => {
                        console.log(response)
                        if(response.ok){
                            fetch(url,options)
                                .then(response => {
                                    console.log(response)
                                    if(response.ok){
                                        document.cookie = "booking_id=; path=/";
                                        performQueryAndUpdateBookings(" ");
                                        toggleOff();
                                    }else{
                                        alert("Something wrong! Please try again")
                                    }
                                })
                        }else{
                            alert("Something wrong with manager input! Please try again")
                        }
                    })
            }else{
                alert("Something wrong with crew input! Please try again")
            }
        })
}


function addUpcomingEvents() {
    const url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/on-goingbookings`;
    let booking_list = [];

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log(data)
            data = data.sort((a, b) => {
                const dateA = new Date(a.date);
                const dateB = new Date(b.date);
                return dateA - dateB;
            });
            //TODO FIX THIS BS, its supposed to sort and filter but it does neither
            console.log(data);
            const earliestEvents = data.slice(0,5);
            console.log(earliestEvents);
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
                `
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
    console.log(url);
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
let productManagerElement = document.getElementById("product-manager");
productManagerElement.addEventListener("input", onSingleInputChange);


function getAllCrew() {
    console.log("get all crew called")
    const url = `http://localhost:8080/shotmaniacs2/api/admin/allcrew`;

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
    console.log("input found");
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
    console.log("creating list")
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
    console.log("input found")
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
    console.log("getting all bookings")
    const url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/allbookings`;
    console.log(url);
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
    console.log("All input event listener activated")
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
    console.log("all input create dropdown")
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
    console.log("All input create button")
    event.preventDefault(); //cancels default event (submitting the form)


    document.querySelector("#search-icon-btn").addEventListener("click", ALL_sendInput);

    const buttonEl = event.target; //element that triggered the event (the button itself)
    inputElement.value = buttonEl.innerHTML; //should be a string of the booking name

    removeAutocompleteDropdown();

}

function ALL_sendInput(event) {
    console.log("All input sent" + inputElement.value);
    event.preventDefault();

    if (inputElement.value.length === 0) performQueryAndUpdateBookings(" ");
    else {
        const url = window.location.origin + `/shotmaniacs2/api/admin/bookings/search?searchtext="${inputElement.value}"`
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

    console.log(event);
    console.log(AnnouncementInputElement);
    console.log(AnnouncementInputElement.value);
    if (AnnouncementInputElement.value.length === 0) {
        console.log(inputElement);
        fillAnnouncements(" ");
    }
    else {
        const url = window.location.origin + `/shotmaniacs2/api/admin/announcements/search?searchText=${AnnouncementInputElement.value}`
        console.log(url);
        fillAnnouncements(url);
    }
}


/*Common search functions*/
function removeAutocompleteDropdown() {
    const listEl = document.querySelector("#autocomplete-list");
    if (listEl) listEl.remove(); //checks if it exists and then removes it
}
function graph(){
    const filter = "DONE";
    const url = window.location.origin+`/shotmaniacs2/api/admin/bookings/timefilter/${filter}`;
    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(booking => {
            console.log(booking)
            const bookings = booking.sort((a, b) => {
                const dateA = new Date(a.date);
                const dateB = new Date(b.date);
                return dateA - dateB;
            });
            console.log(bookings)
            var bookingCounts = {};

            bookings.forEach(function (booking) {
                var timestamp = new Date(parseInt(booking.date));

                if (!isNaN(timestamp)) { // Check if timestamp is valid
                    var date = new Date(timestamp);// Convert to Date object

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
                            min: monthss[(monthss.length)-5],  // Specify the minimum value on the x-axis
                            max: monthss[(monthss.length)-1]
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


/*Startup*/
performQueryAndUpdateBookings(" ")
addUpcomingEvents(" ");
fillAnnouncements(" ");
