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


function updateBookings(filterType) {
    while (bookingContainer.firstChild) {
        bookingContainer.removeChild(bookingContainer.firstChild);
    }

    filterButton = document.getElementById("filter_button_text");

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
    fetch(url)
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {

                // const bookingWidget = document.createElement("div");
                // bookingWidget.setAttribute("class","booking");
                // bookingWidget.setAttribute("id", "booking_" + booking.id);
                // const eventName = document.createElement("p");
                // eventName.innerHTML = booking.name;
                // bookingWidget.appendChild(eventName);
                //
                // rightInnerBooking = document.createElement("div");
                // rightInnerBooking.setAttribute("class", "innerBooking right");
                // const date = document.createElement("p");
                // dateTime = new Date(booking.date);
                // date.innerText = dateTime.toDateString() + ', ' + dateTime.getHours() + ':' + dateTime.getMinutes();
                // rightInnerBooking.appendChild(date);
                // bookingWidget.appendChild(rightInnerBooking);
                //
                // leftInnerBooking = document.createElement("div");
                // leftInnerBooking.setAttribute("class", "innerBooking left");
                // statusText = document.createElement("p");
                // statusText.innerText = "TODO"
                // leftInnerBooking.appendChild(statusText);
                // bookingWidget.appendChild(leftInnerBooking);

                bookingContainer.appendChild(bookingElement);
            });
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}