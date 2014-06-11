<?xml version="1.0" encoding="utf-8"?>
<svg width="320" height="155"  xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
  <rect x="75" y="10" rx="20" ry="20" width="220" height="120" style="fill:green;stroke:black;stroke-width:2;opacity:0.1" />
  <path id="path1" d="M90,0 H280 M90,20 H280 M90,40 H250 M90,60 H220 M90,80 H200 M90,100 H200"></path>
  
  <g>
  <image xlink:href="/image/bimage/toon/char/${charId}/${emotion}.png" x="200" y="60" height="90px" width="90px"/>
  </g>
  
  <text x="0" y="20" transform="translate(0,35)" style="font-size:12px">
  <textPath xlink:href="#path1">${message}</textPath>
  </text>

  <text x ="85" y ="138" fill="navy" font-size="11">
      ${node.property(time).asDateFmt(HH:mm:ss)} from ${node.ref(sender).property(nickname).asString()}  
  </text>
  
  Sorry, your browser does not support inline SVG.
</svg>