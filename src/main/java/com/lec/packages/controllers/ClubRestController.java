package com.lec.packages.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lec.packages.dto.ClubBoardReplyDTO;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.UploadFileDTO;
import com.lec.packages.dto.UploadResultDTO;
import com.lec.packages.service.ClubService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;

@Log4j2
@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubRestController {
    
    @Value("${com.lec.upload.path}")
    private String uploadPath;
    
    private ClubService clubService;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> uploadFile(@RequestBody UploadFileDTO uploadFileDTO){
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

	@PostMapping(value = "/replies/register", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Integer> register(@Valid @RequestBody ClubBoardReplyDTO clubBoardReplyDTO, BindingResult bindingResult) throws BindException{
		
		log.info("do replyRegister");

		if(bindingResult.hasErrors()){
			throw new BindException(bindingResult);
		}

		Map<String, Integer> resultMap = new HashMap<>();
		int replyNo = clubService.registerReply(clubBoardReplyDTO);
		resultMap.put("replyNo", replyNo);
		
		return resultMap;
	}
	
	 
	
	
	/* 클럽생성(이미지)
	@PostMapping(value = "/club_create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> createClubWithImages(
			@RequestPart("clubName") String clubName
			, @RequestPart("memId") String memId
			, @RequestPart("clubExercise") String clubExercise
			, @RequestPart("clubIntroduction") String clubIntroduction
			, @RequestPart("clubTheme") String clubTheme
			, @RequestPart("clubAddress") String clubAddress
			, @RequestPart("clubIsprivate") boolean clubIsprivate
			, @RequestPart("clubPw") String clubPw
			, @RequestPart(value = "clubImage1", required = false) MultipartFile clubImage1
			, @RequestPart(value = "clubImage2", required = false) MultipartFile clubImage2
			, @RequestPart(value = "clubImage3", required = false) MultipartFile clubImage3
			, @RequestPart(value = "clubImage4", required = false) MultipartFile clubImage4
			, BindingResult bindingResult
			, RedirectAttributes redirectAttributes
			, HttpServletRequest request, Model model) {
		
		String requestURI = request.getRequestURI();
		model.addAttribute("currentURI", requestURI);

	    ClubDTO clubDTO = ClubDTO.builder()
	            .clubName(clubName)
	            .memId(memId)
	            .clubIntroduction(clubIntroduction)
	            .build();
		
		String clubCode = clubService.create(clubDTO);
		clubDTO.setClubCode(clubCode);
		
		List<MultipartFile> images = List.of(clubImage1, clubImage2, clubImage3, clubImage4);
		List<String> uploadedImages = new ArrayList<>();
		
		for(int i=0; i<images.size(); i++) {
			MultipartFile file = images.get(i);
			if (file != null && !file.isEmpty()) {
				String uuid = UUID.randomUUID().toString();
				String fileName = uuid + "_" + file.getOriginalFilename();
				
				try {
					Path savePath = Paths.get(uploadPath, uuid +"_" + fileName);
					file.transferTo(savePath.toFile());
					uploadedImages.add(fileName);
					
					switch (i) {
						case 0 -> clubDTO.setClubImage1(fileName);
						case 1 -> clubDTO.setClubImage2(fileName);
						case 2 -> clubDTO.setClubImage3(fileName);
						case 3 -> clubDTO.setClubImage4(fileName);
					} 
				} catch (IOException e) {
					e.printStackTrace();
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();	
				}
			}
		}
		clubService.updateImages(clubCode, clubDTO);
		
		return ResponseEntity.status(HttpStatus.FOUND)
		            .header("Location", "/")
		            .build();
	}
	*/
	
}
