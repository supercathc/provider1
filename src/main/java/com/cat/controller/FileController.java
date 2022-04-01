package com.cat.controller;

import com.cat.domain.RequestMessage;
import com.cat.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @RequestMapping("/receive")
    public RequestMessage receiveMessage(MultipartFile file) {
        fileService.saveSubject(file);
        return null;
    }

    @RequestMapping("/export")
    public void exportMessage(HttpServletResponse response) {
        fileService.export(response);
    }
}
