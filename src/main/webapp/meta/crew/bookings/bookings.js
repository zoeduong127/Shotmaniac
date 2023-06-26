const booking = document.getElementById("booking_container");
const popup = document.getElementById("hiddenpop");


function openInfo(element) {
    booking.style.opacity = "0.2";
    popup.style.visibility = "visible";


    /*TODO: is it best to compare to the full database or try something else*/
    console.log(element.children[0]);
    console.log(element.children[1]);
    let info = "";
    info += `
            <img class="icon" src="">
            <p>Still Hard Coded</p>
            <img class="icon" src="">
            <p>Still hard Coded</p>
            <img class="icon" src="">
            <p>${element.children[1].innerText}</p>
            `;

    document.getElementById("top-tab").innerHTML = `<h4 id = "heading"> ${element.children[0].innerText} </h4>
        <span id="back">
                <img src="./../../Brand_Identity/images/icons/X-icon.png" alt="close" id="back-img" onclick="closeInfo()">
        </span>`;

    document.getElementById("popup-main").innerHTML = info;
}

function closeInfo() {
    booking.style.opacity = "1";
    popup.style.visibility = "hidden";
}