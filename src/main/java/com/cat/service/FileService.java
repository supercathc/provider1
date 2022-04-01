package com.cat.service;

import com.cat.domain.Scholar;
import com.cat.utils.EasyExcels;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    public void saveSubject(MultipartFile file) {
        try {
            EasyExcels.getExcelContent(file, Scholar.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void export(HttpServletResponse response) {
        try {
            List<Scholar> list = new ArrayList<>();
            for (int x = 0; x < 1000000; x++) {
                Scholar scholar = new Scholar();
                scholar.setLocation("湖底" + x);
                scholar.setId(String.valueOf(x));
                scholar.setCard("775178" + x);
                scholar.setName("小黄人" + x + "号");
                list.add(scholar);
            }
            EasyExcels easyExcels = new EasyExcels();
            easyExcels.writeExcel(response,list, "模板", "sheet11", Scholar.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

