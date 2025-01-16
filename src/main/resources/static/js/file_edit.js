document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('fileInput');
    const addFilesBtn = document.getElementById('addFilesBtn');
    const fileList = document.getElementById('fileList');
    const form = document.getElementById('facilityEdit');
	const facilityCode = document.querySelector('input[name="facilityCode"]').value; // 시설 코드 가져오기
	
    let uploadedFiles = []; // 새로 추가된 파일 목록
    let existingFiles = []; // 이미 저장된 파일 목록
	let deletedFiles = []; // 삭제된 기존 파일 목록

    // **1. 초기화: 이미 렌더링된 기존 파일 목록 가져오기**
    document.querySelectorAll('#fileList tr').forEach(row => {
        const fileName = row.querySelector('td').innerText.trim();
        if (fileName) {
            existingFiles.push(fileName);
        }
    });

    // **2. 파일 추가 버튼 클릭 이벤트**
    addFilesBtn.addEventListener('click', () => {
        fileInput.click();
    });

	// 파일 선택 시 리스트에 추가
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
	            button.addEventListener('click', async (e) => {
	                const columnName = e.target.getAttribute('data-column'); // 삭제할 컬럼 이름

	                try {
	                    // 서버에 DELETE 요청 전송
	                    const response = await fetch(`/admin/remove/${facilityCode}/${columnName}`, {
	                        method: 'DELETE',
	                    });

	                    if (response.ok) {
	                        const result = await response.json();
	                        if (result.result) {
	                            alert(`${columnName}이(가) 성공적으로 삭제되었습니다.`);
	                            e.target.closest('tr').remove(); // UI에서 해당 행 제거
	                        } else {
	                            alert(`${columnName} 삭제에 실패했습니다.`);
	                        }
	                    } else {
	                        alert("삭제 요청 중 오류가 발생했습니다.");
	                    }
	                } catch (error) {
	                    console.error("Error deleting file:", error);
	                    alert("서버와 통신 중 오류가 발생했습니다.");
	                }
	            });
	        });
	    }


	// 폼 제출 이벤트
	form.addEventListener('submit', async (e) => {
	    e.preventDefault();

	    const formData = new FormData(form);

	    uploadedFiles.forEach(file => formData.append('files[]', file));
	    existingFiles.forEach(file => formData.append('existingFiles[]', file));
	    deletedFiles.forEach(file => formData.append('deletedFiles[]', file));

	    try {
	        const response = await fetch(form.action, { method: 'POST', body: formData });

	        if (response.ok) {
	            alert("시설 정보가 성공적으로 수정되었습니다.");
	            window.location.reload();
	        } else {
	            alert("시설 정보 수정 중 오류가 발생했습니다.");
	        }
	    } catch (error) {
	        console.error("Error submitting form:", error);
	        alert("서버와 통신 중 오류가 발생했습니다.");
	    }
	});
	
	attachRemoveHandlers();


});