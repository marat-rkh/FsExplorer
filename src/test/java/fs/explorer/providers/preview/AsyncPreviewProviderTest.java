package fs.explorer.providers.preview;

import fs.explorer.TestEnvironment;
import fs.explorer.providers.dirtree.TreeNodeData;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AsyncPreviewProviderTest {
    private PreviewProvider previewProvider;
    private AsyncPreviewProvider asyncPreviewProvider;

    private CyclicBarrier cyclicBarrier;
    private AtomicInteger counter;

    @Before
    public void setUp() {
        Assume.assumeTrue(TestEnvironment.asyncTestsNeeded());
    }

    @Test
    public void interruptsGetPreviewOnShutdownNow()
            throws InterruptedException, TimeoutException, BrokenBarrierException {
        previewProvider = mock(PreviewProvider.class);
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
        }).when(previewProvider).getPreview(any(), any(), any());

        cyclicBarrier = new CyclicBarrier(2);
        counter = new AtomicInteger(0);

        asyncPreviewProvider = new AsyncPreviewProvider(previewProvider, 0);

        asyncPreviewProvider.getPreview(null, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        Thread.sleep(2000);
        asyncPreviewProvider.shutdownNow();
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        assertEquals(1, counter.get());
    }

    @Test
    public void cancelsPreviousGetPreviewTaskWithANewOne()
            throws InterruptedException, TimeoutException, BrokenBarrierException {
        previewProvider = mock(PreviewProvider.class);

        TreeNodeData dummyData1 = mock(TreeNodeData.class);
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
        }).when(previewProvider).getPreview(same(dummyData1), any(), any());

        TreeNodeData dummyData2 = mock(TreeNodeData.class);
        doNothing().when(previewProvider).getPreview(same(dummyData2), any(), any());

        cyclicBarrier = new CyclicBarrier(2);
        counter = new AtomicInteger(0);

        asyncPreviewProvider = new AsyncPreviewProvider(previewProvider, 0);

        asyncPreviewProvider.getPreview(dummyData1, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        Thread.sleep(2000);
        asyncPreviewProvider.getPreview(dummyData2, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        asyncPreviewProvider.shutdownNow();
        assertEquals(1, counter.get());
    }

    // TODO try to find a way to test cancels of delayed tasks
}