package com.shahrai.atm.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AppController {
    //for homepage
    @RequestMapping("/test")
    public String test(){
        System.out.println("AppController->welcome()");

        return "test";
    }

    @RequestMapping("/list_contact")
    public String listContact(Model model){

        //ContactBusiness business = new ContactBusiness();
        //List<Contact> contactList = business.getContactList();

        //model.addAttribute("contacts", contactList);

        return "contact";
    }
}
