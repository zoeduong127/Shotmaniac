let openHref = document.getElementById('openInfo');
let closeBtn = document.getElementById('closeBtn');
let popUpWindow = document.getElementById('hiddenpop');

openHref.addEventListener('click', function(){
    popUpWindow.style.display = 'block';
});

closeBtn.addEventListener('click', function(){
    popUpWindow.style.display = 'none';
});