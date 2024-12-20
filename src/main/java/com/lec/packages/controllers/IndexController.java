package com.lec.packages.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.service.ClubService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {

	@Autowired
	private final ClubService clubService;
    
    @GetMapping("/")
    public String index(PageRequestDTO pageRequestDTO, Model model){
    	PageResponseDTO<ClubDTO> responseDTO = clubService.list(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        
        return "index";
    }
	
	
}
