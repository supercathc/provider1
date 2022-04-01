package com.cat.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.cat.domain.Scholar;
import com.cat.listener.ScholarListener;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EasyExcels {
    /**
     * @param file   导入的Excel文件
     * @param tClass 对应excel实体bean
     * @return 对应excel实体bean的list
     */
    public static void getExcelContent(MultipartFile file, Class<Scholar> tClass) {
        try {
            ScholarListener listener = new ScholarListener();
            /* file.getInputStream() 输入流
             * listener 自定义的监听器
             */
            ExcelReader excelReader = EasyExcel.read(file.getInputStream(), Scholar.class, listener).build();
            //readSheet(0) 指定读取哪一页的数据
            ReadSheet sheet = EasyExcel.readSheet(0).build();
            excelReader.read(sheet);
            //读取所有的sheet页的数据
            //excelReader.readAll();
            //关闭，读的时候会创建临时文件，到时磁盘会崩的
            excelReader.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出 Excel ：一个 sheet，带表头.
     *
     * @param response  HttpServletResponse
     * @param  data      数据 list，每个元素为一个 BaseRowModel
     * @param fileName  导出的文件名
     * @param sheetName 导入文件的 sheet 名
     * @param model     映射实体类，Excel 模型
     * @throws Exception 异常
     */

    public void writeExcel(HttpServletResponse response, List<? extends Object> data, String fileName, String sheetName, Class model) throws Exception {

        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        // 字体
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setWrapped(true);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置内容靠中对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(getOutputStream(fileName, response), model).excelType(ExcelTypeEnum.XLSX).sheet(sheetName).registerWriteHandler(horizontalCellStyleStrategy)
                //最大长度自适应 目前没有对应算法优化 建议注释掉不用 会出bug
//                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .doWrite(data);

    }

    /**
     * 导出文件时为Writer生成OutputStream.
     *
     * @param fileName 文件名
     * @param response response
     * @return ""
     */
    private OutputStream getOutputStream(String fileName, HttpServletResponse response) throws Exception {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            return response.getOutputStream();
        } catch (IOException e) {
            throw new Exception("导出excel表格失败!", e);
        }
    }


}
