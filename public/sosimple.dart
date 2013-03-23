import 'dart:html';
import 'dart:json';


void main() {
  
  CanvasElement canvas = query("#canvas");
  
  CanvasRenderingContext2D context = canvas.getContext("2d");
  context.font = "20px sans-serif";
  var webSocket;
  
  query("#sample_text_id")
    ..text = "Click me!"
    ..onClick.listen(reverseText);
  
  ButtonElement connect = query("#connect");
  
  ButtonElement ping = query("#ping");
  ping.disabled=true;
  ButtonElement disconnect = query("#disconnect");
  disconnect.disabled=true;
  
  connect.onClick.listen((e){
    webSocket = new WebSocket('ws://127.0.0.1:9000/ws/zozo');
    webSocket.onOpen.listen((e){
      connect.disabled=true;
      ping.disabled=false;
      disconnect.disabled=false;
      
      webSocket.onMessage.listen((e){
        Map data = parse(e.data); 
        // window.alert(data["message"]);
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.strokeText(data["message"], 100, 100);
      });
      
    });
 
    
  });
  
  
  ping.onClick.listen((e){
    webSocket.send('{"text":"helo from dart"}');
  });

  
  disconnect.onClick.listen((e){
    webSocket.close();
    connect.disabled=false;
    ping.disabled=true;
    disconnect.disabled=true;
    
  });
  
  
}

void reverseText(MouseEvent event) {
  var text = query("#sample_text_id").text;
  var buffer = new StringBuffer();
  for (int i = text.length - 1; i >= 0; i--) {
    buffer.write(text[i]);
  }
  query("#sample_text_id").text = buffer.toString();
  
  
  
  
}




