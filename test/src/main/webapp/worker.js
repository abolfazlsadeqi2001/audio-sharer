var ws = new WebSocket("ws://192.168.42.45:8080/test/main")

onmessage = function(e){
	ws.send(e.data)
}