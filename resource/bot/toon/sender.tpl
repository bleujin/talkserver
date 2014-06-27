<svg id="${node.property(messageId).asString()}" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
<script type="text/ecmascript" xlink:href="/toonweb/js/svg.js" />
<script type="text/ecmascript" xlink:href="/toonweb/js/svg.easing.min.js" />
<script type="text/ecmascript" xlink:href="/toonweb/js/svg.foreignobject.js" />
<script type="text/ecmascript" xlink:href="/toonweb/js/htmlparser.js" />
<script type="text/ecmascript" xlink:href="/toonweb/js/chatbubble.js" />
<script><![CDATA[

function init() {
    var msgId = '${node.property(messageId).asString()}'
    var message = '${message}'
    var sender = '${node.ref(sender).property(nickname).asString()}'
    var receivedTime = '${node.property(time).asDateFmt(HH:mm:ss)}'

    var character = '${character}'
    var emotion = '${emotion}'

    var bubble = MyMessage.create(msgId, message).from(sender).when(receivedTime).toonBubble(character, emotion).draw()
}

init()

]]></script>
</svg>