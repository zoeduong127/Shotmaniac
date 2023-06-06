let currentThere = document.getElementById("currentCSS");
currentThere.setAttribute('href', 'crewDashboardStyles.css');
function toggleStyle() {
    let theme = document.getElementsByTagName('link');

    for (let i = 0; i < theme.length; i++) {
        if (theme[i].rel === 'stylesheet') {

            if (theme[i].getAttribute("href") === 'crewDashboardStyles.css') {
                theme[i].setAttribute('href', 'singleEventHomePage.css');
            } else {
                theme[i].setAttribute('href', 'crewDashboardStyles.css');
            }

        }
    }
    window.alert('css changed')
}