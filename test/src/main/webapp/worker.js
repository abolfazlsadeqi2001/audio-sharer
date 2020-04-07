var ws;
onmessage = function(e){
	if (ws == undefined){
		ws = new WebSocket('ws://localhost:8080/test/main');
	}
	ws.send(e.data[0]);
}