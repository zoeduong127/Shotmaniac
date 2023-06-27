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
            /*TODO: is it best to compare to the full database or try something else*/

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


/*Get all bookings*/
function performQueryAndUpdateBookings() {
    const url = `http://localhost:8080/shotmaniacs2/api/crew/${account_id}/allbookings`;

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
                console.log(booking);

                element += `
                    <div class="booking" id="${booking.id}" onclick="openInfo(this)">
                        <div class="bookings-name">
                    
                            <p>${booking.name}</p>
                    
                        </div>
                        <div class="short-descri">
                            <p>${booking.description}</p>
                        </div>
                    </div>`;

            });
            document.getElementById("booking_container").innerHTML = element;
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}

function enroll() {
    const id = document.querySelector(".bookinginfo").id;
    const url = `http://localhost:8080/shotmaniacs2/api/admin/booking/${id}/crew/${account_id}/enrol`;

    fetch(url, {
        method: 'PUT',
        headers: {
            'Authorization': `${token}`
        }
    }).catch(error => {
        // Handle any errors
        console.error(error);
    });
}

/*To load at the start of the page*/
performQueryAndUpdateBookings();