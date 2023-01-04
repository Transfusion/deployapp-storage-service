package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.FtpCredential;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.UUID;

import static io.github.transfusion.deployapp.storagemanagementservice.services.StorageService.*;

public class FtpDeleter implements IDeleter {

    private final FtpCredential ftpCreds;
    private final FTPClient client;

    public FtpDeleter(FtpCredential ftpCreds) throws AuthenticationException, IOException {
        this.ftpCreds = ftpCreds;
        this.client = getFTPClient(this.ftpCreds);
    }

    private boolean removeDirectoryRecursively(String pathName) throws AuthenticationException, IOException {
//        FTPClient client = getFTPClient(this.ftpCreds);
        int replyCode = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            return false;
        }

        FTPFile[] files = client.listFiles(pathName);
        if (files == null || files.length <= 0) return true;
        for (FTPFile file : files) {
            // found in the wild; some FTP servers return the current directory *in* in the directory listing...
            // drwxr-xr-x    6 foo   foo          202 Nov 13 22:17 ..
            // drwxr-xr-x    2 foo   foo           32 Nov 13 22:17 .
            if (file.isDirectory() && (file.getName().startsWith("..") || file.getName().equals("."))) continue;
            if (file.isDirectory()) {
                removeDirectoryRecursively(pathName + "/" + file.getName());
                client.changeWorkingDirectory(pathName.substring(0, pathName.lastIndexOf("/")));
                client.removeDirectory(pathName);
            } else {
                client.deleteFile(pathName + "/" + file.getName());
            }
        }

//        client.changeWorkingDirectory(pathName.substring(0, pathName.lastIndexOf("/")));
        client.removeDirectory(pathName);
        return true;
    }

    @Override
    public void deleteStorageCredential() throws AuthenticationException, IOException {
        removeDirectoryRecursively(getFtpPublicPrefixDirectory(ftpCreds.getDirectory()));
        removeDirectoryRecursively(getFtpPrivatePrefixDirectory(ftpCreds.getDirectory()));
        try {
            client.disconnect();
        } catch (Exception caught) {
            caught.printStackTrace();
        }
    }

    @Override
    public void deleteAllAppBinaryData(UUID id) throws AuthenticationException, IOException {
        removeDirectoryRecursively(getFtpPrivateAppBinaryDirectory(ftpCreds.getDirectory(), id));
        removeDirectoryRecursively(getFtpPublicAppBinaryDirectory(ftpCreds.getDirectory(), id));
        try {
            client.disconnect();
        } catch (Exception caught) {
            caught.printStackTrace();
        }
    }
}
