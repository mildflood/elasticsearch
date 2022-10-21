package gov.sec.idap.maxds.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RestController;  
@RestController  
public class HelloworldController   
{  
@RequestMapping("/")  
public String hello()   
{  
return "Hello MaxDS on AWS! ";  
}  
}  