<%@page import="configurations.sound.streaming.StreamingValues"%>
<html>
<head>
<title>client</title>
<meta charset="utf-8" />
<!-- memory performance also be up to date -->
<meta http-equiv="refresh" content="300">
<script type="text/javascript">
	// general variables
	var ws;
	var isSetCurrentTime = false;
	var currentTime = 0;
	var blobTimeDuration = <%out.print(StreamingValues.getDelay());%>;
	var array = [];
	var audio;
	//start method is called on startup
	function start() {
		// value the general variables
		var host = <%String host = request.getLocalAddr();
		if(host.equals("127.0.0.1") || host.equals("0:0:0:0:0:0:0:1")){
			host = "localhost";
		}
		
		out.print("'");
		out.print(host);
		out.print("'");%>;
		var port = <%out.print(request.getLocalPort());%>;
		var url = "wss://"+host+':'+port+"/test/client";
		ws = new WebSocket(url);
		audio = document.querySelector("audio");
		// ===================>handle websocket events
		ws.onopen = function() {// log open when open connection
			console.log("open")
		};

		ws.onmessage = function(e) {// set audio time or play the blob
			if (typeof e.data === "string") {// if it is a string set audio time otherwise play the e.data(blob) 
				currentTime = Number(e.data) * blobTimeDuration;// number-of-received-blobs * every-blob-length
				isSetCurrentTime = true;// prevent set default value for audio cursor
			} else {
				play(e.data);
			}
		};

		ws.onclose = function() {// log close when close connection
			console.log("closed");
		};
	}
	// general functions
	async function play(e) {// play the blob
		// remove all indexes except for first one (which include headers very necessary for read)
		while (array.length > 1) {
			array.pop();
		}
		// push the new blob on it
		array.push(e)
		// create a blob of our array
		var blob = new Blob(array, {
			mimeType : <%out.print("'");
			out.print(StreamingValues.getMimeType());
			out.print("'");%>
		});
		// create a url from our blob
		var url = URL.createObjectURL(blob);
		// if previously audio time was not declarared use the current time of audio otherwise use the declrared value 
		if (!isSetCurrentTime) {
			currentTime = audio.currentTime;
		} else {
			isSetCurrentTime = false;
		}
		// set the src
		audio.src = url;
	}
	// load when the new music (blob) has been downloaded and metadas set correctly
	function loadNewMusic() {
		if (audio.duration > currentTime) {// if auido.length > value-to-set set it otherwise set 0 as default
			audio.currentTime = currentTime;
		} else {
			audio.currentTime = 0;
			audio.pause();
		}
		if (audio.paused) {// if the audio player is paused play it
			audio.play();
		}
	}
	// load when the audio finish (stream is closed or connection so slow to get new blobs)
	function finishAudio() {
		currentTime = audio.duration;// when the audio finished the cursor is going to place 0 but if the finish audio happened because of slow connection and after that we get a blob and our audio length is changed we don't now to start from where and we must play form 0
		isSetCurrentTime = true;// prevent set default value for audio cursor
	}
</script>
</head>
<body onload="start()">
	<!-- title -->
	<h1>client</h1>
	<!-- audio player -->
	<audio onended="finishAudio()" onloadedmetadata="loadNewMusic()"></audio>
</body>
</html>