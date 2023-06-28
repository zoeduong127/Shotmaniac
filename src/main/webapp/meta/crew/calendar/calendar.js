let date = new Date();
const event_info = document.getElementById("event_container");
const calendar = document.getElementById("calendar_container");
let correctDay = [];
let correctMonth = [];
let blob = "";
const cookies = parseCookie(document.cookie);
const account_id = cookies['account_id'];
const token = cookies['auth_token'];
let enrolledBookings = [];
let role;

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


function renderCalendar() {
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
        if (i < 10) {
            if (i === new Date().getDate() && date.getMonth() === new Date().getMonth() && date.getFullYear() === new Date().getFullYear()) {
                days += `<div class="today" id="${i}" onclick="performQueryAndUpdateBookings(this)"><span id="cd0${i}" class="calendar-day">0${i}</span></div>`;
            } else {
                days += `<div class="day" id="${i}" onclick="performQueryAndUpdateBookings(this)"><span id="cd0${i}" class="calendar-day">0${i}</span></div>`
            }
        } else {
            if (i === new Date().getDate() && date.getMonth() === new Date().getMonth() && date.getFullYear() === new Date().getFullYear()) {
                days += `<div class="today" id="${i}" onclick="performQueryAndUpdateBookings(this)"><span id="cd${i}" class="calendar-day">${i}</span></div>`;
            } else {
                days += `<div class="day" id="${i}" onclick="performQueryAndUpdateBookings(this)"><span id="cd${i}" class="calendar-day">${i}</span></div>`;
            }
        }
    }


    for (let offset = 1; offset <= nextDays; offset++) { //days of next month
        days += `<div class="next-date" onclick="performQueryAndUpdateBookings(this)">0${offset}</div>`
    }
    daysInMonth.innerHTML = days;

    addDots();
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
    renderCalendar();
}

function back() {
    event_info.style.visibility = "hidden";

    calendar.style.opacity = "1";
    calendar.style.zIndex = "auto";
    calendar.style.pointerEvents = "auto";

    correctDay = [];
    blob = "";
    document.getElementById("event").innerHTML = "";
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

function addDots() {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {
                const dotClass = document.createElement("span");
                dotClass.className = "calendar-dot";
                dotClass.id = "dot_${booking.id}";

                let BackDay = new Date(booking.date).getDate();
                let BackMonth = new Date(booking.date).getMonth();
                let BackYear = new Date(booking.date).getFullYear();

                let CalMonth = date.getMonth();
                let CalYear = date.getFullYear();

                if (BackMonth === CalMonth && BackYear === CalYear) {
                    if (BackDay < 10) BackDay = "0" + BackDay;
                    let list = [booking];
                    const element = document.getElementById(BackDay);

                    if (enrolledBookings.includes(booking.id)) {
                        dotClass.style.backgroundColor = "rgba(112, 110, 110, 0.55)";
                    }
                    document.getElementById("cd" + BackDay).style.paddingTop = "2vh";
                    if (element.children.length > 2) {
                        dotClass.className = "calendar-dot-overflow";
                        dotClass.style.backgroundColor = "#a40be3";

                    }
                    element.appendChild(dotClass);
                }
            })
        })
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
                if (!enrolledBookings.includes(booking.id)) {
                    enrolledBookings.push(booking.id);
                    console.log("newly enrolled: " + enrolledBookings)

                }
            })
            console.log("enrolled: " + enrolledBookings)
            renderCalendar();
        })
}

