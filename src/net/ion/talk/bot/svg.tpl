<?xml version="1.0" encoding="utf-8"?>
<svg width="100%" height="100%"  xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
  <rect x="5" y="10" rx="20" ry="20" width="300" height="150" style="fill:yellow;stroke:black;stroke-width:2;opacity:0.1" />
  <path id="path1" d="M130,0 H290 M130,25 H290 M130,50 H290 M130,75 H290"></path>
  
  <text x="0" y="20" transform="translate(0,35)" style="font-size:17px">
  <textPath xlink:href="#path1">
  	Here is a <strong>paragraph</strong> that requires <em>word wrap</em>
  </textPath>
  </text>
  
   <foreignObject id="msgtext" x="10" y="10" width="100" height="150">
    <body xmlns="http://www.w3.org/1999/xhtml">
  	Here is a <strong>paragraph</strong> that requires <em>word wrap</em>
  	<ul>
          <li><strong>First</strong> item</li>
          <li><em>Second</em> item</li>
          <li>Thrid item</li>
        </ul>
  	</body>
  </foreignObject>
  
 
  
  <g transform="translate(100,180) scale(-1,1)">
  <image xlink:href="/image/bimage/toon/char/bat/sad.png" x="0" y="-130" height="90px" width="90px"/>
  </g>
  
    <use id="use" xlink:href="#msgtext" />
  
  Sorry, your browser does not support inline SVG.
</svg>