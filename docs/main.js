function jsonCallback(json) {
    console.log(json);
}

$.ajax({
    url: "http://52.207.210.73/api/discord/gametracker/users/110926688455495680.json",
    dataType: "json"
});
