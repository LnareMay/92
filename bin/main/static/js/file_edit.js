document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('fileInput');
    const addFilesBtn = document.getElementById('addFilesBtn');
    const fileList = document.getElementById('fileList');
    const form = document.getElementById('facilityEdit');
    const facilityCode = document.querySelector('input[name="facilityCode"]').value; // 시설 코드 가져오기

    let uploadedFiles = []; // 새로 추가된 파일 목록
    let existingFiles = []; // 기존에 저장된 파일 목록
    let deletedFiles = []; // 삭제된 기존 파일 목록
    let removedNewFiles = []; // 삭제된 새로 추가된 파일 목록

    // **1. 초기화: 이미 렌더링된 기존 파일 목록 가져오기**
    document.querySelectorAll('#fileList tr').forEach(row => {
        const fileName = row.querySelector('td').innerText.trim();
		const removeButton = row.querySelector('.remove-file');
		
        if (fileName && removeButton) {
			removeButton.setAttribute('data-file',fileName);
            existingFiles.push(fileName);
		}
	
    });

    // **2. 파일 추가 버튼 클릭 이벤트**
    addFilesBtn.addEventListener('click', () => {
        if (uploadedFiles.length >= 4) {
            alert("최대 4개의 파일만 업로드할 수 있습니다.");
            return;
        }
        fileInput.click();
    });

    // **3. 파일 선택 시 리스트에 추가**
    fileInput.addEventListener('change', () => {
        const files = Array.from(fileInput.files);

        files.forEach(file => {
            if (!uploadedFiles.some(uploadedFile => uploadedFile.name === file.name)) { // 중복 방지
                uploadedFiles.push(file);

                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${file.name}</td>
                    <td class="text-right">
                        <button type="button" class="btn btn-danger btn-sm remove-file" data-file="${file.name}">Remove</button>
                    </td>
                `;
                fileList.appendChild(row);
            } else {
                alert(`${file.name}은(는) 이미 추가된 파일입니다.`);
            }
        });

        attachRemoveHandlers(); // 삭제 버튼 이벤트 핸들러 추가
    });

    // **4. 삭제 버튼 이벤트 핸들러 추가**
    function attachRemoveHandlers() {
        document.querySelectorAll('.remove-file').forEach(button => {
            button.addEventListener('click', (e) => {
				
                const fileName = e.target.getAttribute('data-file'); // 파일 이름 가져오기
                const columnName = e.target.getAttribute('data-column'); // 삭제할 컬럼 이름

                if (columnName) {
                    // 기존 파일 삭제 처리
					if(!deletedFiles.includes(fileName)){
	                    deletedFiles.push(fileName); // 삭제된 기존 파일 추적
	                    existingFiles = existingFiles.filter(file => file !== fileName); // 기존 배열에서 제거		
	                    console.log(`Deleted file added to deletedFiles: ${fileName}`);
					}
                } else {
                    // 새로 추가한 파일 삭제 처리
                    uploadedFiles = uploadedFiles.filter(file => file.name !== fileName); // 업로드 리스트에서 제거
                    removedNewFiles.push(fileName); // 삭제된 새 파일 추적
                    console.log(`Deleted new file: ${fileName}`);
                }

                e.target.closest('tr').remove(); // UI에서 해당 행 제거
            });
        });
    }

	form.addEventListener('submit', async (e) => {
	    e.preventDefault();
	    console.log("Form submission started");

	    const formData = new FormData(form);

	    // 기존 파일 추가
	    existingFiles.forEach((fileName) => {
	        formData.append('existingFiles[]', fileName);
	        console.log(`Added to FormData (existing): ${fileName}`);
	    });

	    // 새로 추가된 파일 추가
	    uploadedFiles.forEach((file) => {
	        formData.append('newFiles[]', file);
	        console.log(`Added to FormData (new): ${file.name}`);
	    });

	    // 삭제된 파일 추가
	    deletedFiles.forEach((fileName) => {
	        formData.append('deletedFiles[]', fileName);
	        console.log(`Added to FormData (deleted): ${fileName}`);
	    });

	    try {
	        const response = await fetch(form.action, { method: 'POST', body: formData });

	        if (response.ok) {
	            alert("시설 정보가 성공적으로 수정되었습니다.");
	        } else {
	            alert("시설 정보 수정 중 오류가 발생했습니다.");
	        }
	    } catch (error) {
	        console.error("Error submitting form:", error);
	        alert("서버와 통신 중 오류가 발생했습니다.");
	    }

	    console.log("Form submission ended");
	});

    attachRemoveHandlers();
});
