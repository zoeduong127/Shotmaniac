const text = document.getElementById('password');

document.getElementById('Show-Pass').addEventListener("click", () => {
    document.getElementById('Show-Pass').hidden = true;
    document.getElementById('Hide-Pass').hidden = false;
    text.type = "text";
})

document.getElementById('Hide-Pass').addEventListener("click", () => {
    document.getElementById('Hide-Pass').hidden = true;
    document.getElementById('Show-Pass').hidden = false;
    text.type = "password";
})




/*Form JS*/


const form = document.getElementById("signup-form");

form.addEventListener('submit', event);

function event() {
    event.preventDefault() // prevents auto-submission

    const url = "";

    const  form = document.getElementById("signup-form");
    const elements = form.elements;
    let information = {};


    for (let i = 0; i < elements.length - 1; i++) {
        let element = elements[i];
        let id = element.id;
        let value;

        if (element.type !== "button") {
            value = element.value;

            information[id] = value;
        }
    }

    const jsonBooking = JSON.stringify(information);
    console.log(jsonBooking);

    const options = {
        method: "POST",
        headers: {
            "Content-Type" : "application/json",
        },
        body: jsonBooking
    };


    fetch(url, options)
        .then(response => {
            console.log(response)

            if (response.ok) {
                alert("sign-up successful!");
                document.getElementById("signup-form").reset();
            } else {
                alert("Sign-up unsuccessful");
            }
        })

        .catch(error => {
            console.error("Server error: ", error);
            alert("An error occurred while processing your information. " +
                "Try again Later!")
        })
}