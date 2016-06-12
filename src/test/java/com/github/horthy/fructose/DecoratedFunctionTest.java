package com.github.horthy.fructose;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.PrintStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * created by dex on 6/11/16.
 */
public class DecoratedFunctionTest {

    @Mock Consumer<Integer> before;
    @Mock Function<Integer, Integer> function;
    @Mock BiConsumer<Integer, Integer> success;
    @Mock BiConsumer<Integer, Throwable> failure;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCallsFunction() throws Exception {
        Decorators.decorating(function).build().apply(1);
        verify(function, times(1)).apply(1);

        verify(before, never()).accept(anyInt());
        verify(failure, never()).accept(anyInt(), any());
        verify(success, never()).accept(anyInt(), anyInt());
    }

    @Test
    public void testCallsBefore() throws Exception {

        Decorators.decorating(function)
                .before(before)
                .build()
                .apply(1);

        InOrder inOrder = inOrder(before, function);

        inOrder.verify(before, times(1)).accept(1);
        inOrder.verify(function, times(1)).apply(1);

        verify(failure, never()).accept(anyInt(), any());
        verify(success, never()).accept(anyInt(), anyInt());
    }

    @Test
    public void testCallsBeforeAndSuccess() throws Exception {
        when(function.apply(1)).thenReturn(2);

        Decorators.decorating(function)
                .before(before)
                .success(success)
                .build()
                .apply(1);

        InOrder inOrder = inOrder(before, function, success);

        inOrder.verify(before, times(1)).accept(1);
        inOrder.verify(function, times(1)).apply(1);
        inOrder.verify(success, times(1)).accept(1,2);

        verify(failure, never()).accept(anyInt(), any());
    }

    @Test
    public void testCallsBeforeAndFailure() throws Exception {
        RuntimeException e = new RuntimeException("oops");
        when(function.apply(1)).thenThrow(e);

        try {
            Decorators.decorating(function)
                    .before(before)
                    .success(success)
                    .failure(failure)
                    .build()
                    .apply(1);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException ignored) {}

        InOrder inOrder = inOrder(before, function, failure);

        inOrder.verify(before, times(1)).accept(1);
        inOrder.verify(function, times(1)).apply(1);
        inOrder.verify(failure, times(1)).accept(1,e);

        verify(success, never()).accept(anyInt(), anyInt());
    }

    @Mock PrintStream printStream;

    @Test
    public void testReadmeExample() throws Exception {
        Function<String,Character> getFifthChar = s -> s.charAt(4);
        Function<String,Character> decorated = Decorators.decorating(getFifthChar)
                .before(str -> printStream.println("getting fifth char of " + str))
                .success((str, c) -> printStream.println(str + " had fifth character " + c))
                .failure((str, t) -> printStream.println("couldn't get fifth char of " + str + " exception was " + t.getMessage()))
                .build();

        char c = decorated.apply("abcdefg");
        assertEquals('e', c);

        InOrder inOrder = Mockito.inOrder(printStream);

        inOrder.verify(printStream, times(1)).println("getting fifth char of abcdefg");
        inOrder.verify(printStream, times(1)).println("abcdefg had fifth character e");

        try {
            decorated.apply("abc");
            Assert.fail("exception not thrown");
        } catch (StringIndexOutOfBoundsException ignored) {}

        inOrder.verify(printStream, times(1)).println("getting fifth char of abc");
        inOrder.verify(printStream, times(1)).println("couldn't get fifth char of abc exception was String index out of range: 4");


    }
}
