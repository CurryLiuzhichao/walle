package com.lzc.walle.controller;

import com.lzc.walle.entity.ViewPDF;
import com.lzc.walle.vo.Product;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pdf")
public class PrintPdfController {
    @RequestMapping("printPdf")
    public ModelAndView printPdf(){
        Product product1 = new Product("产品一","cp01",120);
        Product product2 = new Product("产品一","cp01",120);
        Product product3 = new Product("产品一","cp01",120);
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        Map<String, Object> model = new HashMap<>();
        model.put("sheet", products);
        return new ModelAndView(new ViewPDF(), model);
    }
}
