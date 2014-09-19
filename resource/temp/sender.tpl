<?xml version="1.0" encoding="utf-8"?>
<svg width="100%" height="100%"  xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
  <rect x="5" y="10" rx="20" ry="20" width="240" height="${rectHeight}" style="fill:aquamarine;stroke:black;stroke-width:2;opacity:0.1" />

  <g id="character" style="fill-opacity:0.9; stroke-opacity:0.9">
  <image xlink:href="/bot/bimage/toon/char/${charId}/${emotion}.png" x="150" y="${characterY}" height="90px" width="90px"/>
  </g>

   <foreignObject x="15" y="25" width="230" height="${foreignObjectHeight}" id="msgtext">
    <body xmlns="http://www.w3.org/1999/xhtml" style="text-align: left;">
        <span style="word-wrap: break-word;">${message}</span>
  	</body>
  </foreignObject>
  <use id="use" xlink:href="#msgtext" />

  <text x ="30" y ="${fromWhoY}" fill="navy" font-size="11">
      ${node.property(time).asDateFmt(HH:mm:ss)} from ${node.ref(sender).property(nickname).asString()}
  </text>


  Sorry, your browser does not support inline SVG.
</svg>