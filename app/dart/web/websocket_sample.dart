part of mylib;


class WebSocketSample {

  String _base;
  
  WebSocketSample(this._base) {
    init();
  }
  
  Random rand = new Random(1); 
  
  CanvasElement canvas = query("#canvas");
  
  
  init(){
  CanvasRenderingContext2D context = canvas.getContext("2d");
  context.font = "20px sans-serif";
  var webSocket;
  
  
  ButtonElement connect = query("#connect");
  
  ButtonElement send = query("#send");
  send.disabled=true;
  ButtonElement disconnect = query("#disconnect");
  disconnect.disabled=true;
  
  connect.onClick.listen((e){
    SelectElement rate = query("#rate");
    String ws_url = "ws://${_base}/ws/zozo/" + rate.selectedOptions.first.value;
    webSocket = new WebSocket(ws_url);
    webSocket.onOpen.listen((e){
      connect.disabled=true;
      send.disabled=false;
      disconnect.disabled=false;
      
      webSocket.onMessage.listen((e){
        Map data = parse(e.data); 
        // window.alert(data["message"]);
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.strokeText(data["message"], rand.nextInt(200), 30 + rand.nextInt(200));
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
}
