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


function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}


function updateBookings(filterType) {

    const bookingWidget = document.createElement("div");
    bookingWidget.setAttribute("class","booking");
    bookingWidget.setAttribute("id", "booking_");

    var filter;

    switch (filterType) {
        case 'in_progress':
            filter = "ongoing";
            break;
        case 'future':
            filter = "ongoing";
            break;
        case 'past':
            filter = "past";
            break;
        default:
            filter = 'ongoing';
    }

    const cookies = parseCookie(document.cookie);
    const id = cookies['account_id'];

    const url = `http://localhost:8080/shotmaniacs2/api/crew/${id}/mybooking?filtertime=${filter}`;
    fetch(url)
        .then(response => response.json())
        .then(data => {
            data.forEach(booking => {
                console.log(booking.name);
            });
        })
        .catch(error => {
            // Handle any errors
            console.error(error);
        });
}