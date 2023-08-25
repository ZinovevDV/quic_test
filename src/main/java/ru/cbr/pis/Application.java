package ru.cbr.pis;

import net.luminis.quic.QuicConnection;
import net.luminis.quic.QuicStream;
import net.luminis.quic.Version;
import net.luminis.quic.log.Logger;
import net.luminis.quic.log.SysOutLogger;
import net.luminis.quic.server.ApplicationProtocolConnection;
import net.luminis.quic.server.ServerConnector;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Application {

    public static final String certificateFileName = "/mnt/MyProjects/git/quic_test/src/main/resources/keys/Server-certificate.pem";
    public static final String privateKeyFileName ="/mnt/MyProjects/git/quic_test/src/main/resources/keys/Server-private-key.key";

    public static final int portNumber = 5001;

    public static final boolean retryMode = true;

    private static void registerProtocolHandler(ServerConnector serverConnector, Logger log) {
        serverConnector.registerApplicationProtocol("echo", (protocol, quicConnection) -> new EchoProtocolConnection(quicConnection, log));
    }
    static class EchoProtocolConnection implements ApplicationProtocolConnection {

        private Logger log;

        public EchoProtocolConnection(QuicConnection quicConnection, Logger log) {
            this.log = log;
        }

        @Override
        public void acceptPeerInitiatedStream(QuicStream quicStream) {
            new Thread(() -> handleEchoRequest(quicStream)).start();
        }

        private void handleEchoRequest(QuicStream quicStream) {
            try {
                // Note that this implementation is not safe to use in the wild, as attackers can crash the server by sending arbitrary large requests.
                byte[] bytesRead = quicStream.getInputStream().readAllBytes();
                System.out.println("Read echo request with " + bytesRead.length + " bytes of data.");
                quicStream.getOutputStream().write(bytesRead);
                quicStream.getOutputStream().close();
            } catch (IOException e) {
                log.error("Reading quic stream failed", e);
            }
        }
    }



    public static void main(String[] args) throws Exception {

        Logger log = new SysOutLogger();
        log.timeFormat(Logger.TimeFormat.Long);
        log.logInfo(true);
        log.logWarning(true);


        File certificateFile = new File(certificateFileName);

        if (!certificateFile.exists()) {
            System.err.println("Cannot open certificate file " + certificateFileName);
            System.exit(1);
        }

        File privateKeyFile = new File(privateKeyFileName);

        if (!privateKeyFile.exists()) {
            System.err.println("Cannot open private key file " + privateKeyFileName);
            System.exit(1);
        }

        List<Version> supportedVersions = new ArrayList<>();
        supportedVersions.add(Version.QUIC_version_1);
        supportedVersions.add(Version.QUIC_version_2);

        ServerConnector serverConnector = new ServerConnector(portNumber,
                new FileInputStream(certificateFile),
                new FileInputStream(privateKeyFile),
                supportedVersions, retryMode, log);

        registerProtocolHandler(serverConnector, log);

        serverConnector.start();


    }
}
