<svg width="240" height="70" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" onload="init()">

<script>
<![CDATA[
var xmlns="http://www.w3.org/2000/svg"

function init(){
    var dialogbox = document.getElementById("msg_${node.property(messageId).asString()}") ;
    dialogbox.setAttribute("height", "40") ;
}

]]>
</script>

   <rect x="0" y="5" rx="10" ry="10" width="230" height="${rectHeight}" style="fill:aquamarine;stroke:black;stroke-width:2;opacity:0.1" id="msg_${node.property(messageId).asString()}"/>

   <foreignObject x="10" y="15" width="230" height="${foreignObjectHeight}" id="msgtext">
    <body xmlns="http://www.w3.org/1999/xhtml" style="text-align: left;">
        <span style="word-wrap: break-word; font-size:12px">${message}</span>
  	</body>
  </foreignObject>
  <use id="use" xlink:href="#msgtext" />

  <text x="10" y="65" fill="navy" font-size="11">
      ${node.property(time).asDateFmt(HH:mm:ss)} from ${node.ref(sender).property(nickname).asString()}
  </text>


  Sorry, your browser does not support inline SVG.
</svg>