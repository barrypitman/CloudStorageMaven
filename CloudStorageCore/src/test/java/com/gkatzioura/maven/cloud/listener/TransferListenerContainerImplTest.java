package com.gkatzioura.maven.cloud.listener;

import org.apache.maven.wagon.Wagon;

import java.lang.reflect.Proxy;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.resource.Resource;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TransferListenerContainerImplTest {

    @Test
    public void fireTransferStartedSetsUnknownLengthForGetWhenLengthIsNotKnown() throws IOException {
        TransferListenerContainerImpl container = new TransferListenerContainerImpl(createWagonStub());
        Resource resource = new Resource("artifact.pom");
        File localFile = File.createTempFile("transfer", ".tmp");
        localFile.deleteOnExit();

        TransferStartedCaptureListener listener = new TransferStartedCaptureListener();
        container.addTransferListener(listener);

        container.fireTransferStarted(resource, TransferEvent.REQUEST_GET, localFile);

        assertEquals(-1L, listener.lastEvent.getResource().getContentLength());
    }

    @Test
    public void fireTransferStartedUsesLocalFileLengthForPut() throws IOException {
        TransferListenerContainerImpl container = new TransferListenerContainerImpl(createWagonStub());
        Resource resource = new Resource("artifact.pom");
        File localFile = File.createTempFile("transfer", ".tmp");
        localFile.deleteOnExit();

        TransferStartedCaptureListener listener = new TransferStartedCaptureListener();
        container.addTransferListener(listener);

        container.fireTransferStarted(resource, TransferEvent.REQUEST_PUT, localFile);

        assertEquals(localFile.length(), listener.lastEvent.getResource().getContentLength());
    }

    private Wagon createWagonStub() {
        return (Wagon) Proxy.newProxyInstance(
                Wagon.class.getClassLoader(),
                new Class<?>[]{Wagon.class},
                (proxy, method, args) -> null
        );
    }

    private static class TransferStartedCaptureListener implements TransferListener {

        private TransferEvent lastEvent;

        @Override
        public void transferInitiated(TransferEvent transferEvent) {
        }

        @Override
        public void transferStarted(TransferEvent transferEvent) {
            this.lastEvent = transferEvent;
        }

        @Override
        public void transferProgress(TransferEvent transferEvent, byte[] bytes, int i) {
        }

        @Override
        public void transferCompleted(TransferEvent transferEvent) {
        }

        @Override
        public void transferError(TransferEvent transferEvent) {
        }

        @Override
        public void debug(String s) {
        }
    }
}