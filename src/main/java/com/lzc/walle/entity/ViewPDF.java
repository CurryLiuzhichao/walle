package com.lzc.walle.entity;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import com.lzc.walle.util.PdfUtil;
import com.lzc.walle.vo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class ViewPDF extends AbstractPdfView {

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开
        // 设置文件名
//        String fileName = System.currentTimeMillis()+"_quotation.pdf";

        // 获取服务器PDF模板文件
        Resource resource = resourceLoader.getResource("classpath:/public/demo.pdf");
        String fileName;
        fileName = URLDecoder.decode(resource.getURL().getPath(), "utf-8");

        // 设置编码格式
        response.setCharacterEncoding("UTF-8");
        // 这个方法设置发送到客户端的响应的内容类型
        response.setContentType("application/pdf");
        // response.setHeader("Content-Disposition","filename=" + new String(fileName.getBytes(), "iso8859-1"));
        //直接下载
        response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes(), "iso8859-1"));
        List<Product> products = (List<Product>) model.get("sheet");
        PdfUtil pdfUtil = new PdfUtil();
        pdfUtil.createPDF(document, writer, products);
    }

    protected void demoPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取服务器PDF模板文件
        Resource resource = resourceLoader.getResource("classpath:/public/demo.pdf");
        String filePath;

        filePath = URLDecoder.decode(resource.getURL().getPath(), "utf-8");
        // 设置编码格式
        response.setCharacterEncoding("UTF-8");
        // 这个方法设置发送到客户端的响应的内容类型
        response.setContentType("application/pdf");
        // response.setHeader("Content-Disposition","filename=" + new String(fileName.getBytes(), "iso8859-1"));
        //直接下载
        response.setHeader("Content-Disposition","attachment;filename=" + new String(filePath.getBytes(), "iso8859-1"));
        List<Product> products = (List<Product>) model.get("sheet");
        PdfUtil pdfUtil = new PdfUtil();
        pdfUtil.createPDF(document, writer, products);
    }
}
