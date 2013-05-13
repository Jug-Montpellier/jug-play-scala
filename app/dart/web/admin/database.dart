import 'dart:html';
import 'dart:async';
import 'dart:json';

import 'package:web_ui/watcher.dart' as watchers;
import 'package:json_object/json_object.dart';


import 'package:mtp_jug/mtp_jug.dart';

String query = '';

List<Map> tables = [];

List<String> get results {
  var lQuery = query.toLowerCase();
  var res = tables.where((v) => v["name"].toLowerCase().contains(lQuery));
  return res.toList();

}

bool get noMatches => results.isEmpty;


main() {
  HttpRequest.request(JUGBaseURL+"/admin/tables").then(tablesLoader);
}



tablesLoader(HttpRequest req){
 tables = parse(req.responseText);
 watchers.dispatch();
}

