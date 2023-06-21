let currentTheme = document.getElementById("currentCSS");
currentTheme.setAttribute('href', 'crewDashboardStyles.css');

console.log("initial theme: " + currentTheme.getAttribute('href'))

function toggleStyle() {
    let theme = document.getElementsByTagName('link');
    console.log(theme)

    if (currentTheme.getAttribute("href") === 'crewDashboardStyles.css') {
        currentTheme.setAttribute('href', 'singleEventHomePage.css');
        console.log('new theme changed to: ' + currentTheme.getAttribute('href'));
    } else {
        currentTheme.setAttribute('href', 'crewDashboardStyles.css');
        console.log('new theme changed to: ' + currentTheme.getAttribute('href'));
    }
}

function setTheme(href){
    currentTheme.setAttribute('href', href);
}


function reloadEvent() {
    //TODO: figure out a way to call the current event page
}



/*Filter*/
const bookingContainer = document.getElementById("booking_container");
const bookingElement = bookingContainer.firstElementChild.cloneNode(true);

function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}


function performQueryAndUpdateBookings(url) {

    while (bookingContainer.firstChild) {
        bookingContainer.removeChild(bookingContainer.firstChild);
    }

    const cookies = parseCookie(document.cookie);
    const token = cookies['auth_token'];
    console.log(token);

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {
                let bookingElementCopy = bookingElement.cloneNode(true);

                //TODO: Calculate amount of slots already taken.

                //TODO: the line below probably allows stored code attacks. Needs fixing
                bookingElementCopy.querySelector("#event_name").innerHTML = booking.name /*+ " <span class=\"bolded\">(Available Slots: " + booking.slots + ")</span>"*/;
                // * bookingElementCopy.querySelector("#booking_type").innerHTML = "<b>Booking Type: </b>" + booking.bookingType;

                bookingElementCopy.querySelector("#location").innerHTML =  "<b>Location: </b>" + booking.location;
//                bookingElementCopy.querySelector("#client").innerHTML =  "<b>Client: </b>" + booking.clientName;
                bookingElementCopy.querySelector("#event_type").innerHTML =  "<b>Event Type: </b>" + booking.eventType;
//                bookingElementCopy.querySelector("#duration").innerHTML =  "<b>Duration: </b>" + booking.duration + " hours";

                let dateTime = new Date(booking.date);
                const formattedDate = dateTime.toLocaleString('en-US', {
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false
                });
                bookingElementCopy.querySelector("#date").innerText = dateTime.toDateString() + ', ' + formattedDate;

                let bookingState = bookingElementCopy.querySelector("#state");

                switch (booking.state) {
                    case "PENDING":
                        bookingState.classList.add("pending-state");
                        break;
                    case "APPROVED":
                        bookingState.classList.add("approved-state");
                        break;
                    default:
                        bookingState.classList.add("pending-state");
                }

                bookingState.innerText = booking.state;
                bookingContainer.appendChild(bookingElementCopy);

            });
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}


function updateBookings(filterType) {
    const filterButton = document.getElementById("filter_button_text");

    var filter;

    switch (filterType) {
        case 'in_progress':
            filter = "ongoing";
            filterButton.textContent = "Filter: In progress";
            break;
        case 'future':
            filter = "ongoing";
            filterButton.textContent = "Filter: On going";
            break;
        case 'past':
            filter = "past";
            filterButton.textContent = "Filter: Past";
            break;
        default:
            filter = 'ongoing';

    }

    const cookies = parseCookie(document.cookie);
    const id = cookies['account_id'];

    const url = `http://localhost:8080/shotmaniacs2/api/crew/${id}/mybooking/timefilter/${filter}`;

    performQueryAndUpdateBookings(url);

}

function searchBookings(searchText) {
    const cookies = parseCookie(document.cookie);
    const id = cookies['account_id'];
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${id}/mybooking/search?searchtext=\"${searchText}\"`;
    performQueryAndUpdateBookings(url);
}

//Event listeners
const searchbox = document.getElementById("search-input");
searchbox.addEventListener("keydown", function (e) {
    if (e.code === "Enter") {  //checks whether the pressed key is "Enter"
        searchBookings(searchbox.value);
    }
});

updateBookings("ongoing");
