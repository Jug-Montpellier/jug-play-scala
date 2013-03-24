import 'dart:html';
import 'dart:json';
import 'dart:math';


void main() {
 
  String loc = document.window.location.toString();
  int i = loc.indexOf("/assets/sosimple.html");
  String base;
  if(i==-1){
    //Dart Editor ?
    base="127.0.0.1:9000";
    }else{
     base = loc.substring(7, i);
  }
  window.alert(base);
  
  Random rand = new Random(1); 
  
  CanvasElement canvas = query("#canvas");
  
  CanvasRenderingContext2D context = canvas.getContext("2d");
  context.font = "20px sans-serif";
  var webSocket;
  
  query("#sample_text_id")
    ..text = "Click me!"
    ..onClick.listen(reverseText);
  
  ButtonElement connect = query("#connect");
  
  ButtonElement send = query("#send");
  send.disabled=true;
  ButtonElement disconnect = query("#disconnect");
  disconnect.disabled=true;
  
  connect.onClick.listen((e){
    SelectElement rate = query("#rate");
    String ws_url = "ws://${base}/ws/zozo/" + rate.selectedOptions.first.value;
    window.alert(ws_url);
    webSocket = new WebSocket(ws_url);
    webSocket.onOpen.listen((e){
      connect.disabled=true;
      send.disabled=false;
      disconnect.disabled=false;
      
      webSocket.onMessage.listen((e){
        Map data = parse(e.data); 
        // window.alert(data["message"]);
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.strokeText(data["message"], rand.nextInt(300), 30 + rand.nextInt(200));
      });
      
    });
 
    
  });
  
  
  send.onClick.listen((e){
    webSocket.send('{"text":"helo from dart"}');
  });

  
  disconnect.onClick.listen((e){
    webSocket.close();
    connect.disabled=false;
    send.disabled=true;
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




