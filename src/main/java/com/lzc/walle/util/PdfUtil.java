package com.lzc.walle.util;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lzc.walle.vo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfUtil {



    public void createPDF(Document document, PdfWriter writer, List<Product> products) throws IOException {
        //Document document = new Document(PageSize.A4);
        try {
            document.addTitle("sheet of product");
            document.addAuthor("scurry");
            document.addSubject("product sheet.");
            document.addKeywords("product.");
            document.open();
            PdfPTable table = createTable(writer,products);
            document.add(table);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
    public static PdfPTable createTable(PdfWriter writer,List<Product> products) throws IOException, DocumentException {
        //生成一个三列的表格
        PdfPTable table = new PdfPTable(3);
        PdfPCell cell;
        //设置高度
        int size = 20;
        Font font = new Font(BaseFont.createFont("C://Windows//Fonts//simfang.ttf", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED));

        // 通过遍历的方式获取产品编号、产品名称、产品价格
        for(int i = 0;i<products.size();i++) {
            //产品编号
            cell = new PdfPCell(new Phrase(products.get(i).getProductCode(),font));
            cell.setFixedHeight(size);
            table.addCell(cell);
            //产品名称
            cell = new PdfPCell(new Phrase(products.get(i).getProductName(),font));
            cell.setFixedHeight(size);
            table.addCell(cell);
            //产品价格
            cell = new PdfPCell(new Phrase(products.get(i).getPrice()+"",font));
            cell.setFixedHeight(size);
            table.addCell(cell);
        }
        //设置最后一行
        cell = new PdfPCell(new Phrase("from:lzc"));
        //设置所占列数
        cell.setColspan(3);
        cell.setFixedHeight(size*2);
        //设置水平居中
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        //设置垂直居中
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);


        return table;
    }


}
