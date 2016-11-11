package org.fer.syncfiles.domain;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by fensm on 06/11/2016.
 */
public class MsgLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> {

    public MsgLinkedBlockingQueue() {
        super(100);
    }
}
