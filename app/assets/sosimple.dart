import 'dart:html';

import 'package:asset_pack/asset_pack.dart';
import 'packages/game_loop/game_loop.dart';

void main() {
  
  final String baseUrl = "${window.location.href.substring(0, window.location.href.length - "sosimple.html".length)}";
  
  
  query("#sample_text_id")
    ..text = "Click me!"
    ..onClick.listen(reverseText);
  
  
  CanvasElement canvas = query("#canvas");
  CanvasRenderingContext2D ctx = canvas.getContext("2d");
  
  GameLoop mainGameLoop = new GameLoop(canvas);
  
  var x = 10;
  
  mainGameLoop.onUpdate = ((gameLoop){
    x += 1;
    x = x % 100;
  });
  
  
  mainGameLoop.onRender = ((gameLoop) {
    ctx.beginPath();
    ctx.clearRect(0, 0, 200, 200);
    for(var i = 0; i < 25 ; i+=2){
      ctx.rect(x+2*i, 10+2*i, 100-4*i, 100-4*i);
    }
    ctx.stroke();
  });

  AssetManager assets = new AssetManager();
  
  assets.loaders['image'] = new ImageLoader();
  
  // Load 'explosions' from 'explosions.pack'.
  assets.loadPack('jug', 'pack/jug.pack').then((assetPack){ 
      print(assetPack);
      mainGameLoop.start(); 
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




