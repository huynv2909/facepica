package org.huynv.facepica;

import java.io.File;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;

public class S3 {
	private static AmazonS3 credential = AmazonS3ClientBuilder.defaultClient();
	
	private static void init() {
		credential = AmazonS3ClientBuilder.defaultClient();
	}
	
	public static PutObjectResult uploadObjectToBucket(String object_path, String bucket_name) {
		if (credential == null) {
			init();
		}

		PutObjectResult result = new PutObjectResult();

		try {
			String[] strArr = object_path.split("/");
			result = credential.putObject(bucket_name, strArr[strArr.length - 1], new File(object_path));
		} catch (AmazonServiceException e) {
		    System.out.println(e.getErrorMessage());
		}
		return result;
	}

	public static void uploadFolderToBucket(String folder_path, String bucket_name) {
		final File folder = new File(folder_path);

		for (final File f : folder.listFiles()) {
			if (f.isFile()) {
				S3.uploadObjectToBucket(f.getAbsolutePath(), bucket_name);
			}

		}
	}

	public static void deleteObjectFromBucket(String obj_name, String bucket) {
		if (credential == null) {
			init();
		}

		try {
			credential.deleteObject(bucket, obj_name);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