function performQueryAndUpdateBookings(element) {
    /*Styling*/
    event_info.style.visibility = "visible";

    calendar.style.opacity = "0.2";
    calendar.style.zIndex = "0";
    calendar.style.pointerEvents = "none";


    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {
                let list = [booking];
                console.log(list);

                let BackDay = new Date(booking.date).getDate();
                let BackMonth = new Date(booking.date).getMonth();
                let BackYear = new Date(booking.date).getFullYear();


                let CalDay = Array.from(element.innerText).join("");  //returns the date of the clicked event,
                // complicated, due to the do icons added later.
                let CalMonth = date.getMonth();
                let CalYear = date.getFullYear();


                if (BackDay == CalDay && BackMonth === CalMonth && BackYear === CalYear) {
                    correctDay.push(booking);
                    console.log(booking + " added to the correct day list")
                }

            });
            if (correctDay.length === 0) {
                console.log("list empty")
                document.getElementById("blob-nav").innerHTML =
                    `<span id="back" onclick="back()"> <img id="back-img" src="./../../Brand_Identity/images/icons/X-icon.png" alt="back">
                        </span>`
                    +
                    `<span> No events available for this day</span>`;
            } else {
                console.log("else statement")
                blob = "";
                let colour;
                let pointerEvent;
                for (let i = 0; i < correctDay.length; i++) {
                    if (enrolledBookings.includes(correctDay[i].id)) {
                        colour = "rgba(148, 144, 144, 0.6)";
                        pointerEvent = "none";

                        blob += `<span class="dots enrolled" onclick="displayInformation(correctDay.at(${i}), '${colour}',
                                        '${pointerEvent}')">${i + 1}</span>`
                        console.log("blob added (" + i + ")")
                    } else {
                        colour = "rgb(16, 183, 18)";
                        pointerEvent = "auto";


                        blob += `<span class="dots new" onclick="displayInformation(correctDay.at(${i}), '${colour}',
                                        '${pointerEvent}')">${i + 1}</span>`
                        console.log("blob added (" + i + ")");
                    }
                }

                document.getElementById("blob-nav").innerHTML = `<span id="back" onclick="back()"> 
                         <img id="back-img" src="./../../Brand_Identity/images/icons/X-icon.png" alt="back">
                                </span>` + blob;

                displayInformation(correctDay[0],  colour, pointerEvent);
            }


        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}

function displayInformation(booking, colour, pointerEvent) {
    console.log("colour: " + colour);
    console.log("pointer: " + pointerEvent);

    console.log(booking);
    document.getElementById("event").innerHTML = `
        <div class="event-info" id="event-name">
            <p><strong>Name: </strong><span>${booking.name}</span></p>
        </div>
        
        <span class="popup-button-span" id="active-button">
            <button class="popup-button" id="${booking.id}" onclick="enroll(this)" 
                style="background-color: ${colour}; pointer-events: ${pointerEvent}">
                Enrol</button>
        </span>
        
        <div class="event-info" id="event-type">
            <p><strong>Event Type: </strong><span>${booking.eventType}</span></p>
        </div>
        
        <div class="event-info" id="date">
            <p><strong>Date: </strong>
            <span>
                ${new Date(booking.date).getDate() + " " + 
                months[new Date(booking.date).getMonth()] + " " +
                new Date(booking.date).getFullYear()}
            </span>
            </p>
        </div>
        
        <div class="event-info" id="location">
            <p><strong>Location: </strong><span>${booking.location}</span></p>
        </div>
        
        <div class="event-info" id="duration">
            <p><strong>Duration: </strong><span>${booking.duration}</span></p>
        </div>
        
        <div class="event-info" id="booking-type">
            <p><strong>Booking Type: </strong><span>${booking.bookingType}</span></p>
        </div>
        
        <div class="event-info" id="product-Manager">
            <p><strong>Product Manager: </strong><span id="product-manager"></span></p>
        </div>
        
        <div class="event-info" id="crew">
            <p>
                <strong>Crew: </strong>
                <span>
                    <ul id="ul_list"></ul>
                </span>
            </p>
        </div>
    `;


    addCrew(booking.id);
}

function addCrew(id) {
    const url = `http://localhost:8080/shotmaniacs2/api/admin/booking/${id}/crew`; //TODO confirm that crew have permission to see this as well

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let crewList = document.getElementById("ul_list");

            crewList.innerHTML = "";

            data.forEach(crew => {
                console.log("crew name: " + crew.username + ", type: " + crew.accountType);


                        crewList.innerHTML += `<li>${crew.username}</li>`;
            })
            addProductManager(id);
        })
}

function addProductManager(id) {
    const url = `http://localhost:8080/shotmaniacs2/api/admin/booking/${id}/productmanager`

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let productManager = document.getElementById("product-Manager");

            productManager.innerHTML = data.username;
        })
}

function enroll(dot) {
   back();


    console.log("enroll clicked");
    console.log("id = " + dot.id);

    console.log("crew role: " + role);

    const url = `http://localhost:8080/shotmaniacs2/api/admin/booking/${dot.id}/crew/${account_id}/enrol?role=${role}`;

    fetch(url, {
        method: 'PUT',
        headers: {
            'Authorization': `${token}`
        }
    }).catch(error => {
        // Handle any errors
        console.error(error);
    });
    getEnrolled();
}

/*Calls the functions that need to be loaded on their own*/
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
            getEnrolled();
        })
}

