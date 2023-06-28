let date = new Date();
const event_info = document.getElementById("event_container");
const calendar = document.getElementById("calendar_container");
let correctDay= [];
let blob = "";

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


    document.querySelector(".date h1").innerHTML
        = `${months[date.getMonth()]}` + " " + date.getFullYear();

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
                days += `<div class = "day" onclick="performQueryAndUpdateBookings(this)">${i}</div>`;
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


    correctDay = [];
    blob = "";
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

    const url = window.location.origin+`/shotmaniacs2/api/crew/${id}/allbookings`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {
                console.log("inner loop started")
                let list = [booking];
                console.log(list);


                let BackDay = new Date(booking.date).getDate();
                let BackMonth = new Date(booking.date).getMonth();
                let BackYear = new Date(booking.date).getFullYear();

                let CalDay = Array.from(element.innerHTML).slice(0, 2).join("");  //returns the date of the clicked event,
                // complicated, due to the do icons added later.
                let CalMonth = date.getMonth();
                let CalYear = date.getFullYear();


                if (BackDay == CalDay && BackMonth == CalMonth && BackYear == CalYear) {
                    correctDay.push(booking);
                    console.log(booking + " added to the correct day list")
                }


                /*
                TODO: should add blobs, add this to the days in the calendar
                console.log(Array.from(element.innerHTML).slice(0,2).join(""));
                if (BackDay === CalDay && BackMonth === CalMonth && BackYear === CalYear) {
                    const blobDiv = document.getElementById("blob" + Array.from(element.innerHTML).slice(0,2).join(""));
                    blobDiv.innerHTML += `<i class="fas fa-period dot"></i`;
                }*/


            });
            console.log("outer loop started")
            if (correctDay.length === 0) {
                console.log("list empty")
                document.getElementById("blob-nav").innerHTML =
                    `<span id="back" onclick="back()"> <img id="back-img" src="./../../Brand_Identity/images/icons/X-icon.png" alt="back">
                        </span>`
                    +
                    `<span> No events available for this day</span>`;

                document.getElementById("enroll-button").style.backgroundColor = "gray";

            } else {
                for (let i = 0; i < correctDay.length; i++) {
                    blob += `<span class = "dots" onclick="displayInformation(correctDay.at(${i}))">${i + 1}</span>`
                    console.log("blob added (" + i + ")")
                }
                document.getElementById("blob-nav").innerHTML = `<span id="back" onclick="back()"> 
                         <img id="back-img" src="./../../Brand_Identity/images/icons/X-icon.png" alt="back">
                                </span>` + blob;
            }
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}

function displayInformation(booking) {
    console.log(booking);

    document.getElementById("event-name").innerHTML =
        `<p>Name: <span>${booking.name}</span></p>`;

    document.getElementById("event-type").innerHTML =
        `<p>Event Type: <span>${booking.eventType}</span></p>`;

    document.getElementById("date").innerHTML =
        `<p>Date: <span>${
                new Date(booking.date).getDate() + " " +
                months[new Date(booking.date).getMonth()] + " " +
                new Date(booking.date).getFullYear()
    }</span></p>`;

    document.getElementById("location").innerHTML =
        `<p>Location: <span>${booking.location}</span></p>
`;
    document.getElementById("duration").innerHTML =
        `<p>Duration: <span>${booking.duration}</span></p>`;

    document.getElementById("booking-type").innerHTML =
        `<p>Booking Type: <span>${booking.bookingType}</span></p>`;

    document.getElementById("product-Manager").innerHTML =
        `<p>Product Manager: <span>PRODUCT MANAGER FIELD NEEDED??</span></p>`;

    document.getElementById("crew").innerHTML =
        `<p>Crew: <span><ul>
            <li>Name 1</li>
            <li>Name 2</li>
            <li>Name 3</li>
            <li>Obscenely long name for testing purposes</li>
            <li>Name 5</li>
            </ul>
            </span>
          </p>`;
}

function enroll() {

}


/*Calls the functions that need to be loaded on their own*/
renderCalendar();

