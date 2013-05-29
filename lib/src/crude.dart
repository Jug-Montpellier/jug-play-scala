part of mtp_jug;


class Crude {
  
  
  static Crude instance;
  
  String _base;
  
  Crude._internal(this._base);
  
  factory Crude() {
    if(instance == null)
      instance = new Crude._internal(JUGBaseURL);
    return instance;
  }
  
  Future<String> post(String table, Map data){
    Completer completer = new Completer();

    HttpRequest req = new HttpRequest();
    req.open("POST", "${_base}/admin/api/" + table, async:true);
    req.setRequestHeader("Content-type", "application/json");
    
    req.send(stringify(data));
    
    req.onLoadEnd.listen((e){
      if(req.status == 200){
        completer.complete(req.responseText);
      }else{
        completer.completeError("Not 200 resp: ${req.status}");
      }
    });
    
    return completer.future;
    
  }
  
  Future<String> all(String table, Map data){
    Completer completer = new Completer();

    HttpRequest req = new HttpRequest();
    req.open("GET", "${_base}/admin/api/" + table, async:true);
    req.setRequestHeader("Content-type", "application/json");
    
    //req.send(stringify(data));
    
    req.onLoadEnd.listen((e){
      if(req.status == 200){
        completer.complete(req.responseText);
      }else{
        completer.completeError("Not 200 resp: ${req.status}");
      }
    });
    
    return completer.future;
    
  }
  
}

