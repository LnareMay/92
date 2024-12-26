package com.lec.packages.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lec.packages.dto.MemberJoinDTO;
import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

	@Value("${com.lec.upload.path}")
	private String uploadPath;
	
	private final MemberService memberService;

	@GetMapping({ "/login", "/login/{error}/{logout}", "/login/{logout}" })
	public void loginGet(@RequestParam(name = "error", defaultValue = "") @PathVariable Optional<String> error,
			@RequestParam(name = "logout", defaultValue = "") @PathVariable Optional<String> logout) {
		log.info("login get ................... ");
		log.info("logout ................... " + logout);

		if (logout != null) {
			log.info("user logout ................... ");
		}
	}

	@GetMapping("/join")
	public void joinGet() {
		log.info("회원가입 GET방식.....");
	}

	@PostMapping("/join")
	public String joinPost(MemberJoinDTO memberJoinDTO, HttpServletRequest request, RedirectAttributes redirectAttributes) {
	    log.info("회원가입 POST방식.....");
	    log.info(memberJoinDTO);

	    // 세션에서 업로드된 파일 이름 가져오기
	    HttpSession session = request.getSession();
	    String storedFileName = (String) session.getAttribute("storedFileName");
	  
	        // 회원가입 처리
	        memberService.join(memberJoinDTO, storedFileName);
	        redirectAttributes.addFlashAttribute("result", "회원가입 성공");
	    

	    // 회원가입 성공 후 세션에서 파일 이름 제거
	    session.removeAttribute("storedFileName");

	    return "redirect:/member/login";
	}

	
	@GetMapping("/checkId")
	public ResponseEntity<Map<String, String>> checkId(@RequestParam("MEM_ID") String memId) {
	    Map<String, String> response = new HashMap<>();

	    try {
	        boolean isDuplicate = memberService.isDuplicateId(memId);

	        if (isDuplicate) {
	            response.put("status", "FAIL");
	            response.put("message", "이미 존재하는 아이디입니다.");
	        } else {
	            response.put("status", "SUCCESS");
	            response.put("message", "사용 가능한 아이디입니다.");
	        }

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        response.put("status", "ERROR");
	        response.put("message", "서버 처리 중 문제가 발생했습니다.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	@GetMapping("/mypage")
	public String mypageGet(HttpServletRequest request, Model model) {
        String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI); 
        return "member/mypage"; 
    }
	
	@GetMapping("/mypage_modify")
	public String mypageModifyGet(HttpServletRequest request, Model model) {
	    String requestURI = request.getRequestURI();
	    model.addAttribute("currentURI", requestURI); // 템플릿에서 사용된다면 유지
	    return "member/mypage_modify";
	}

	@PostMapping("/modify")
	public String mypageModifyPost(MemberJoinDTO memberJoinDTO, HttpServletRequest request, RedirectAttributes redirectAttributes) {
	    // 세션에서 업로드된 파일 이름 가져오기
	    HttpSession session = request.getSession();
	    String storedFileName = (String) session.getAttribute("storedFileName");
	    if (storedFileName == null) {
	        storedFileName = ""; // 기본 파일 처리
	    }

	    try {
	        memberService.modify(memberJoinDTO, storedFileName);
	        redirectAttributes.addFlashAttribute("result", "나의 정보 수정 성공");
	    } catch (Exception e) {
	        log.error("회원 정보 수정 실패", e);
	        redirectAttributes.addFlashAttribute("error", "회원 정보 수정 중 오류가 발생했습니다.");
	        return "redirect:/member/mypage_modify"; // 에러 시 수정 페이지로 다시 이동
	    }

	    // 세션에서 파일 이름 제거
	    session.removeAttribute("storedFileName");
	    return "redirect:/member/mypage"; // 수정 성공 시 마이페이지로 이동
	}

}

