<?xml version="1.0" encoding="utf-8"?>
<svg width="400" height="180"  xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
  <rect x="0" y="10" rx="20" ry="20" width="220" height="120" style="fill:yellow;stroke:black;stroke-width:2;opacity:0.1" />
  <path id="path1" d="M25,0 H200 M25,20 H200 M85,40 H200 M85,60 H200 M85,80 H200"></path>
  
  <text x="0" y="20" transform="translate(0,35)" style="font-size:12px">
  <textPath xlink:href="#path1">${message}</textPath>
  </text>
  <g transform="translate(90,185) scale(-1,1)">
  <image xlink:href="/image/bimage/toon/char/${charId}/${emotion}.png" x="0" y="-130" height="90px" width="90px"/>
  </g>
  
  <text x ="80" y ="138" fill="navy" font-size="11">
        ${node.property(time).asDateFmt(HH:mm:ss)} from ${node.ref(sender).property(nickname).asString()}  
  </text>
  
  Sorry, your browser does not support inline SVG.
</svg>