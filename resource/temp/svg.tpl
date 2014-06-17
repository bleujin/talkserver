<?xml version="1.0" encoding="utf-8"?>
<svg width="100%" height="100%"  xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
  <rect x="5" y="10" rx="20" ry="20" width="300" height="200" style="fill:yellow;stroke:black;stroke-width:2;opacity:0.1" />

  <g id="character" style="fill-opacity:0.9; stroke-opacity:0.9">
  <image xlink:href="./char/bat/sadness.png" x="0" y="90" height="90px" width="90px" opacity="0.7"/>
  </g>
  
   <foreignObject x="20" y="20" width="270" height="200" id="msgtext">
    <body xmlns="http://www.w3.org/1999/xhtml">
  	<!--
  	Here is a <strong>paragraph</strong> that requires <em>word wrap</em>
  	    <ul>
          <li><strong>First</strong> item</li>
          <li><em>Second</em> item</li>
          <li>Thrid item</li>
        </ul>
    -->

    <span style="word-wrap: break-word;">Very long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long long text</span>

    <!--
    <span style="word-wrap: break-word;">Very Loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong text</span>
    -->
  	</body>
  </foreignObject>
  <use id="use" xlink:href="#msgtext" />
  
  
  Sorry, your browser does not support inline SVG.
</svg>