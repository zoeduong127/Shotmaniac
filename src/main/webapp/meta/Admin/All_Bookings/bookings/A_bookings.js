const cookies = parseCookie(document.cookie);
const token = cookies['auth_token'];
const account_id = cookies['account_id'];
let role;
let element;

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

function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}

//Upcoming approved bookings with 0 assigned crew
function UnassignedBookings() {
    let url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/allbookings`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            element = "";
            element += `
                <div class="small-rectangle">
                    <p id="Unassigned-Bookings">Unassigned Bookings</p>
                </div>`;
            console.log(element);

            data.forEach(booking => {
                if (booking.state === "APPROVED") {
                    calcAvailableSlots(booking.slots, booking.id, booking.name, booking.description);
                }
            });

            setTimeout(function () {
                console.log("element: " + element);
                document.getElementById("unassigned-wrapper").innerHTML = element;
            }, 500);
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}

function calcAvailableSlots(slots, id, name, description) {
    const url = window.location.origin+`/shotmaniacs2/api/admin/booking/${id}/crew`;
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
            if (slotCount === 0) {
                element += `
                    <div class="bookings-info" id="${id}" onclick="accessApprovedBooking(this)">
                        <div class="cover-box">
                            <p class="title-name-in-right-side">${name}</p>
                        </div>
                        <div class="Short-Description">${description}</div>
                    </div>
                `;
            }
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}

let currentPage = 1;
const bookingsPerPage = 4;
let bookingsArr = []; //Store the fetched bookings data

function fetchBookings(input) {
    let url;

    if (input === " ") {
        url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/allbookings`;
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
        .then(loadedData => {
            console.log(loadedData);
            bookingsArr = loadedData.filter(booking => booking.state === "APPROVED" || booking.state === "PENDING");
            console.log(bookingsArr);
            allBookings(url);
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}

function allBookings(url) {
    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let main = "";
            const startIndex = (currentPage-1) * bookingsPerPage;
            const endIndex = startIndex + bookingsPerPage;
            const bookingsToShow = data
                .filter(booking => booking.state === "APPROVED" || booking.state === "PENDING")
                .slice(startIndex, endIndex);

            //Hide the Back-BTN when the currentPage = 1
            if (currentPage > 1) {
                main = `
                    <button onclick="back()" class="paginationBtn" id="Back-button" style="display: block"><span>Back</span></button>
                    ${main}
                `;
            }

            //Hide the Next-BTN when reaching the last page
            const maxPage = Math.ceil(bookingsArr.length / bookingsPerPage);
            if (currentPage < maxPage) {
                main += `<button onclick="next()" class="paginationBtn" id="Next-button"><span>Next</span></button>`;
            }

            bookingsToShow.forEach((booking, index) => {
                let BackDay = new Date(booking.date).getDate();
                let BackMonth = new Date(booking.date).getMonth();
                let BackYear = new Date(booking.date).getFullYear();

                if (BackYear > new Date().getFullYear()) { //100% ongoing bookings
                    if (booking.state === "APPROVED") {
                        main += `
                            <div class="ongoing-booking pad" id="${booking.id}" onclick="accessApprovedBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-approved">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-ongoing-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                    } else if (booking.state === "PENDING") {
                        main += `
                            <div class="ongoing-booking pad" id="${booking.id}" onclick="accessPendingBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-pending">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-ongoing-booking">
                                    <p class="date"> 
                                       ${BackDay}
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                    }
                } else if (BackYear == new Date().getFullYear()) {
                    if (BackMonth > new Date().getMonth()) { //ongoing bookings
                        if (booking.state === "APPROVED") {
                            main += `
                            <div class="ongoing-booking pad" id="${booking.id}" onclick="accessApprovedBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-approved">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-ongoing-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                        } else if (booking.state === "PENDING") {
                            main += `
                            <div class="ongoing-booking pad" id="${booking.id}" onclick="accessPendingBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-pending">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-ongoing-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                        }
                    } else if (BackMonth == new Date().getMonth()) {
                        if (BackDay >= new Date().getDate()) { //ongoing bookings
                            if (booking.state === "APPROVED") {
                                main += `
                            <div class="ongoing-booking pad" id="${booking.id}" onclick="accessApprovedBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-approved">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-ongoing-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                            } else if (booking.state === "PENDING") {
                                main += `
                            <div class="ongoing-booking pad" id="${booking.id}" onclick="accessPendingBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-pending">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-ongoing-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                            }
                        } else { //past bookings
                            if (booking.state === "APPROVED") {
                                main += `
                            <div class="past-booking pad" id="${booking.id}" onclick="accessApprovedBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-approved">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-past-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                            } else if (booking.state === "PENDING") {
                                main += `
                            <div class="past-booking pad" id="${booking.id}" onclick="accessPendingBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-pending">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-past-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                            }
                        }
                    } else { //past bookings
                        if (booking.state === "APPROVED") {
                            main += `
                            <div class="past-booking pad" id="${booking.id}" onclick="accessApprovedBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-approved">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-past-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                        } else if (booking.state === "PENDING") {
                            main += `
                            <div class="past-booking pad" id="${booking.id}" onclick="accessPendingBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-pending">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-past-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                        }
                    }
                } else { //100% past bookings
                    if (booking.state === "APPROVED") {
                        main += `
                            <div class="past-booking pad" id="${booking.id}" onclick="accessApprovedBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-approved">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-past-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                    } else if (booking.state === "PENDING") {
                        main += `
                            <div class="past-booking pad" id="${booking.id}" onclick="accessPendingBooking(this)">
                                <div class="booking-name">${booking.name}</div>
                                <div class="label-for-booking-pending">
                                    <p class="status">${booking.state}</p>
                                </div>
                                <div class="date-info-rectangle-past-booking">
                                    <p class="date"> 
                                       ${BackDay} 
                                       ${months[BackMonth]} 
                                       ${BackYear}
                                   </p>
                                </div>
                            </div>
                        `;
                    }
                }
            });
            document.getElementById("bookings").innerHTML = main;
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}

function back() {
    if (currentPage>1) {
        currentPage--;
        const url = getCurrentURL();
        allBookings(url);
        const bookingsElement = document.getElementById("bookings");
        bookingsElement.style.animation = 'slide-right 0.5s';
        setTimeout(() => {
            bookingsElement.style.animation = '';
        }, 500);
    }
}

function next() {
    const maxPage = Math.ceil(bookingsArr.length / bookingsPerPage);
    if (currentPage<maxPage) {
        currentPage++;
        const url = getCurrentURL();
        allBookings(url);
        const bookingsElement = document.getElementById("bookings");
        bookingsElement.style.animation = 'slide-left 0.5s';
        setTimeout(() => {
            bookingsElement.style.animation = '';
        }, 500);
    }
}

function getCurrentURL() {
    const filterChoice = document.getElementById("filter-choice").textContent.toLowerCase();
    let url = "";

    switch (filterChoice) {
        case 'approved':
            url = `${window.location.origin}/shotmaniacs2/api/admin/bookings/statefilter/approved`;
            console.log("Approved: " + url);
            break;
        case 'pending':
            url = `${window.location.origin}/shotmaniacs2/api/admin/bookings/statefilter/pending`;
            console.log("pending: " + url);
            break;
        case 'on-going':
            url = `${window.location.origin}/shotmaniacs2/api/admin/bookings/timefilter/ongoing`;
            console.log("ongoing: " + url);
            break;
        case 'past':
            url = `${window.location.origin}/shotmaniacs2/api/admin/bookings/timefilter/past`;
            console.log("past: " + url);
            break;
        default:
            url = `${window.location.origin}/shotmaniacs2/api/crew/${account_id}/allbookings`;
            console.log("all: " + url);
    }
    return url;
}

//Add sliding animation to CSS
const style = document.createElement("style");
style.innerHTML = `
                    @keyframes slide-left {
                        0% {
                            transform: translateX(22%);
                        }
    
                        100% {
                            transform: translateX(0%);
                        }
                    }
    
                    @keyframes slide-right {
                        0% {
                            transform: translateX(-22%);
                        }
    
                        100% {
                            transform: translateX(0%);
                        }
                    }
                    `;
document.head.appendChild(style);

function accessApprovedBooking(element) {
    console.log(element.id);
    document.cookie = "booking_id=" + element.id + "; path=/";
    window.location.href = "./A_CrewsForBooking.html";
}

function accessPendingBooking(element) {
    console.log(element.id);
    document.cookie = "booking_id=" + element.id + "; path=/";
    window.location.href = "./A_specificBooking.html";
}

//Filter
function updateBookingsByFilter(filterType) {
    console.log("update bookings started");
    console.log("filter type: " + filterType.toLowerCase());
    const filterButton = document.getElementById("filter-choice");

    let filter;
    let url;

    switch (filterType) {
        case 'approved': {
            filter = "approved";
            filterButton.textContent = filter;
            url =  window.location.origin+`/shotmaniacs2/api/admin/bookings/statefilter/${filter}`;
            break;
        }
        case 'pending': {
            filter = "pending";
            filterButton.textContent = filter;
            url =  window.location.origin+`/shotmaniacs2/api/admin/bookings/statefilter/${filter}`;
            break;
        }
        case 'on-going': {
            filter = "ongoing";
            filterButton.textContent = filter;
            url =  window.location.origin+`/shotmaniacs2/api/admin/bookings/timefilter/${filter}`;
            break;
        }
        case 'past': {
            filter = "past";
            filterButton.textContent = filter;
            url =  window.location.origin+`/shotmaniacs2/api/admin/bookings/timefilter/${filter}`;
            break;
        }
        default: {
            filterButton.textContent = "All";
            url = " ";
        }
    }

    fetchBookings(url);
}

//Search function
let allBookingsList = [];
let inputElement = document.getElementById("booking-input");

inputElement.addEventListener("input", onInputChange);
function getAllBookings() {
    const url =  window.location.origin+`/shotmaniacs2/api/crew/${account_id}/allbookings`

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            allBookingsList = data.map((booking) => {
                return booking.name;
            })
        })
}

function  onInputChange() {
    removeAutocompleteDropdown();

    const value = inputElement.value.toLowerCase();

    if (value.length === 0) return;

    const filteredNames = [];

    allBookingsList.forEach((BookingName) => {
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

    if (inputElement.value.length === 0) fetchBookings(" ");
    else {
        const url =  window.location.origin+`/shotmaniacs2/api/admin/bookings/search?searchtext="${inputElement.value}"`
        fetchBookings(url);
    }
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
                window.location.href= window.location.origin+"/shotmaniacs2/";
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

//Call startups
UnassignedBookings();
fetchBookings(" ");