
var ChatBubble = function(config) {

    var windowSize = function () {
        var w = window,
            d = document,
            e = d.documentElement,
            g = d.body || d.getElementsByTagName('body')[0],
            _width = w.innerWidth || e.clientWidth || g.clientWidth,
            _height = w.innerHeight|| e.clientHeight|| g.clientHeight

        return {
            width: _width,
            height:_height
        }
    }

    var htmlToFragment = function(html)
    {
        var retval = document.createDocumentFragment();
        var currentParent = retval;

        HTMLParser(html,
            {
                start : function(tag, attrs, unary)
                {
                    var node = document.createElement(tag);
                    for (var i = 0 ; i < attrs.length ; i++)
                        node.setAttribute(attrs[i].name, attrs[i].value);
                    currentParent.appendChild(node);
                    if (!unary)
                        currentParent = node;
                },

                end : function(tag)
                {
                    currentParent = currentParent.parentNode || retval;
                },

                chars : function(text)
                {
                    currentParent.appendChild(document.createTextNode(text));
                },

                comment : function(text)
                {
                    currentParent.appendChild(document.createComment(text));
                }
            });

        return retval;
    }

    // constructor part
    var msg = config.message || '',
        padding = 30,
        rx = 10,
        ry = 10,
        screenWidth = windowSize().width,
        svgId = config.id || '',
        bubble = '',
        toonBubble = false,
        character = 'bat',
        emotion = 'NEUTRAL',
        bubbleStyle = config.bubbleStyle || 'fill: #',
        self = this

    this.sender = ''
    this.receivedTime = ''

    if(svgId === '') {
        throw 'svgId undefined'
    }

    var bubbleWidth = Math.min(parseInt(screenWidth * 0.65), 400) - (toonBubble ? 80 : 0)
    var characterWidth = 60

    console.log(windowSize().width)

    var matrix = function() {
        var coordinates = {}

        if(config.align === 'left') {
            coordinates.bubbleX = toonBubble ? 80 : 10
            coordinates.messageX = coordinates.bubbleX + 10
            coordinates.senderX = coordinates.bubbleX
            coordinates.charX = 5
        } else {
            coordinates.charX = toonBubble ? screenWidth - characterWidth - 10 : screenWidth            // right margin : 10
            coordinates.bubbleX = coordinates.charX - bubbleWidth - 10
            coordinates.messageX = coordinates.bubbleX + 10
            coordinates.senderX = coordinates.bubbleX
        }

        return coordinates
    }

    // private methods
    var drawBubble = function(svg) {
        var x = matrix().bubbleX
        var rect = svg.rect(bubbleWidth, svg.height() - 15).x(x).attr('rx', rx).attr('ry', ry).style(bubbleStyle)

        return rect
    }

    var drawMessage = function(svg) {
        var _width = bubbleWidth - 20
        var _x = matrix().messageX
        var _height = 70				// just initial value. this value will be recalculated
        var fobj = svg.foreignObject(_width, _height).attr({id: 'fobj', x: _x, y: 10})
        var body = ''

        body += '<body xmlns="http://www.w3.org/1999/xhtml"><div id="msg_' + config.id + '" style="width: ' + _width + 'px; font-size:12px; white-space: pre-wrap; white-space: -moz-pre-wrap; white-space: -pre-wrap; white-space: -o-pre-wrap; word-wrap: break-word;">' + msg + '</div></body>'

        fobj.appendChild(htmlToFragment(body))

        return fobj
    }

    var drawFrom = function(svg) {
        var x = matrix().senderX
        var senderText = svg.text(self.receivedTime + ' from ' + self.sender).attr({x: x, y: svg.height() - 15})
        senderText.font({
            size: 10
        })
        return senderText;
    }

    var drawCharacter = function(svg) {
        var url = '/image/bimage/toon/char/' + character + '/' + emotion + '.png'
        var width = 60
        var height = 60
        var x = matrix().charX
        var y = 10
        var charImg = svg.image(url, width, height).x(x).y(y)
        return charImg
    }

    var containerSize = function() {
        var el = document.getElementById('msg_' + config.id)
        var matrix = {}

        if(el !== null) {
            // not firefox
            matrix.width = el.offsetWidth
            matrix.height = el.offsetHeight
        } else {
            // firefox
            var el = document.getElementsByTagName('div')[0]
            var boundary = el.getBoundingClientRect()
            matrix.width = boundary.width
            matrix.height = boundary.height
        }

        return matrix
    }

    this.draw = function() {
        var height = 70
        var svg = SVG(svgId).size(screenWidth, height)

        bubble = svg.group()

        bubble.opacity(0)

        // just draw first ( then resize by message length because we do not know how long is the message before render text actually )
        var background = drawBubble(svg)
        var fobj = drawMessage(svg)
        var sender = drawFrom(svg)
        var character = null

        bubble.add(background)
        bubble.add(fobj)
        bubble.add(sender)

        var offsetHeight = containerSize().height

        // adjust height relative to message div container inside of foriegnobject
        var computedHeight = Math.max(offsetHeight + padding, 70)
        svg.height(computedHeight)
        bubble.height(computedHeight)
        fobj.attr({height: computedHeight})
        background.height(computedHeight - 15)
        sender.y(computedHeight - 10)

        if(toonBubble) {
            character = drawCharacter(svg)
            bubble.add(character)
        }

        bubble.animate(500, ">", 0).opacity(1)
        return this
    }

    this.from = function(_sender) {
        this.sender = _sender
        return this
    }

    this.when = function(_receivedTime) {
        this.receivedTime = _receivedTime
        return this
    }

    this.toonBubble = function(_character, _emotion) {
        toonBubble = true
        emotion = _emotion

        if(_character !== '') character = _character


        return this
    }

    // bubble effect
    this.vibrate = function(duration) {
        if(bubble) {
            var originX = bubble.x(), originY = bubble.y();
            bubble.animate(50).move(originX - 5, originY).move(originX, originY).move(originX + 5, originY).loop(20);
        }
    }

    this.bounce = function(duration) {
        if(bubble) {
            var loop = 0
            var keyTime = parseInt(duration / 3)

            var zoomOut = function() {
                if(loop > 5) {
                    restore()
                    return
                }
                bubble.animate(keyTime * 0.5).scale(0.5).after(zoomIn)
            }
            var zoomIn = function() {
                bubble.animate(keyTime * 1.5).scale(1.2).after(zoomOut)
                loop++
            }
            var restore = function() {
                bubble.animate(keyTime).scale(1).after(zoomOut)
            }

            zoomOut()
        }
    }

    this.ambulence = function(loop, duration) {
        var rect = bubble.get(0);
        rect.animate(duration).fill('red').loop(loop)
    }
}

var MyMessage = function() {}
var OtherMessage = function() {}

MyMessage.create = function(messageId, _message) {
    return new ChatBubble({
        id: messageId,
        message: _message,
        bubbleStyle: "fill:aquamarine;stroke:black;stroke-width:2;opacity:0.1",
        align: 'right'
    })
}

OtherMessage.create = function(messageId, _message) {
    return new ChatBubble({
        id: messageId,
        message: _message,
        bubbleStyle: 'fill:#f4cecf;stroke:black;stroke-width:2;opacity:0.1',
        align: 'left'
    })
}