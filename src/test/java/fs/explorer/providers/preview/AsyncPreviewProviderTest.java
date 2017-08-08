package fs.explorer.providers.preview;

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
    public void interruptsGetTextPreviewOnShutdownNow()
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
        }).when(previewProvider).getTextPreview(any(), any(), any());

        cyclicBarrier = new CyclicBarrier(2);
        counter = new AtomicInteger(0);

        asyncPreviewProvider = new AsyncPreviewProvider(previewProvider);

        asyncPreviewProvider.getTextPreview(null, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        Thread.sleep(2000);
        asyncPreviewProvider.shutdownNow();
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        assertEquals(1, counter.get());
    }

    @Test
    public void interruptsGetImagePreviewOnShutdownNow()
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
        }).when(previewProvider).getImagePreview(any(), any(), any());

        cyclicBarrier = new CyclicBarrier(2);
        counter = new AtomicInteger(0);

        asyncPreviewProvider = new AsyncPreviewProvider(previewProvider);

        asyncPreviewProvider.getImagePreview(null, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        Thread.sleep(2000);
        asyncPreviewProvider.shutdownNow();
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        assertEquals(1, counter.get());
    }

    @Test
    public void cancelsPreviousGetTextTaskWithGetImageTask()
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
        }).when(previewProvider).getTextPreview(any(), any(), any());
        doNothing().when(previewProvider).getImagePreview(any(), any(), any());

        cyclicBarrier = new CyclicBarrier(2);
        counter = new AtomicInteger(0);

        asyncPreviewProvider = new AsyncPreviewProvider(previewProvider);

        asyncPreviewProvider.getTextPreview(null, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        Thread.sleep(2000);
        asyncPreviewProvider.getImagePreview(null, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        asyncPreviewProvider.shutdownNow();
        assertEquals(1, counter.get());
    }

    @Test
    public void cancelsPreviousGetImageTaskWithGetTextTask()
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
        }).when(previewProvider).getImagePreview(any(), any(), any());
        doNothing().when(previewProvider).getTextPreview(any(), any(), any());

        cyclicBarrier = new CyclicBarrier(2);
        counter = new AtomicInteger(0);

        asyncPreviewProvider = new AsyncPreviewProvider(previewProvider);

        asyncPreviewProvider.getImagePreview(null, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        Thread.sleep(2000);
        asyncPreviewProvider.getTextPreview(null, null, null);
        cyclicBarrier.await(10, TimeUnit.SECONDS);
        asyncPreviewProvider.shutdownNow();
        assertEquals(1, counter.get());
    }
}