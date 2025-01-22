package com.lec.packages.controllers;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lec.packages.domain.ChargeHistory;
import com.lec.packages.domain.Member;
import com.lec.packages.domain.Reservation;
import com.lec.packages.domain.TransferHistory;
import com.lec.packages.dto.ChargeHistoryDTO;
import com.lec.packages.dto.MemberJoinDTO;
import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.dto.TransferHistoryDTO;
import com.lec.packages.repository.ChargeHistoryRepository;
import com.lec.packages.repository.MemberRepository;
import com.lec.packages.repository.ReservationRepository;
import com.lec.packages.repository.TransferHistoryRepository;
import com.lec.packages.security.CustomUserDetailsService;
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
	private final CustomUserDetailsService customUserDetailsService;
	private final MemberRepository memberRepository;
	private final ReservationRepository reservationRepository;
	private final TransferHistoryRepository transferHistoryRepository;
	private final ChargeHistoryRepository chargeHistoryRepository;

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
	public String joinPost(MemberJoinDTO memberJoinDTO, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
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

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
			MemberSecurityDTO dto = (MemberSecurityDTO) authentication.getPrincipal();

			// Member 객체를 가져오는 로직 추가
			Optional<Member> memberOptional = memberRepository.findById(dto.getMemId());
			if (memberOptional.isPresent()) {
				model.addAttribute("member", memberOptional.get());
			}
		}
		return "member/mypage";
	}

	@GetMapping("/mypage_modify")
	public String mypageModifyGet(HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
		model.addAttribute("currentURI", requestURI); // 템플릿에서 사용된다면 유지

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
			MemberSecurityDTO dto = (MemberSecurityDTO) authentication.getPrincipal();

			// Member 객체를 가져오는 로직 추가
			Optional<Member> memberOptional = memberRepository.findById(dto.getMemId());
			if (memberOptional.isPresent()) {
				model.addAttribute("member", memberOptional.get());
			}
		}
		return "member/mypage_modify";
	}

	@PostMapping("/modify")
	public String mypageModifyPost(MemberJoinDTO memberJoinDTO, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		HttpSession session = request.getSession();
		String storedFileName = (String) session.getAttribute("storedFileName");
		if (storedFileName == null) {
			storedFileName = "";
		}

		try {
			// 회원 정보 수정
			memberService.modify(memberJoinDTO, storedFileName);

			// 수정된 사용자 정보 가져오기
			UserDetails updatedUser = customUserDetailsService.loadUserByUsername(memberJoinDTO.getMemId());

			// 새 인증 정보 생성
			Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUser, updatedUser.getPassword(),
					updatedUser.getAuthorities());

			// 보안 컨텍스트 갱신
			SecurityContextHolder.getContext().setAuthentication(newAuth);

			redirectAttributes.addFlashAttribute("result", "나의 정보 수정 성공");
		} catch (Exception e) {
			log.error("회원 정보 수정 실패", e);
			redirectAttributes.addFlashAttribute("error", "회원 정보 수정 중 오류가 발생했습니다.");
			return "redirect:/member/mypage_modify";
		}

		session.removeAttribute("storedFileName");
		return "redirect:/member/mypage";
	}

	// 회원 삭제
	@PostMapping("/remove")
	public String removeAccount(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		try {
			// 현재 인증된 사용자 정보 가져오기
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String username = authentication.getName(); // 현재 사용자 ID

			// 회원 삭제 처리 (DELETE_FLAG를 1로 설정)
			memberService.remove(username);

			// 세션 무효화
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}

			// 보안 컨텍스트 초기화
			SecurityContextHolder.clearContext();

			redirectAttributes.addFlashAttribute("success", "계정이 성공적으로 삭제되었습니다.");
			return "redirect:/member/login"; // 로그인 페이지로 이동
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "계정 삭제 중 오류가 발생했습니다.");
			return "redirect:/member/mypage";
		}
	}

	// 마이페이지 나의시설예약 조회
	@GetMapping("/reservation")
	public String reservationGet(HttpServletRequest request, Model model) {
		// Add current URI to the model
		String requestURI = request.getRequestURI();
		model.addAttribute("currentURI", requestURI);

		// Get authentication details
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
			MemberSecurityDTO member = (MemberSecurityDTO) authentication.getPrincipal();
			String memId = member.getMemId(); // Current logged-in user's ID

			// Fetch reservations by memId
			List<Reservation> reservations = reservationRepository.findByMemId(memId);

			/// 업데이트된 사용자 정보 가져오기
			Member updatedMember = memberRepository.findById(memId)
					.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
			UserDetails updatedUser = customUserDetailsService.loadUserByUsername(updatedMember.getMemId());

			// 새 인증 정보 생성
			Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUser, updatedUser.getPassword(),
					updatedUser.getAuthorities());

			// 보안 컨텍스트 갱신
			SecurityContextHolder.getContext().setAuthentication(newAuth);

			if (!reservations.isEmpty()) {
				model.addAttribute("reservations", reservations); // Pass reservation list to the model
			} else {
				model.addAttribute("noReservations", "예약이 없습니다.");
			}
		} else {
			model.addAttribute("error", "로그인 정보가 필요합니다.");
		}

		return "member/reservation"; // Return the view name
	}

	// 금액 충전
	@GetMapping("/charge/point")
	public String myCashPro(@RequestParam("amount") BigDecimal amount, @RequestParam("plusPoint") BigDecimal plusPoint,
			HttpServletRequest request, Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
			MemberSecurityDTO member = (MemberSecurityDTO) authentication.getPrincipal();
			String memId = member.getMemId(); // Current logged-in user's ID

			memberService.chargePoint(memId, amount, plusPoint);

			// 업데이트된 사용자 정보 가져오기
			Member updatedMember = memberRepository.findById(memId)
					.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
			UserDetails updatedUser = customUserDetailsService.loadUserByUsername(updatedMember.getMemId());

			// 새 인증 정보 생성
			Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUser, updatedUser.getPassword(),
					updatedUser.getAuthorities());

			// 보안 컨텍스트 갱신
			SecurityContextHolder.getContext().setAuthentication(newAuth);

		}
		;
		// 이전 페이지 URL 가져오기
		String referer = request.getHeader("Referer");
		if (referer != null) {
			return "redirect:" + referer; // 이전 페이지로 리다이렉트
		}
		return "redirect:/member/mypage";
	}

	// 마이페이지 송금 내역 조회
	@GetMapping("/pay_list/transfer")
	@ResponseBody
	public List<TransferHistoryDTO> getTransferHistories() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
	        MemberSecurityDTO member = (MemberSecurityDTO) authentication.getPrincipal();
	        String memId = member.getMemId();
	        // Return transfer history
	        return memberService.getTransferHistories(memId);
	    } else {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 필요합니다.");
	    }
	}
	
	// 마이페이지 충전 내역 조회
	@GetMapping("/pay_list/charge")
	@ResponseBody
	public List<ChargeHistoryDTO> getChargeHistories() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
	        MemberSecurityDTO member = (MemberSecurityDTO) authentication.getPrincipal();
	        String memId = member.getMemId();
	        // Return charge history
	        return memberService.getChargeHistories(memId);
	    } else {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 필요합니다.");
	    }
	}


	
	

}
