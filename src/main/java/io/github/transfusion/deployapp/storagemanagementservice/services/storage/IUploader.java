package io.github.transfusion.deployapp.storagemanagementservice.services.storage;

import io.github.transfusion.deployapp.storagemanagementservice.db.entities.*;
//import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.naming.AuthenticationException;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Takes care of uploading objects, in particular {@link AppBinary} or {@link AppBinaryAsset} to public and private storage
 */
public interface IUploader {
    /*PutObjectResponse*/ void uploadPublicAppBinaryObject(/*S3Credential s3Creds,*/
            UUID appBinaryId, String name, File binary) throws Exception;

    /*PutObjectResponse*/ void uploadPrivateAppBinaryObject(/*S3Credential s3Creds,*/ UUID appBinaryId, String name, File binary) throws Exception;

    void abort();
}
