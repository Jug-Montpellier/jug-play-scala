library mtp_jug;


import 'dart:math';
import 'dart:html';
import 'dart:json';
import 'dart:async';


part 'src/crude.dart';
part 'src/websocket_sample.dart';


String JUGBaseURL = _getServerBaseURL();


String _getServerBaseURL() {
   String loc = document.window.location.toString();
   int i = loc.indexOf("/", 10);
   String base = loc.substring(0, i);
   if(base.indexOf("3030") != -1)
      base = "http://127.0.0.1:9000";
   return base;
 }


abstract class Table {
  Object getValue(String name);
  
  void setValue(String name, Object value);
}


abstract class Database {
   void tableSelected(Table table);  
}

