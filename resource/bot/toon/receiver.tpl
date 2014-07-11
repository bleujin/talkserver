<svg version="1.1" id="svg_${node.property(messageId).asString()}" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" xml:space="preserve" height="70">
    <g id="bubble_${node.property(messageId).asString()}" opacity="1" height="70">
        <image id="char_${node.property(messageId).asString()}" xlink:href="/image/bimage/toon/char/${character}/${emotion}.png" x="0" y="20" height="60" width="60"/>
        <rect id="rect_${node.property(messageId).asString()}" width="250" height="55" x="0" rx="10" ry="10" style="fill: rgb(127, 255, 212); stroke: rgb(0, 0, 0); stroke-width: 2px; opacity: 0.1;"/>
        <foreignObject id="body_${node.property(messageId).asString()}" width="250" height="70" x="0" y="10">
            <body xmlns="http://www.w3.org/1999/xhtml">
                <div id="msg_${node.property(messageId).asString()}" style="width: 230px; font-size:12px; white-space: pre-wrap; white-space: -moz-pre-wrap; white-space: -pre-wrap; white-space: -o-pre-wrap; word-wrap: break-word;">
                ${message}
                </div>
            </body>
        </foreignObject>
        <text>
            <tspan id="sender_${node.property(messageId).asString()}" dy="13" x="0" style="font-size: 10px">${node.property(time).asDateFmt(HH:mm:ss)} from ${node.ref(sender).property(nickname).asString()}</tspan>
        </text>

    </g>
</svg>