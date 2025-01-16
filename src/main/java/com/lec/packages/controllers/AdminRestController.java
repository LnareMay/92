package com.lec.packages.controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

import com.lec.packages.domain.Facility;
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
								        		  .price(price) 
								        		  .exerciseCode(exerciseCode)
								        		  .facilityIsOnlyClub(facilityIsOnlyClub) 
								        		  .facilityImage1(imagePaths.size() > 0 ? imagePaths.get(0) : null) 
								        		  .facilityImage2(imagePaths.size() > 1 ? imagePaths.get(1) : null) 
								        		  .facilityImage3(imagePaths.size() > 2 ? imagePaths.get(2) : null) 
								        		  .facilityImage4(imagePaths.size() > 3 ? imagePaths.get(3) : null) 
								        		  .build();

	  
	        facilityService.register(facilityDTO);

	        //성공 시 리다이렉트 설정
	        HttpHeaders headers =new HttpHeaders();
	        headers.setLocation(URI.create("/admin/Facility_list"));
	        return new ResponseEntity<>(headers,HttpStatus.FOUND);
	        	        
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "시설 등록 중 오류가 발생했습니다.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
    
    //시설 사진 수정(삭제)
  	@DeleteMapping("/remove/{facilityCode}/{columnName}")
  	public ResponseEntity<Map<String, Boolean>> removeFacilityImage(
  	        @PathVariable("facilityCode") String facilityCode,
  	        @PathVariable("columnName") String columnName) {
  	    Map<String, Boolean> resultMap = new HashMap<>();
  	    boolean updated = false;

  	    try {
  	        // 시설 정보 가져오기
  	        Optional<Facility> optionalFacility = facilityRepository.findByFacilityCode(facilityCode);
  	        Facility facility = optionalFacility.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시설입니다."));

  	        // 컬럼 이름에 따라 해당 컬럼을 null로 설정
  	        switch (columnName) {
  	            case "facilityImage1" -> facility.setFacilityImage1(null);
  	            case "facilityImage2" -> facility.setFacilityImage2(null);
  	            case "facilityImage3" -> facility.setFacilityImage3(null);
  	            case "facilityImage4" -> facility.setFacilityImage4(null);
  	            default -> throw new IllegalArgumentException("잘못된 컬럼 이름입니다.");
  	        }

  	        // 변경 내용 저장
  	        facilityRepository.save(facility); // 반드시 호출하여 변경 사항 저장
  	        updated = true;
  	    } catch (Exception e) {
  	        e.printStackTrace();
  	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("result", false));
  	    }

  	    resultMap.put("result", updated);
  	    return ResponseEntity.ok(resultMap);
  	}

    
    
    
  	@PostMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  	public ResponseEntity<?> facilityModify(
  	        @ModelAttribute FacilityDTO facilityDTO,
  	        @RequestPart(value = "files", required = false) List<MultipartFile> files,
  	        @RequestParam(value = "existingFiles", required = false) List<String> existingFiles,
  	        @RequestParam(value = "deletedFiles", required = false) List<String> deletedFiles,
  	        HttpServletRequest request,
  	        Model model,
  	        PageRequestDTO pageRequestDTO,
  	        RedirectAttributes redirectAttributes) {

  	    try {
  	        Optional<Facility> optionalFacility = facilityRepository.findByFacilityCode(facilityDTO.getFacilityCode());
  	        Facility facility = optionalFacility.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시설입니다."));

  	        // 삭제된 기존 파일 처리
  	        if (deletedFiles != null && !deletedFiles.isEmpty()) {
  	            for (String fileName : deletedFiles) {
  	                // 데이터베이스에서 컬럼 값 제거
  	                if (facility.getFacilityImage1() != null && facility.getFacilityImage1().equals(fileName)) {
  	                    facility.setFacilityImage1(null);
  	                } else if (facility.getFacilityImage2() != null && facility.getFacilityImage2().equals(fileName)) {
  	                    facility.setFacilityImage2(null);
  	                } else if (facility.getFacilityImage3() != null && facility.getFacilityImage3().equals(fileName)) {
  	                    facility.setFacilityImage3(null);
  	                } else if (facility.getFacilityImage4() != null && facility.getFacilityImage4().equals(fileName)) {
  	                    facility.setFacilityImage4(null);
  	                }

  	                // 파일 시스템에서 실제 파일 삭제
  	                Path filePath = Paths.get(uploadPath, fileName);
  	                Files.deleteIfExists(filePath);
  	            }
  	            facilityRepository.save(facility); // 변경 사항 저장
  	        }

  	        // 기존 파일 유지
  	        if (existingFiles != null) {
  	            facilityDTO.setFacilityImage1(existingFiles.size() > 0 ? existingFiles.get(0) : null);
  	            facilityDTO.setFacilityImage2(existingFiles.size() > 1 ? existingFiles.get(1) : null);
  	            facilityDTO.setFacilityImage3(existingFiles.size() > 2 ? existingFiles.get(2) : null);
  	            facilityDTO.setFacilityImage4(existingFiles.size() > 3 ? existingFiles.get(3) : null);
  	        }

  	        // 새로 업로드된 파일 처리
  	        if (files != null && !files.isEmpty()) {
  	            for (int i = 0; i < files.size(); i++) {
  	                MultipartFile file = files.get(i);
  	                if (!file.isEmpty()) {
  	                    String originalFileName = file.getOriginalFilename();
  	                    String uuid = UUID.randomUUID().toString();
  	                    String fileName = uuid + "_" + originalFileName;

  	                    Path filePath = Paths.get(uploadPath, fileName);
  	                    file.transferTo(filePath);

  	                    switch (i) {
  	                        case 0 -> facilityDTO.setFacilityImage1(fileName);
  	                        case 1 -> facilityDTO.setFacilityImage2(fileName);
  	                        case 2 -> facilityDTO.setFacilityImage3(fileName);
  	                        case 3 -> facilityDTO.setFacilityImage4(fileName);
  	                    }
  	                }
  	            }
  	        }

  	        // 시설 정보 업데이트
  	        facilityService.modify(facilityDTO);

  	        String redirectUrl = String.format("./Facility_detail?facilityCode=%s", facilityDTO.getFacilityCode());
  	        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();

  	    } catch (Exception e) {
  	        e.printStackTrace();
  	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
  	    }
  	}



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
