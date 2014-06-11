<?xml version="1.0" encoding="utf-8"?>
<svg width="100%" height="100%"  xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
  <rect x="5" y="10" rx="20" ry="20" width="300" height="150" style="fill:yellow;stroke:black;stroke-width:2;opacity:0.1" />
  <path id="path1" d="M130,0 H290 M130,25 H290 M130,50 H290 M130,75 H290"></path>
  
  <text x="0" y="20" transform="translate(0,35)" style="font-size:17px">
  <textPath xlink:href="#path1">
  	Here is a <strong>paragraph</strong> that requires <em>word wrap</em>
  </textPath>
  </text>
  
 
  <g id="character" style="fill-opacity:0.9; stroke-opacity:0.9">
  <image xlink:href="./char/bat/sadness.png" x="0" y="90" height="90px" width="90px" opacity="0.7"/>
  </g>
  
   <foreignObject x="10" y="10" width="150" height="150" id="msgtext">
    <body xmlns="http://www.w3.org/1999/xhtml">
  	Here is a <strong>paragraph</strong> that requires <em>word wrap</em>
  	<ul>
          <li><strong>First</strong> item</li>
          <li><em>Second</em> item</li>
          <li>Thrid item</li>
        </ul>
  	</body>
  </foreignObject>
  <use id="use" xlink:href="#msgtext" />
  
  
  Sorry, your browser does not support inline SVG.
</svg>