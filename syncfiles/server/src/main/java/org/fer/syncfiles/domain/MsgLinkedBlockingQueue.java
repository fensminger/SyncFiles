package org.fer.syncfiles.domain;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by fensm on 06/11/2016.
 */
public class MsgLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> {

    public MsgLinkedBlockingQueue() {
        super(100);
    }

    @Override
    public boolean add(E e) {
        if (offer(e))
            return true;
        else {
            poll();
            if (!offer(e)) {
                throw new IllegalStateException("Queue full !!!");
            }
            return true;
        }
    }

}
