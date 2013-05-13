import 'dart:html';
import 'dart:async';
import 'dart:json';

import 'package:web_ui/watcher.dart' as watchers;
import 'package:json_object/json_object.dart';


import 'package:mtp_jug/mtp_jug.dart';

String query = '';

List<String> speakers = [];

List<String> get results {
  var lQuery = query.toLowerCase();
  var res = speakers.where((v) => v["fullname"].toLowerCase().contains(lQuery));
//  res = res.map((e)=>new SpeakerImpl.fromMap(e));
  return (res.length <= 20) ? res.toList()
      : (res.take(20).toList()..add({'id' : '0','photourl':'', 'fullname': '... and many more'}));
}

bool get noMatches => results.isEmpty;


main() {
  HttpRequest.request(JUGBaseURL+"/api/speakers").then(speakersLoader);
}


abstract class Speaker {
  num id;
  String fullname;
  String photourl;
}

class SpeakerImpl extends JsonObject implements Speaker {
  factory SpeakerImpl.fromMap(string) {
    return new JsonObject.fromMap(string, new SpeakerImpl());
  }
}

speakersLoader(HttpRequest req){
 speakers = parse(req.responseText);
 watchers.dispatch();
}

