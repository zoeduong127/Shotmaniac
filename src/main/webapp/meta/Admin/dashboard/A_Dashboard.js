const left = document.getElementById("container1");
const right = document.getElementById("information");
const popup = document.getElementById("popup-container");
const cancel = document.getElementById("cancel-container");


function toggleStyle() {
    left.style.opacity = "0.2";
    right.style.opacity = "0.2";

    popup.style.visibility = "visible";
}

function toggleOff() {
    left.style.opacity = "1";
    right.style.opacity = "1";

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

const bookingContainer = document.getElementById("event_container");
const bookingElement = bookingContainer.firstElementChild.cloneNode(true);

function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}


function    performQueryAndUpdateBookings(url) {

    while (bookingContainer.firstChild) {
        bookingContainer.removeChild(bookingContainer.firstChild);
    }

    fetch(url)
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
