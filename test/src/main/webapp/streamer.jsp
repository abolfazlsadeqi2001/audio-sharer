<%@page import="configurations.sound.streaming.DefaultStreamingValues"%>
<html>
<head>
<title>Server</title>
<meta charset="utf-8" />
<script type="text/javascript">
	var host = <%
	String host = request.getLocalAddr();
	if(host.equals("127.0.0.1")){
		host = "localhost";
	}
	
	out.print("'");
	out.print(host);
	out.print("'");
	%>;
	var port = <% out.print(request.getLocalPort()); %>;
	var url = "wss://"+host+":"+port+"/test/main";
	var ws = new WebSocket(url);// to connect to database
	var blobTimeDuration = <% out.print(DefaultStreamingValues.getDelay()); %>;// #depend on client.html
	var recorder;// to recrod the stream
	ws.onopen = function (){
		console.log("open")
	};
	ws.onclose = function(){
		console.log("close");
	};
	// start method load on startups
	function start() {
		// prepare media
		navigator.getUserMedia = navigator.getUserMedia
				|| navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
		// reading media
		navigator.getUserMedia({video:false,audio:true},read,error);
	}
	// stream handlers (read event)
	function read(stream) {
		// read from another source
		recorder = new MediaRecorder(stream,{mimeType: <%
			out.print("'");
			out.print(DefaultStreamingValues.getMimeType());
			out.print("'");
			%>});
		recorder.ondataavailable = e => {
			send(e.data);
		};
		recorder.start(blobTimeDuration*1000);// so long but prevent delay on server because of resources and short time to send and record
	}
	// stream handlers (error event)
	function error(e) {
		alert(e);
	}
	// send to other sockets (async (prevent stoping the recording))
	async function send(blob){
		ws.send(blob);
	}
	// on closestreambutton click
	function onCloseConnection(){
		recorder.stop();
		ws.close();
	}
</script>
</head>
<body onload="start()">
	<!-- title -->
	<h1>server</h1>
	<!-- to close the stream -->
	<button onclick="onCloseConnection();">close stream</button>
</body>
</html>
