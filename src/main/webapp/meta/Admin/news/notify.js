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





/*announcementContent.addEventListener('click', function() {
    announcementTitle.textContent = '';
    announcementDescription.textContent = '';
});*/


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

                    /*<div class="comment-box" id="comment-box">
                        <label for="comment-input"></label>
                        <textarea id="comment-input" placeholder="Type your comment here"></textarea>
                        <button id="comment-submit" type="submit">Submit</button>
                    </div>*/
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
    document.getElementById("add-comment").innerHTML = "Add a comment";
    commentBox.style.visibility = "hidden";
    document.getElementById(id).style.visibility = "visible";
    document.querySelector(".current_display").style.visibility = "hidden";
    document.getElementById(id).classList.add("current_display");
}

readAllAnnouncements();