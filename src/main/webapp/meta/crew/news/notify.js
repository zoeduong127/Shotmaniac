/*
const announcementListItems = document.querySelectorAll(".announcement-list-item");
const announcementContent = document.querySelector(".announcement-content");
const announcementTitle = document.querySelector("#announcement-title");
const announcementDescription = document.querySelector("#announcement-description");
const announcementList = document.querySelector('.announcement-list');
const pre_announcement = document.getElementById('pre-announcement');
const addCommentButton = document.getElementById("add-comment");
const commentBox = document.getElementById("comment-box");
const commentInput = document.getElementById("comment-input");
const commentSubmitButton = document.getElementById("comment-submit");

const cookies = parseCookie(document.cookie);
const token = cookies['auth_token'];
const account_id = cookies['account_id'];

function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}





/!*announcementContent.addEventListener('click', function() {
    announcementTitle.textContent = '';
    announcementDescription.textContent = '';
});*!/


function showComment() {
    const comment = document.getElementById("add-comment");
    if (comment.innerHTML === "Add a comment") {
        comment.innerHTML = "Close comment box";
        commentBox.style.visibility = "visible";
    } else {
        comment.innerHTML = "Add a comment";
        commentBox.style.visibility = "hidden";
    }
}




function readAllAnnouncements() {
    const url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/news`;

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let container = "";
            let description = "";
            let check = false;

            data.forEach(a => {
                console.log([a]);
                container +=
                    `<div class="announcement-list-item" id="A_${a.announcement_id}" onclick="display(a.announcement_id)">
                        ${a.title}
                    </div>
                    `;

                if (check) {
                    description += `
                    <h3 class="announcement-title current_display" id="${a.announcement_id}">${a.title}</h3>
                   
                    <div id="announcement-description">${a.body}
                    
                    <div class="add-comment" id="add-comment" onclick="showComment()">Add a comment</div>
                    
                    `;

                } else {
                    description += `
                    <h3 class="announcement-title " id="${a.announcement_id}">${a.title}</h3>
                   
                    <div id="announcement-description">${a.body}
                    
                    <div class="add-comment" id="add-comment" onclick="showComment()">Add a comment</div>
                    
                `;

                    /!*<div class="comment-box" id="comment-box">
                        <label for="comment-input"></label>
                        <textarea id="comment-input" placeholder="Type your comment here"></textarea>
                        <button id="comment-submit" type="submit">Submit</button>
                    </div>*!/
                    let submit = document.createElement("div")
                    let label = document.createElement("label");
                    let textarea = document.createElement("textarea");
                    let button = document.createElement("button");

                    submit.className = "comment-box";
                    submit.id = "comment-box";

                    label.setAttribute("for", "comment-input");

                    textarea.id = "comment-input";
                    textarea.placeholder = "Type your comment here";

                    button.id = "comment-submit";
                    button.type = "submit";
                    button.innerText = "Submit";
                    button.addEventListener("click", closeComment);

                    submit.appendChild(label)
                    submit.appendChild(textarea)
                    submit.appendChild(button);
                    description += (submit.innerHTML);
                }
            })
            console.log(container + ", " + description)
            document.getElementById("announcement-wrapper").innerHTML = container;
            document.getElementById("description").innerHTML = description;
        })
}


function closeComment() {
    const comment = commentInput.value.trim();
    if (comment !== "") {
        // Logic to handle the submitted comment
        console.log("Submitted comment:", comment);
        commentInput.value = "";

        document.getElementById("add-comment").innerHTML = "Add a comment";
        commentBox.style.visibility = "hidden";
    }
}
function display(id) {
    document.getElementById(id).style.visibility = "visible";
    document.querySelector(".current_display").style.visibility = "hidden";
    document.getElementById(id).classList.add("current_display");
}

readAllAnnouncements();

*/


/*FROM ADMIN*/

let commentBox;
let commentInput;
let submitbutton;

const cookies = parseCookie(document.cookie);
const token = cookies['auth_token'];
const account_id = cookies['account_id'];

function parseCookie(cookieString) {
    const cookies = {};
    cookieString.split(';').forEach(cookie => {
        const [name, value] = cookie.trim().split('=');
        cookies[name] = decodeURIComponent(value);
    });
    return cookies;
}

