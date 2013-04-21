import 'dart:html';
import 'dart:async';
import 'dart:json';

import 'package:web_ui/watcher.dart' as watchers;

String query = '';

List<String> speakers = [];

List<String> get results {
  var lQuery = query.toLowerCase();
  var res = speakers.where((v) => v["fullname"].toLowerCase().contains(lQuery));
  return (res.length <= 20) ? res.toList()
      : (res.take(20).toList()..add({'id' : '', 'fullname': '... and many more'}));
}

bool get noMatches => results.isEmpty;

main() {
  
  
  HttpRequest.request(getServerBaseURL()+"/api/speakers").then(speakersLoader);
  
  
}

String getServerBaseURL() {
  String loc = document.window.location.toString();
  int i = loc.indexOf("/", 7);
  String base = loc.substring(0, i);
  if(base.indexOf("3030") != -1)
      base = "http://127.0.0.1:9000";
  return base;
}

speakersLoader(HttpRequest req){
 speakers = parse(req.responseText);
 watchers.dispatch();
}

