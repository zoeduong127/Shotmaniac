const announcementListItems = document.querySelectorAll(".announcement-list-item");
const announcementContent = document.querySelector(".announcement-content");
const announcementTitle = document.querySelector("#announcement-title");
const announcementDescription = document.querySelector("#announcement-description");
//clicking on an announcement makes it show the content
announcementListItems.forEach(function(item) {
    item.addEventListener("click", function() {
        const title = this.textContent.trim();
        const content = "Content for " + title;

        announcementTitle.textContent = title;
        announcementDescription.textContent = content;
    });
});

announcementContent.addEventListener('click', function() {
    announcementTitle.textContent = '';
    announcementDescription.textContent = '';
});


const filterOptions = document.querySelectorAll('.filter-option');
const filterButton = document.querySelector('.filter-button');

filterOptions.forEach(function(option) {
    option.addEventListener('click', function(e) {
        e.preventDefault();
        const selectedOption = this.textContent;
        filterButton.textContent = selectedOption;
        // Perform filtering logic based on the selected option
        // You can add your code here to filter the announcements based on the selected option
    });
});
