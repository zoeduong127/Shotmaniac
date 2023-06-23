let date = new Date();
const event_info = document.getElementById("event_container");
const calendar = document.getElementById("calendar_container");


function renderCalendar(){
    date.setDate(1); //sets the date to the first day of the month (used for previous days)

    const daysInMonth = document.querySelector(".days");

    const lastDay = new Date(date.getFullYear(),
        date.getMonth() + 1, 0).getDate();

    const prevLastDay = new Date(date.getFullYear(),
        date.getMonth(), 0).getDate();

    const fistDayIndex = date.getDay();

    const lastDayIndex = new Date(date.getFullYear(),
        date.getMonth() + 1, 0).getDay();

    const nextDays = 7 - lastDayIndex - 1;

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

    document.querySelector(".date h1").innerHTML
        = `${months[date.getMonth()]}`;

        document.querySelector(".date p").innerHTML
            = new Date().toDateString();


    let days = "";
    for (let offset = fistDayIndex; offset > 0; offset--) { //days of the previous month
        days += `<div class = "prev-date" onclick="performQueryAndUpdateBookings(this)">${prevLastDay - offset + 1} </div>`
    }

    for (let i = 1; i <= lastDay; i++) { //days of the month
        if (i === new Date().getDate() && date.getMonth() === new Date().getMonth()) {
            days += `<div class="today" onclick="performQueryAndUpdateBookings(this)">${i}</div>`;
        } else {
            if (i < 10) {
                days += `<div class="day" onclick="performQueryAndUpdateBookings(this)">0${i}</div>`
            } else {
                days += `<div onclick="performQueryAndUpdateBookings(this)">${i}</div>`;
            }
        }
    }

    for (let offset = 1; offset <= nextDays; offset++) { //days of next month
        days += `<div class="next-date" onclick="performQueryAndUpdateBookings(this)">0${offset}</div>`
    }
    daysInMonth.innerHTML = days;
}

//arrow movement
document.querySelector('.prev').addEventListener('click', () => {
    date.setMonth(date.getMonth() - 1);
    renderCalendar();
});

document.querySelector('.next').addEventListener('click', () => {
    date.setMonth(date.getMonth() + 1);
    renderCalendar();
});

function returnToDay() {
    date = new Date();
    console.log()
    renderCalendar();
}
function back() {
    event_info.style.visibility = "hidden";

    calendar.style.opacity = "1";
    calendar.style.zIndex = "auto";
}
/*Fetching backend information*/
function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}

function performQueryAndUpdateBookings(element) {
    /*Styling*/
    event_info.style.visibility = "visible";

    calendar.style.opacity = "0.2";
    calendar.style.zIndex = "0";



    const cookies = parseCookie(document.cookie);
    const id = cookies['account_id'];

    const url = `http://localhost:8080/shotmaniacs2/api/crew/${id}/allbookings`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {
                let list = [booking];
                console.log(list);


                let BackDay = new Date(booking.date).getDate();
                let BackMonth = new Date(booking.date).getMonth();
                let BackYear = new Date(booking.date).getFullYear();

                let CalDay = Array.from(element.innerHTML).slice(0,2).join("");  //returns the date of the clicked event,
                // complicated, due to the do icons added later.
                let CalMonth = date.getMonth();
                let CalYear = date.getFullYear();


                /*
                TODO: should add blobs, add this to the days in the calendar
                console.log(Array.from(element.innerHTML).slice(0,2).join(""));
                if (BackDay === CalDay && BackMonth === CalMonth && BackYear === CalYear) {
                    const blobDiv = document.getElementById("blob" + Array.from(element.innerHTML).slice(0,2).join(""));
                    blobDiv.innerHTML += `<i class="fas fa-period dot"></i`;
                }*/


             });
            /*let bookingElementCopy = bookingElement.cloneNode(true);

            //TODO: Calculate amount of slots already taken.

            //TODO: the line below probably allows stored code attacks. Needs fixing


            bookingElementCopy.querySelector("#event_name").innerHTML = booking.name /!*+ " <span class=\"bolded\">(Available Slots: " + booking.slots + ")</span>"*!/;
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
            CalendarContainer.appendChild(bookingElementCopy);

        });*/
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}



/*Calls the functions that need to be loaded on their own*/
renderCalendar();

