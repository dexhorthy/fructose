package com.github.horthy.fructose;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * created by dex on 6/11/16.
 */
public class DecoratedConsumerTest {

    @Mock Consumer<Integer> before;
    @Mock Consumer<Integer> consumer;
    @Mock Consumer<Integer> success;
    @Mock BiConsumer<Integer, Throwable> failure;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCallsFunction() throws Exception {
        Decorators.decorating(consumer).build().accept(1);
        verify(consumer, times(1)).accept(1);

        verify(before, never()).accept(anyInt());
        verify(failure, never()).accept(anyInt(), any());
        verify(success, never()).accept(anyInt());
    }

    @Test
    public void testCallsBefore() throws Exception {

        Decorators.decorating(consumer)
                .before(before)
                .build()
                .accept(1);

        InOrder inOrder = inOrder(before, consumer);

        inOrder.verify(before, times(1)).accept(1);
        inOrder.verify(consumer, times(1)).accept(1);

        verify(failure, never()).accept(anyInt(), any());
        verify(success, never()).accept(anyInt());
    }

    @Test
    public void testCallsBeforeAndSuccess() throws Exception {

        Decorators.decorating(consumer)
                .before(before)
                .success(success)
                .build()
                .accept(1);

        InOrder inOrder = inOrder(before, consumer, success);

        inOrder.verify(before, times(1)).accept(1);
        inOrder.verify(consumer, times(1)).accept(1);
        inOrder.verify(success, times(1)).accept(1);

        verify(failure, never()).accept(anyInt(), any());
    }

    @Test
    public void testCallsBeforeAndFailure() throws Exception {
        RuntimeException e = new RuntimeException("oops");
        doThrow(e).when(consumer).accept(1);

        try {
            Decorators.decorating(consumer)
                    .before(before)
                    .success(success)
                    .failure(failure)
                    .build()
                    .accept(1);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ignored) {}

        InOrder inOrder = inOrder(before, consumer, failure);

        inOrder.verify(before, times(1)).accept(1);
        inOrder.verify(consumer, times(1)).accept(1);
        inOrder.verify(failure, times(1)).accept(1,e);

        verify(success, never()).accept(anyInt());
    }


}
