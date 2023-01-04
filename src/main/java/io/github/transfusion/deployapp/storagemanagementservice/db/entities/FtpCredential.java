package io.github.transfusion.deployapp.storagemanagementservice.db.entities;

import io.github.transfusion.deployapp.storagemanagementservice.services.storage.*;

import javax.persistence.*;

@Entity
@Table(name = "ftp_credentials")
@PrimaryKeyJoinColumn(name = "id")
public class FtpCredential extends StorageCredential {
    public static final String IDENTIFIER = "FTP";

    public static String PUBLIC_PREFIX = "public/";
    public static String PRIVATE_PREFIX = "private/";
//    @Id
//    @Column(name = "id", nullable = false)
//    private UUID id;

//    @MapsId
//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "id", nullable = false)
//    private StorageCredential storageCredentials;

    @Column(name = "server", nullable = false)
    private String server;

    @Column(name = "port", nullable = false)
    private Integer port;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "directory", nullable = false, length = 4096)
    private String directory;

    @Column(name = "base_url", nullable = false, length = 2048)
    private String baseUrl;

//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }

//    public StorageCredential getStorageCredentials() {
//        return storageCredentials;
//    }
//
//    public void setStorageCredentials(StorageCredential storageCredentials) {
//        this.storageCredentials = storageCredentials;
//    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public IUploader resolveUploader(IUploaderResolver resolver) {
        return resolver.apply(this);
    }

    @Override
    public IDownloader resolveDownloader(IDownloaderResolver resolver) {
        return resolver.apply(this);
    }

    @Override
    public IURLGetter resolveURLGetter(IURLGetterResolver resolver) {
        return resolver.apply(this);
    }

    @Override
    public IDeleter resolveDeleter(IDeleterResolver resolver) throws Exception {
        return resolver.apply(this);
    }

}