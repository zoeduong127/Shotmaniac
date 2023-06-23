const date = new Date();

const renderCalendar = () =>
{
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
        = `${months[date.getMonth()]}`/*, ${date.getFullYear()}`*/;

    document.querySelector(".date p").innerHTML
        = new Date().toDateString();


    let days = "";
    for (let offset = fistDayIndex; offset > 0; offset--) { //days of previous month
        days += `<div class="prev-date">${prevLastDay - offset + 1} </div>`
    }

    for (let i = 1; i <= lastDay; i++) { //days of the month
        if (i === new Date().getDate() && date.getMonth() === new Date().getMonth()) {
            days += `<div class="today">${i}</div>`;
        } else {
            days += `<div>${i}</div>`;
        }
    }

    for (let offset = 1; offset <= nextDays; offset++) { //days of next month
        days += `<div class="next-date">${offset}</div>`
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

renderCalendar();