package com.lec.packages.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lec.packages.dto.ClubBoardReplyDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.UploadFileDTO;
import com.lec.packages.dto.UploadResultDTO;
import com.lec.packages.service.ClubService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;

@Log4j2
@RestController
@RequestMapping("/club")
public class ClubRestController {
    
    @Value("${com.lec.upload.path}")
    private String uploadPath;
    
    @Autowired
    private ClubService clubService;


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

	@PostMapping(value = "/replies/register/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Integer> register(@RequestBody ClubBoardReplyDTO clubBoardReplyDTO, BindingResult bindingResult) throws BindException{
		
		log.info("do replyRegister");
		log.info(clubBoardReplyDTO);

		if(bindingResult.hasErrors()){
			throw new BindException(bindingResult);
		}

		Map<String, Integer> resultMap = new HashMap<>();
		int replyNo = clubService.registerReply(clubBoardReplyDTO);
		resultMap.put("replyNo", replyNo);
		
		return resultMap;
	}

	@GetMapping(value = "/replies/list/{boardNo},{clubCode}")
	public PageResponseDTO<ClubBoardReplyDTO> getList(@PathVariable("boardNo") int boardNo, @PathVariable("clubCode") String clubCode, PageRequestDTO pageRequestDTO) {

		log.info("do ReplyList");
		log.info(pageRequestDTO);
		PageResponseDTO<ClubBoardReplyDTO> responseDTO = clubService.getReplyListOfBoard(boardNo, clubCode, pageRequestDTO);
		return responseDTO;
		
	}

	@GetMapping("club/replies/getReply/{clubCode},{boardNo},{replyNo}")
	public ClubBoardReplyDTO getReplyDTO(@PathVariable("clubCode") String clubCode, @PathVariable("boardNo") int boardNo, @PathVariable("replyNo") int replyNo) {
		ClubBoardReplyDTO replyDTO = clubService.readReply(clubCode, boardNo, replyNo);
		return replyDTO;
	}

	@PostMapping(value = "/replies/modify/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Integer> modifyReply(@RequestBody ClubBoardReplyDTO replyDTO) {

		log.info("do modifyReply");
		log.info(replyDTO);
		clubService.modifyReply(replyDTO);
		
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("replyNo", replyDTO.getReplyNo());

		return resultMap;
	}

	@DeleteMapping("replies/delete/{clubCode},{boardNo},{replyNo}")
	public Map<String, Integer> deleteReply(@PathVariable("clubCode") String clubCode, @PathVariable("boardNo") int boardNo, @PathVariable("replyNo") int replyNo){
		log.info("do deleteReply");

		int resultReplyNo = clubService.deleteReply(clubCode, boardNo, replyNo);

		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("replyNo", resultReplyNo);

		return resultMap;
	}
}
