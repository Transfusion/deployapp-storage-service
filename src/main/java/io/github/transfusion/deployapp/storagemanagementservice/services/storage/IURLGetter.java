package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

//import io.github.transfusion.deployapp.storagemanagementservice.db.entities.S3Credential;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public interface IURLGetter {
    /*public*/ URL getPrivateURL(/*S3Credential s3Creds, */ UUID appBinaryId, String name) throws MalformedURLException;
    /*public*/ URL getPublicURL(/*S3Credential s3Creds, */ UUID appBinaryId, String name) throws MalformedURLException;
}
