package com.example.shop.controller;

import com.example.shop.model.Product;
import com.example.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    
    // Home page
    @GetMapping("/")
    public String homePage() {
        return "home";
    }
    
    // Show form to create a product
    @GetMapping("/product/new")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }
    
    // Handle form submission
    @PostMapping("/product/save")
    public String saveProduct(@ModelAttribute Product product, Model model) {
        // Save product to database
        productRepository.save(product);
        
        // Add the product to the model for the success page
        model.addAttribute("product", product);
        
        return "success";
    }
    
    // View all saved products
    @GetMapping("/products")
    public String viewAllProducts(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "product-list";
    }
    
    // View product details
    @GetMapping("/product/view/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            return "success"; // Reusing the success page to show details
        } else {
            return "redirect:/products";
        }
    }
    
    // Custom date formatter for the form
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, 
            new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }
}