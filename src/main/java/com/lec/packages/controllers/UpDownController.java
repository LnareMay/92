
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lec.packages.dto.UploadFileDTO;
import com.lec.packages.dto.UploadResultDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;

@Log4j2
@RestController
public class UpDownController {

	@Value("${com.lec.upload.path}")
	private String uploadPath;
	
	@Operation(summary = "File Upload Post", description = "POST방식으로 파일 업로드!!")
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<UploadResultDTO> upload(@RequestBody UploadFileDTO uploadFileDTO) {
		
		log.info(uploadFileDTO);
		
		if(uploadFileDTO.getFiles() != null) {
			
			final List<UploadResultDTO> list = new ArrayList<>();			
			
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
				
			}); // end forEach
			
			return list;
			
		} // end If
		
		return null;
	}
	
	@Operation(summary = "파일보기", description = "GET방식으로 첨부파일조회!!")
	@GetMapping(value = "/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName) {
		
		Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
		String resourceName = resource.getFilename();
		HttpHeaders headers = new HttpHeaders();
		
		try {
			headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
		} catch (IOException e) {
			return ResponseEntity.internalServerError().build();
		}
		
		return ResponseEntity.ok().headers(headers).body(resource);
	}
	
	@Operation(summary = "파일삭제", description = "DELETE방식으로 첨부파일삭제!!")
	@DeleteMapping(value = "/remove/{fileName}")
	public Map<String, Boolean> removeFile(
			@RequestBody @PathVariable("fileName") String fileName) {
		
		Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
		String resourceName = resource.getFilename();
		Map<String, Boolean> resultMap = new HashMap<>();
		boolean removed = false;
		
		try {
			String contentType = Files.probeContentType(resource.getFile().toPath());
			removed = resource.getFile().delete();
			
			// 썸네일이 있을 경우
			if(contentType.startsWith("image")) {
				File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
				thumbnailFile.delete();
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		resultMap.put("result", removed);	
		
		return resultMap;
	}
}
