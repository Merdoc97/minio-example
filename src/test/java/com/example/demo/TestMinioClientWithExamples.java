package com.example.demo;

import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.UUID;

@SpringBootTest
public class TestMinioClientWithExamples {

    @Autowired
    private MinioClient minioClient;

    private ClassLoader classLoader = this.getClass().getClassLoader();
    private String bucketName = "test-bucket";
    private String testReadFile = "test/example/test";
    InputStream file = classLoader.getResourceAsStream("download.png");

    @BeforeEach
    public void setUp() throws Exception {
        if (!minioClient.bucketExists(bucketName)) {
            minioClient.makeBucket(bucketName);
        }
        try {
            minioClient.statObject(bucketName, testReadFile);
        } catch (ErrorResponseException e) {
            var code = e.errorResponse().errorCode();
            if (code == ErrorCode.NO_SUCH_KEY || code == ErrorCode.NO_SUCH_OBJECT)
                minioClient.putObject(bucketName, testReadFile, file, "application/jpg");
        }


    }


    @Test
    public void addFileToBucket() throws Exception {
        minioClient.putObject(bucketName, "test/example/" + UUID.randomUUID().toString(), file, "application/jpg");

    }

    @Test
    public void testReadExample() throws Exception {
        InputStream url = minioClient.getObject(bucketName, testReadFile);
        Assertions.assertNotNull(url);
    }
}
