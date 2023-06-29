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


