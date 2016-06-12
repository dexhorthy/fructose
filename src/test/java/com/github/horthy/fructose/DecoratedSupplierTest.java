package com.github.horthy.fructose;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * created by dex on 6/11/16.
 */
public class DecoratedSupplierTest {

    @Mock Runnable before;
    @Mock Supplier<Integer> supplier;
    @Mock Consumer<Integer> success;
    @Mock Consumer<Throwable> failure;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCallsFunction() throws Exception {
        when(supplier.get()).thenReturn(1);


        int i = Decorators.decorating(supplier)
                .build()
                .get();

        assertEquals(1, i);
        verify(supplier, times(1)).get();

        verify(before, never()).run();
        verify(failure, never()).accept(any());
        verify(success, never()).accept(anyInt());
    }

    @Test
    public void testCallsBefore() throws Exception {
        when(supplier.get()).thenReturn(1);

        int i = Decorators.decorating(supplier)
                .before(before)
                .build()
                .get();

        assertEquals(1, i);

        InOrder inOrder = inOrder(before, supplier);

        inOrder.verify(before, times(1)).run();
        inOrder.verify(supplier, times(1)).get();

        verify(failure, never()).accept(any());
        verify(success, never()).accept(anyInt());
    }

    @Test
    public void testCallsBeforeAndSuccess() throws Exception {

        when(supplier.get()).thenReturn(1);

        int i = Decorators.decorating(supplier)
                .before(before)
                .success(success)
                .build()
                .get();

        assertEquals(1, i);

        InOrder inOrder = inOrder(before, supplier, success);

        inOrder.verify(before, times(1)).run();
        inOrder.verify(supplier, times(1)).get();
        inOrder.verify(success, times(1)).accept(1);

        verify(failure, never()).accept(any());
    }

    @Test
    public void testCallsBeforeAndFailure() throws Exception {
        RuntimeException e = new RuntimeException("oops");
        doThrow(e).when(supplier).get();

        try {
            Decorators.decorating(supplier)
                    .before(before)
                    .success(success)
                    .failure(failure)
                    .build()
                    .get();
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ignored) {}

        InOrder inOrder = inOrder(before, supplier, failure);

        inOrder.verify(before, times(1)).run();
        inOrder.verify(supplier, times(1)).get();
        inOrder.verify(failure, times(1)).accept(e);

        verify(success, never()).accept(anyInt());
    }


}