function readAllAnnouncements(input) {
    let url;

    if (input === " ") {
        url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/news`;
    } else {
        url = input;
    }

    console.log(url);
    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            let container = "";
            let description = "";
            let check = true;

            data.forEach(a => {
                container += `<div class="announcement-list-item" id="A_${a.id}" onclick="display(${a.id})">${a.title}</div>`;

                if (check) {
                    description += `
							<div class="description-wrapper current_display" id="${a.id}">
			                    <h3 class="announcement-title">${a.title}</h3>
			
			                    <div class="announcement-description" id="announcement-description">${a.body}</div>
		                    </div>
		                    `;

                    check = false;
                } else {
                    description += `
							<div class="description-wrapper" id="${a.id}">
			                    <h3 class="announcement-title">${a.title}</h3>
			
			                    <div id="announcement-description">${a.body}</div>
			
		                    </div>
                            `;
                }
            })

            console.log(container + ", " + description)
            document.getElementById("announcement-wrapper").innerHTML = container;
            document.getElementById("description").innerHTML = description;
        })
}


function display(id) {
    /*document.getElementById("add-comment").innerHTML = "Add a comment";
    commentBox.style.visibility = "hidden";
    commentBox.style.display = "none";*/
    document.querySelector(".current_display").className = "description-wrapper";
    document.getElementById(id).classList.add("current_display");
    const url = window.location.origin+`/shotmaniacs2/api/crew/${account_id}/news/${id}/mark_read`;
    const options = {
        method: 'PUT',
        headers: {
            'Authorization': `${token}`
        }
    }
    fetch(url,options)
        .then(response=>console.log(response))

    /*
            changeState(id);
    */
}



/*Filter*/

function filterAnnouncement(state) {
    let url;
    //TODO possibly have to switch this to 0's and 1's, check DB for which is which.
    if (Number(state) === 0) {
        url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/news/filter?status=${state}`;
        state = "UNREAD";
    } else if (Number(state) === 1){
        url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/news/filter?status=${state}`;
        state = "READ";
    } else {
        state = "ALL";
        url = " ";
    }
    document.getElementById("filter-text").innerText = state;

    readAllAnnouncements(url)
}

/*SEARCH */

let allBookings = [];
let inputElement = document.getElementById("search");

inputElement.addEventListener("input", onInputChange);
function getAllAnnouncements() {
    const url = window.location.origin + `/shotmaniacs2/api/crew/${account_id}/news`

    fetch(url, {
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            allBookings = data.map((announcement) => {
                return announcement.title;
            })
        })
}

function  onInputChange() {
    removeAutocompleteDropdown();

    const value = inputElement.value.toLowerCase();

    if (value.length === 0) return;

    const filteredNames = [];


    allBookings.forEach((announcement) => {
        if (announcement.substring(0, value.length).toLowerCase() === value) {
            filteredNames.push(announcement);
        }
    })
    createAutocompleteDropdown(filteredNames);
}

function createAutocompleteDropdown(list) {
    const listEl = document.createElement("ul");
    listEl.className = "autocomplete-list";
    listEl.id =  "autocomplete-list";
    list.forEach((booking) => {
        const listItem = document.createElement("li");

        const bookingButton = document.createElement("button");
        bookingButton.innerHTML = booking;
        bookingButton.addEventListener("click", onBookingButtonClick)
        listItem.appendChild(bookingButton);

        listEl.appendChild(listItem);
    })
    document.querySelector("#search_ALL").appendChild(listEl);
}

function removeAutocompleteDropdown() {
    const listEl = document.querySelector("#autocomplete-list");
    if (listEl) listEl.remove(); //checks if it exists and then removes it
}

function onBookingButtonClick(event) {
    event.preventDefault(); //cancels default event (submitting the form)


    document.querySelector("#search-icon-btn").addEventListener("click", sendInput);

    const buttonEl = event.target; //element that triggered the event (the button itself)
    inputElement.value = buttonEl.innerHTML; //should be a string of the booking name

    removeAutocompleteDropdown();

}


function sendInput(event) {
    console.log("input called")
    event.preventDefault();

    if (inputElement.value.length === 0) readAllAnnouncements(" ");
    else {
        const url = window.location.origin + `/shotmaniacs2/api/admin/announcements/search?searchText="${inputElement.value}"`
        readAllAnnouncements(url);
    }
}


function logout(){
    const token = cookies['auth_token'];
    const url = window.location.origin+`/shotmaniacs2/api/login`
    fetch(url, {
        method: 'DELETE',
        headers: {
            'Authorization': `${token}`
        }
    })
        .then(response => {
            if (response.ok) {
                document.cookie = "account_id =;  Path=/";
                document.cookie = "account_username =;  Path=/";
                document.cookie = "auth_token =;  Path=/";
                window.location.href="http://localhost:8080/shotmaniacs2/";
            } else if (response.status === 304) {
                throw new Error('The given account was not logged in.');
            } else {
                throw new Error('Failed to log out. Status: ' + response.status);
            }
        })
        .then(data => {
            console.log(data); // Logged out successfully
        })
        .catch(error => {
            console.error(error);
        });
}


