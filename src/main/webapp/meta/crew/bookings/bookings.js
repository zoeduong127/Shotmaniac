const booking = document.getElementById("booking_container");
const popup = document.getElementById("hiddenpop");
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

function openInfo(element) {
    booking.style.opacity = "0.2";
    popup.style.visibility = "visible";

    const cookies = parseCookie(document.cookie);
    const id = cookies['account_id'];

    const url = `http://localhost:8080/shotmaniacs2/api/crew/${id}/booking/${element.id}`
    fetch(url)
        .then(response => response.json())
        .then(booking => {
            let element = "";
            /*TODO: is it best to compare to the full database or try something else*/
            console.log(element.children[0]);
            console.log(element.children[1]);


            let info = "";
            info += `
                    <img class="icon" src="">
                    <p>${days[new Date(booking.date).getDay()]}, 
                       ${new Date(booking.date).getDate()} 
                       ${months[new Date(booking.date).getMonth()]} 
                       ${new Date(booking.date).getFullYear()}
                       ${new Date(booking.date).getTime()}
                    </p>
                    <img class="icon" src="">
                    <p><strong>Available slots: </strong>${booking.slot}</p> <!-- TODO: subtract number of crew in the event -->
                    <img class="icon" src="">
                    <p>${booking.description}</p>
                    `;

            document.getElementById("top-tab").innerHTML = `<h4 id = "heading"> ${booking.name}</h4>
                <span id="back">
                        <img src="./../../Brand_Identity/images/icons/X-icon.png" alt="close" id="back-img" onclick="closeInfo()">
                </span>`;

            document.getElementById("popup-main").innerHTML = info;
        })
}


function closeInfo() {
    booking.style.opacity = "1";
    popup.style.visibility = "hidden";
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

    const cookies = parseCookie(document.cookie);
    const id = cookies['account_id'];

    const url = `http://localhost:8080/shotmaniacs2/api/crew/${id}/allbookings`;

    fetch(url)
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



/*To load at the start of the page*/
performQueryAndUpdateBookings();