package fs.explorer.providers.dirtree;

import fs.explorer.TestEnvironment;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AsyncFsDataProviderTest {
    private FsDataProvider fsDataProvider;
    private AsyncFsDataProvider asyncFsDataProvider;

    private CyclicBarrier cyclicBarrier;
    private AtomicInteger counter;

    @Before
    public void setUp() {
        Assume.assumeTrue(TestEnvironment.asyncTestsNeeded());

        fsDataProvider = mock(FsDataProvider.class);
        doAnswer(invocationOnMock -> {
            cyclicBarrier.await(10, TimeUnit.SECONDS);
            try {
                Thread.sleep(10000);
            } catch(InterruptedException e) {
                counter.incrementAndGet();
            } finally {
                cyclicBarrier.await(10, TimeUnit.SECONDS);
            }
            return null;
        }).when(fsDataProvider).getNodesFor(any(), any(), any());

        cyclicBarrier = new CyclicBarrier(4);
        counter = new AtomicInteger(0);

        asyncFsDataProvider = new AsyncFsDataProvider(fsDataProvider);
    }

    @Test
    public void interruptsWithShutdownNow()
            throws InterruptedException, TimeoutException, BrokenBarrierException {
        asyncFsDataProvider.getNodesFor(null, null, null);
        asyncFsDataProvider.getNodesFor(null, null, null);
        asyncFsDataProvider.getNodesFor(null, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        Thread.sleep(2000);
        asyncFsDataProvider.shutdownNow();
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        assertEquals(3, counter.get());
    }
}