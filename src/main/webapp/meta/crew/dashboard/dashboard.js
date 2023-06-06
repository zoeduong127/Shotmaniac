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