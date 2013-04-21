part of mylib;


class HttpSample {
  
  String _base;
  
  HttpSample(this._base);
  
  Future<String> post(){
    Completer completer = new Completer();
    
    HttpRequest req = new HttpRequest();
    req.open("POST", "http://${_base}/admin/tests", async:true);
    req.setRequestHeader("Content-type", "application/json");
    req.send('{"name": "naine"}');
    
    req.onLoadEnd.listen((e){
      if(req.status == 200)
        completer.complete(req.responseText);
      else
        completer.completeError("Not 200 resp");
    });
    
    return completer.future;
    
  }
  
}

