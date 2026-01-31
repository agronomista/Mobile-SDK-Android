package com.dji.sdk.sample.internal.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelemetryUdpSender {
    private final String host;
    private final int port;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private DatagramSocket socket;

    public TelemetryUdpSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void send(String payload) {
        executor.execute(() -> {
            try {
                if (socket == null || socket.isClosed()) {
                    socket = new DatagramSocket();
                }
                InetAddress address = InetAddress.getByName(host);
                byte[] data = payload.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                socket.send(packet);
            } catch (Exception ignored) {
                // Best-effort telemetry; ignore send failures.
            }
        });
    }

    public void close() {
        executor.shutdownNow();
        if (socket != null) {
            socket.close();
        }
    }
}
