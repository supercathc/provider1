package com.cat.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.cat.domain.Scholar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScholarListener extends AnalysisEventListener<Scholar> {
    @Override
    public void invoke(Scholar data, AnalysisContext context) {
        System.out.println(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }



}
