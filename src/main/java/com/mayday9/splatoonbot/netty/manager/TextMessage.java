package com.mayday9.splatoonbot.netty.manager;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class TextMessage extends TextWebSocketFrame {

    public TextMessage(String text) {
        super(text);
    }
}