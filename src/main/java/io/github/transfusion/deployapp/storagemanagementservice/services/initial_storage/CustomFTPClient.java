package io.github.transfusion.deployapp.storagemanagementservice.services.initial_storage;

//import org.apache.commons.net.ftp.DurationUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.time.Duration;

public class CustomFTPClient extends FTPClient {

    Logger logger = LoggerFactory.getLogger(CustomFTPClient.class);
    private Socket socket;

    @Override
    public boolean abort() throws IOException {
        Util.closeQuietly(socket);
        return super.abort();
    }

    private boolean isPositive(final Duration duration) {
        return duration != null && !duration.isNegative() && !duration.isZero();
    }

    @Override
    protected boolean _storeFile(final String command, final String remote, final InputStream local) throws IOException {
        socket = _openDataConnection_(command, remote);

        if (socket == null) {
            return false;
        }

        final OutputStream output;

        // fileType is private
        int fileType;
        try {
            Field f = FTPClient.class.getDeclaredField("fileType");
            f.setAccessible(true);
            fileType = (int) f.get(this);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return false;
        }

        // getBufferedOutputStream is private
        Method getDeclaredMethod_method;
        try {
            getDeclaredMethod_method = FTPClient.class.getDeclaredMethod("getBufferedOutputStream", OutputStream.class);
            getDeclaredMethod_method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }

        try {
            if (fileType == ASCII_FILE_TYPE) {
//            output = new ToNetASCIIOutputStream(getBufferedOutputStream(socket.getOutputStream()));
                output = new ToNetASCIIOutputStream((OutputStream) getDeclaredMethod_method.invoke(this, socket.getOutputStream()));
            } else {
                output = (OutputStream) getDeclaredMethod_method.invoke(this, socket.getOutputStream());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }

        // controlKeepAliveTimeout, and controlKeepAliveReplyTimeout
        Duration controlKeepAliveTimeout;
        Duration controlKeepAliveReplyTimeout;

        try {
            Field f = FTPClient.class.getDeclaredField("controlKeepAliveTimeout");
            f.setAccessible(true);
            controlKeepAliveTimeout = (Duration) f.get(this);
            f = FTPClient.class.getDeclaredField("controlKeepAliveReplyTimeout");
            f.setAccessible(true);
            controlKeepAliveReplyTimeout = (Duration) f.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }

//        CSL csl = null;
        CopyStreamListener csl = null;
        Constructor<? extends CopyStreamListener> CSL_constructor;

        try {
            Class<? extends CopyStreamListener> CSL_class = (Class<? extends CopyStreamListener>) Class.forName("org.apache.commons.net.ftp.FTPClient$CSL");
            CSL_constructor = CSL_class.getDeclaredConstructor(FTPClient.class, Duration.class, Duration.class);
            CSL_constructor.setAccessible(true);

            if (/*DurationUtils.*/isPositive(controlKeepAliveTimeout)) {
//            csl = new CSL(this, controlKeepAliveTimeout, controlKeepAliveReplyTimeout);
                csl = CSL_constructor.newInstance(this, controlKeepAliveTimeout, controlKeepAliveReplyTimeout);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }

        Method mergeListeners_method;
        try {
            mergeListeners_method = FTPClient.class.getDeclaredMethod("mergeListeners", CopyStreamListener.class);
            mergeListeners_method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }

        // Treat everything else as binary for now
        try {
            Util.copyStream(local, output, getBufferSize(), CopyStreamEvent.UNKNOWN_STREAM_SIZE, /*mergeListeners(csl),*/
                    (CopyStreamListener) mergeListeners_method.invoke(this, csl), false);
            output.close(); // ensure the file is fully written
            socket.close(); // done writing the file

            // Get the transfer response
            return completePendingCommand();
        } catch (CopyStreamException e) {
            logger.error("CustomFTPClient._storeFile likely aborted manually.");
            e.printStackTrace();
            throw new UncheckedIOException(e);
        } catch (final IOException e) {
            Util.closeQuietly(output); // ignore close errors here
            Util.closeQuietly(socket); // ignore close errors here
            throw e;
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
            return false;
        } finally {
            // TODO: cslDebug seems to only be used in testing.
//            if (csl != null) {
//                cslDebug = csl.cleanUp(); // fetch any outstanding keepalive replies
//            }
        }
    }
}
