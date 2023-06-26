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

//clicking on an announcement makes it show the content
/*
announcementListItems.forEach(function(item) {
    item.addEventListener("click", function() {
        const title = this.textContent.trim();
        const content = "Content for " + title;

        announcementTitle.textContent = title;
        announcementDescription.textContent = content;
        announcementContent.style.display = "block";
        addCommentButton.style.visibility = "visible";
        preannouncement.style.visibility = "hidden"
    });
});
*/

function display(element) {
    announcementTitle.textContent = element.innerText;
    announcementDescription.textContent = "";
    announcementContent.style.display = "block";
    addCommentButton.style.visibility = "visible";
    pre_announcement.style.visibility = "hidden";
}
/*announcementContent.addEventListener('click', function() {
    announcementTitle.textContent = '';
    announcementDescription.textContent = '';
});*/


const filterOptions = document.querySelectorAll('.filter-option');
const filterButton = document.querySelector('.filter-button');

filterButton.addEventListener('click', function() {
    announcementList.classList.toggle('announcement-list-filtered');
});



filterOptions.forEach(function(option) {
    option.addEventListener('click', function(e) {
        e.preventDefault();
        const selectedOption = this.textContent;
        filterButton.textContent = selectedOption;
        // Perform filtering logic based on the selected option
        // You can add your code here to filter the announcements based on the selected option
    });
});
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


commentSubmitButton.addEventListener("click", function() {
    const comment = commentInput.value.trim();
    if (comment !== "") {
        // Logic to handle the submitted comment
        console.log("Submitted comment:", comment);
        commentInput.value = "";
        const comment_info = document.getElementById("add-comment").innerHTML = "Add a comment";
        commentBox.style.visibility = "hidden";
    }
});