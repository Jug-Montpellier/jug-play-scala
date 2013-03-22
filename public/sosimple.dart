import 'dart:html';
import 'dart:json';


void main() {
  
  CanvasElement canvas = query("#canvas");
  
  CanvasRenderingContext2D context = canvas.getContext("2d");
  context.font = "20px sans-serif";
  var webSocket = new WebSocket('ws://127.0.0.1:9000/ws/zozo');
  webSocket.onOpen.listen((e){
   // window.alert("connected");
    webSocket.onMessage.listen((e){
      Map data = parse(e.data); 
     // window.alert(data["message"]);
      context.clearRect(0, 0, canvas.width, canvas.height);
      context.strokeText(data["message"], 100, 100);
    });
    
    webSocket.send('{"text":"helo from dart"}');
  });
  
  
  query("#sample_text_id")
    ..text = "Click me!"
    ..onClick.listen(reverseText);
  
}

void reverseText(MouseEvent event) {
  var text = query("#sample_text_id").text;
  var buffer = new StringBuffer();
  for (int i = text.length - 1; i >= 0; i--) {
    buffer.write(text[i]);
  }
  query("#sample_text_id").text = buffer.toString();
  
  
  
  
}




