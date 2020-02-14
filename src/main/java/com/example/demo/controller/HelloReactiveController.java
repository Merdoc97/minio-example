package com.example.demo.controller;

import com.example.demo.dto.ExampleDto;
import com.example.demo.dto.UserDto;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
public class HelloReactiveController {

    @Autowired
    private MinioClient minioClient;

    @GetMapping("/hello")
    public String helloMvc() {
        return "Hello webflux";
    }

    @GetMapping(value = "/hello-example", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ExampleDto helloExample() {
        return ExampleDto.builder()
                .name("example")
                .secondName("test")
                .build();
    }

    @GetMapping(value = "/hello-users", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserDto>> helloResponseEntity() {
        return ResponseEntity.ok(List.of(UserDto.builder()
                .email("test@email")
                .login("login")
                .build()));
    }

    @PostMapping(value = "/file/upload")
    public ResponseEntity uploadFiles(@RequestPart("file") FilePart filePart) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidResponseException, ErrorResponseException, NoResponseException, InvalidBucketNameException, XmlPullParserException, InternalException, RegionConflictException, InvalidArgumentException {

        filePart.transferTo(Path.of("/tmp/".concat(filePart.filename())));
        File file = Paths.get("/tmp/".concat(filePart.filename())).toFile();

        if (!minioClient.bucketExists("test")) {
            minioClient.makeBucket("test");
        }
        minioClient.putObject("test", file.getName(), new FileInputStream(file), "application/".concat(file.getName().split("\\.")[1]));
        file.delete();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/file/download/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String filename) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InvalidResponseException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(filename))
                .body(new InputStreamResource(minioClient.getObject("test", filename)));

    }
}
