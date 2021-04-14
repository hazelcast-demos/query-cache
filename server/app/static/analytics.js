function send(event) {
    function payload() {
        let json = {};
        json['timestamp'] = event.timeStamp;
        json['event'] = event.type;
        json['instant'] = Date.now();
        json['x'] = event.x;
        json['y'] = event.y;
        if (event.target) {
            const target = event.target;
            json['component'] = target.id;
            let offset = {};
            offset['left'] = target.offsetLeft;
            offset['height'] = target.offsetHeight;
            offset['top'] = target.offsetTop;
            offset['width'] = target.offsetWidth;
            json['offset'] = offset;
            let client = {};
            client['left'] = target.clientLeft;
            client['height'] = target.clientHeight;
            client['top'] = target.clientTop;
            client['width'] = target.clientWidth;
            json['client'] = client;
            let scroll = {};
            scroll['left'] = target.scrollLeft;
            scroll['height'] = target.scrollHeight;
            scroll['top'] = target.scrollTop;
            scroll['width'] = target.scrollWidth;
            json['scroll'] = scroll;
            json['type'] = target.type;
            json['value'] = target.value;
        }
        return json;
    }

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/analytics');
    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
    xhr.send(JSON.stringify(payload()));
}

document.getElementById("first-name").onchange = send;
document.getElementById("last-name").onchange = send;
document.getElementById("range").onchange = send;
document.getElementById("first-button").onclick = send;
document.getElementById("success-button").onclick = send;
