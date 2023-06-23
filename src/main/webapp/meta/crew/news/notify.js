const announcementListItems = document.querySelectorAll(".announcement-list-item");
const announcementContent = document.querySelector(".announcement-content");
const announcementTitle = document.querySelector("#announcement-title");
const announcementDescription = document.querySelector("#announcement-description");
const announcementList = document.querySelector('.announcement-list');

//clicking on an announcement makes it show the content
announcementListItems.forEach(function(item) {
    item.addEventListener("click", function() {
        const title = this.textContent.trim();
        const content = "Content for " + title;

        announcementTitle.textContent = title;
        announcementDescription.textContent = content;
        announcementContent.style.display = "block";
    });
});

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

const addCommentButton = document.getElementById("add-comment");
const commentBox = document.getElementById("comment-box");
const commentInput = document.getElementById("comment-input");
const commentSubmitButton = document.getElementById("comment-submit");

addCommentButton.addEventListener("click", function() {
    commentBox.style.display = "block";
});

commentSubmitButton.addEventListener("click", function() {
    const comment = commentInput.value.trim();
    if (comment !== "") {
        // Logic to handle the submitted comment
        console.log("Submitted comment:", comment);
        commentInput.value = "";
        commentBox.style.display = "none";
    }
});

