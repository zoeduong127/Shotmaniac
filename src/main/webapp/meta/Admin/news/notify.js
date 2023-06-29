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



function display(element) {
    document.getElementById(element).style.visibility = "visible";
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