<?xml version="1.0" encoding="utf-8"?>
<svg width="240" height="70"  xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">

<rect x="0" y="5" rx="10" ry="10" width="220" height="50" style="fill:aquamarine;stroke:black;stroke-width:2;opacity:0.1" />

  <g id="character" style="fill-opacity:0.9; stroke-opacity:0.9">
  <image xlink:href="/image/bot/icon/${sender}.jpg" x="185" y="20" height="32px" width="32px"/>
  </g>

  <text x ="30" y ="60" fill="navy" font-size="11">
      from ${sender}
  </text>

  <foreignObject x="5" y="15" width="230" height="40" id="msgtext">
    <body xmlns="http://www.w3.org/1999/xhtml" style="text-align: left;">
        <span style="word-wrap: break-word; font-size:12px">${message}</span>
  	</body>
  </foreignObject>
  <use id="use" xlink:href="#msgtext" />


  Sorry, your browser does not support inline SVG.

</svg>