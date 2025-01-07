package com.lec.packages.controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;




import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;

import com.lec.packages.dto.UploadFileDTO;
import com.lec.packages.dto.UploadResultDTO;

import com.lec.packages.repository.FacilityRepository;

import com.lec.packages.service.FacilityService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;

@Log4j2
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminRestController {
    
    @Value("${com.lec.upload.path}")
    private String uploadPath;
    
    @Autowired
    private FacilityService facilityService;
    
    @Autowired
    private FacilityRepository facilityRepository;  

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> uploadFile(@ModelAttribute UploadFileDTO uploadFileDTO){
        log.info("do upLoadFileController");
        
        if(uploadFileDTO.getFiles() != null){
            List<UploadResultDTO> list;

            list = new ArrayList<>();
            uploadFileDTO.getFiles().forEach(multipartFile -> {
                String originalFileName = multipartFile.getOriginalFilename();
				log.info(originalFileName);
				String uuid = UUID.randomUUID().toString();
				log.info(uuid);
				
				Path savePath = Paths.get(uploadPath, uuid + "_" + originalFileName);
				boolean isImage = false;
				
				try {
					multipartFile.transferTo(savePath); // 실제로 저장할 파일 위치
					
					// 이미지파일이라면
					if(Files.probeContentType(savePath).startsWith("image")) {
						isImage = true;
						File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalFileName);
						Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}  
				
				list.add(UploadResultDTO.builder()
						.uuid(uuid)
						.fileName(originalFileName)
						.img(isImage)
						.build());
            });
            return list;
        }
        return null;
    }
    
	@GetMapping(value = "/view/{fileName}")
	public ResponseEntity<Resource> viewImgFile(@RequestBody @PathVariable("fileName") String fileName) {

		Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

		HttpHeaders headers = new HttpHeaders();
		
		try {
			headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
		} catch (IOException e) {
			return ResponseEntity.internalServerError().build();
		}
		
		return ResponseEntity.ok().headers(headers).body(resource);
	}


	@Operation(summary = "파일삭제", description = "DELETE 방식으로 첨부파일 삭제")
	@DeleteMapping(value = "/remove/{fileName}")
	public ResponseEntity<Map<String, Boolean>> removeFile(@PathVariable("fileName") String fileName) {
	    Map<String, Boolean> resultMap = new HashMap<>();
	    boolean removed = false;
	    try {
	        // 전달된 파일 이름이 썸네일 파일(s_)이라면, 원본 파일 이름 추출
	        String originalFileName = fileName.startsWith("s_") ? fileName.substring(2) : fileName;
	        // 원본 파일 경로
	        File originalFile = new File(uploadPath + File.separator + originalFileName);
	        // 썸네일 파일 경로
	        File thumbnailFile = new File(uploadPath + File.separator + fileName);
	        // 원본 파일 삭제
	        if (originalFile.exists()) {
	            String contentType = Files.probeContentType(originalFile.toPath());
	            removed = originalFile.delete();
	            // 썸네일 파일 삭제 (이미지일 경우만)
	            if (contentType != null && contentType.startsWith("image") && thumbnailFile.exists()) {
	                thumbnailFile.delete();
	            }
	        } else {
	            log.warn("파일이 존재하지 않습니다: {}", originalFileName);
	        }
	    } catch (IOException e) {
	        log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("result", false));
	    }
	    resultMap.put("result", removed);
	    return ResponseEntity.ok(resultMap);
	}

	// 시설 등록
    @PostMapping(value = "/Facility_add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> addFaciltyPagePost(
										        @RequestPart(value = "files", required = false) List<MultipartFile> files,
										        @RequestParam("facilityName") String facilityName,
										        @RequestParam("memId") String memId,
										        @RequestParam("facilityStartTime") LocalTime facilityStartTime,
										        @RequestParam("facilityEndTime") LocalTime facilityEndTime,
										        @RequestParam("facilityAddress") String facilityAddress,
										        @RequestParam("facilityZipcode") String facilityZipcode,
										        @RequestParam("facilityDescription") String facilityDescription,
										        @RequestParam("price") BigDecimal price,
										        @RequestParam("exerciseCode") String exerciseCode,
										        @RequestParam(value = "facilityIsOnlyClub", required = false, defaultValue = "false") Boolean facilityIsOnlyClub) {

	    Map<String, Object> response = new HashMap<>();
	    List<String> imagePaths = new ArrayList<>();

	    try {
	        if (files != null && !files.isEmpty()) {
	            for (MultipartFile file : files) {
	                String originalFileName = file.getOriginalFilename();
	                System.out.println("파일 이름: " + file.getOriginalFilename());
	                if (originalFileName == null || originalFileName.isEmpty()) {
	                    continue;
	                }

	                String uuid = UUID.randomUUID().toString();
	                String fileName = uuid + "_" + originalFileName;

	                Path filePath = Paths.get(uploadPath, fileName);

	                file.transferTo(filePath);

	                String contentType = Files.probeContentType(filePath);
	                if (contentType != null && contentType.startsWith("image")) {
	                    File thumbnail = new File(uploadPath, "s_" + fileName);
	                    Thumbnailator.createThumbnail(filePath.toFile(), thumbnail, 200, 200);
	                }

	                imagePaths.add(fileName);
	            }
   
	        }

	        FacilityDTO facilityDTO = FacilityDTO.builder() 
								        		  .facilityName(facilityName)
								        		  .memId(memId) 
								        		  .facilityAddress(facilityAddress)
								        		  .facilityZipcode(facilityZipcode) 
								        		  .facilityDescription(facilityDescription)
								        		  .facilityStartTime(facilityStartTime) 
								        		  .facilityEndTime(facilityEndTime)
								        		  .price(price) .exerciseCode(exerciseCode)
								        		  .facilityIsOnlyClub(facilityIsOnlyClub) 
								        		  .facilityImage1(imagePaths.size() > 0 ? imagePaths.get(0) : null) 
								        		  .facilityImage2(imagePaths.size() > 1 ? imagePaths.get(1) : null) 
								        		  .facilityImage3(imagePaths.size() > 2 ? imagePaths.get(2) : null) 
								        		  .facilityImage4(imagePaths.size() > 3 ? imagePaths.get(3) : null) .build();

	  
	        String facilityCode = facilityService.register(facilityDTO);

	        //성공 시 리다이렉트 설정
	        HttpHeaders headers =new HttpHeaders();
	        headers.setLocation(URI.create("/admin/Facility_list"));
	        return new ResponseEntity<>(headers,HttpStatus.FOUND);
	        
//	        response.put("success", true);
//	        response.put("redirectUrl", "/");
//	        return ResponseEntity.status(HttpStatus.FOUND)
//	                .header(HttpHeaders.LOCATION, "/")
//	                .body(response);
//	        
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "시설 등록 중 오류가 발생했습니다.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	// 클럽수정
//	@PostMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> clubModify(
//	        @ModelAttribute ClubDTO clubDTO,
//	        @RequestPart(value = "files", required = false) List<MultipartFile> files,
//	        HttpServletRequest request,
//	        Model model,
//	        PageRequestDTO pageRequestDTO,
//	        RedirectAttributes redirectAttributes) {
//
//	    String requestURI = request.getRequestURI();
//	    model.addAttribute("currentURI", requestURI);
//
//	    try {
//	        Optional<Club> optionalClub = clubRepository.findById(clubDTO.getClubCode());
//	        Club club = optionalClub.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽입니다."));
//
//	        if (files != null && !files.isEmpty()) {
//	            for (int i = 0; i < files.size(); i++) {
//	                MultipartFile file = files.get(i);
//	                if (file != null && !file.isEmpty()) {
//	                    String originalFileName = file.getOriginalFilename();
//	                    String uuid = UUID.randomUUID().toString();
//	                    String fileName = uuid + "_" + originalFileName;
//
//	                    Path filePath = Paths.get(uploadPath, fileName);
//	                    file.transferTo(filePath);
//
//	                    String contentType = Files.probeContentType(filePath);
//	                    if (contentType != null && contentType.startsWith("image")) {
//	                        File thumbnail = new File(uploadPath, "s_" + fileName);
//	                        Thumbnailator.createThumbnail(filePath.toFile(), thumbnail, 200, 200);
//	                    }
//
//	                    switch (i) {
//	                        case 0 -> clubDTO.setClubImage1(fileName);
//	                        case 1 -> clubDTO.setClubImage2(fileName);
//	                        case 2 -> clubDTO.setClubImage3(fileName);
//	                        case 3 -> clubDTO.setClubImage4(fileName);
//	                    }
//	                }
//	            }
//	        }
//
//	        if (clubDTO.getClubImage1() == null) clubDTO.setClubImage1(club.getClubImage1());
//	        if (clubDTO.getClubImage2() == null) clubDTO.setClubImage2(club.getClubImage2());
//	        if (clubDTO.getClubImage3() == null) clubDTO.setClubImage3(club.getClubImage3());
//	        if (clubDTO.getClubImage4() == null) clubDTO.setClubImage4(club.getClubImage4());
//
//	        clubService.modify(clubDTO);
//	        log.info(clubDTO.getClubAddress());
//
//	        String redirectUrl = String.format("./club_detail?clubCode=%s", clubDTO.getClubCode());
//	        return ResponseEntity.status(HttpStatus.FOUND)
//	                .header(HttpHeaders.LOCATION, redirectUrl)
//	                .build();
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
//	    }
//	}

	/*
	 * @GetMapping("club/replies/getReply/{clubCode},{boardNo},{replyNo}") public
	 * ClubBoardReplyDTO getReplyDTO(@PathVariable("clubCode") String
	 * clubCode, @PathVariable("boardNo") int boardNo, @PathVariable("replyNo") int
	 * replyNo) { ClubBoardReplyDTO replyDTO = clubService.readReply(clubCode,
	 * boardNo, replyNo); return replyDTO; }
	 */

	/*
	 * @PostMapping(value = "/replies/modify/", consumes =
	 * MediaType.APPLICATION_JSON_VALUE) public Map<String, Integer>
	 * modifyReply(@RequestBody ClubBoardReplyDTO replyDTO) {
	 * 
	 * log.info("do modifyReply"); log.info(replyDTO);
	 * clubService.modifyReply(replyDTO);
	 * 
	 * Map<String, Integer> resultMap = new HashMap<>(); resultMap.put("replyNo",
	 * replyDTO.getReplyNo());
	 * 
	 * return resultMap; }
	 */
	/*
	 * @DeleteMapping("replies/delete/{clubCode},{boardNo},{replyNo}") public
	 * Map<String, Integer> deleteReply(@PathVariable("clubCode") String
	 * clubCode, @PathVariable("boardNo") int boardNo, @PathVariable("replyNo") int
	 * replyNo){ log.info("do deleteReply");
	 * 
	 * int resultReplyNo = clubService.deleteReply(clubCode, boardNo, replyNo);
	 * 
	 * Map<String, Integer> resultMap = new HashMap<>(); resultMap.put("replyNo",
	 * resultReplyNo);
	 * 
	 * return resultMap; }
	 */

	/*
	 * @GetMapping("/club_board_rest") public PageResponseDTO<ClubBoardAllListDTO>
	 * clubBoard(@RequestParam("clubCode") String clubCode, PageRequestDTO
	 * pageRequestDTO , @RequestParam("page") int page, @RequestParam("size") int
	 * size , HttpServletRequest request, Model model) { String requestURI =
	 * request.getRequestURI(); model.addAttribute("currentURI", requestURI);
	 * 
	 * log.info("do clubBoardListRest"); pageRequestDTO.setSize(size);
	 * pageRequestDTO.setPage(page); pageRequestDTO.setType("ALL");
	 * PageResponseDTO<ClubBoardAllListDTO> boardDTO =
	 * clubService.listWithAll(pageRequestDTO, clubCode); log.info(boardDTO);
	 * 
	 * return boardDTO; }
	 */

}
