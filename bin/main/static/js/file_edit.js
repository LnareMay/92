document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('fileInput');
    const addFilesBtn = document.getElementById('addFilesBtn');
    const fileList = document.getElementById('fileList');
    const form = document.getElementById('facilityEdit');
    const facilityCode = document.querySelector('input[name="facilityCode"]').value; // 시설 코드 가져오기
    const MAX_FILES = 4; // 최대 업로드 가능한 파일 개수

    let uploadedFiles = []; // 새로 추가된 파일 목록
    let existingFiles = []; // 기존에 저장된 파일 목록
    let deletedFiles = []; // 삭제된 기존 파일 목록
    let removedNewFiles = []; // 삭제된 새로 추가된 파일 목록(저장 전 리스트에서 보이는 파일 삭제)

    // **1. 초기화: 이미 렌더링된 기존 파일 목록 가져오기**
    document.querySelectorAll('#fileList tr').forEach(row => {
        const fileName = row.querySelector('td').innerText.trim();
        const removeButton = row.querySelector('.remove-file');

        if (fileName && removeButton) {
            removeButton.setAttribute('data-file', fileName);
            existingFiles.push(fileName); // 기존 파일 목록에 추가
        }
    });

    // **2. 리스트 업데이트 함수**
    function updateFileList() {
        fileList.innerHTML = ""; // 리스트 초기화

        // 기존 파일 렌더링
        existingFiles.forEach(fileName => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${fileName}</td>
                <td class="text-right">
                    <button type="button" class="btn btn-danger btn-sm remove-file" data-file="${fileName}" data-column="true">Remove</button>
                </td>
            `;
            fileList.appendChild(row);
        });

        // 새로 추가된 파일 렌더링
        uploadedFiles.forEach(file => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${file.name}</td>
                <td class="text-right">
                    <button type="button" class="btn btn-danger btn-sm remove-file" data-file="${file.name}">Remove</button>
                </td>
            `;
            fileList.appendChild(row);
        });

        attachRemoveHandlers(); // 삭제 버튼 이벤트 핸들러 재설정
    }

    // **3. 파일 추가 버튼 클릭 이벤트**
    addFilesBtn.addEventListener('click', () => {
        if (uploadedFiles.length + existingFiles.length >= MAX_FILES) { // 기존 및 새로 추가된 파일 개수 확인
            alert("최대 4개의 파일만 업로드할 수 있습니다.");
            return;
        }
        fileInput.click();
    });

    // **4. 파일 선택 시 리스트에 추가**
    fileInput.addEventListener('change', () => {
        const files = Array.from(fileInput.files); // 사용자가 선택한 파일 배열로 변환

        if (uploadedFiles.length + existingFiles.length + files.length > MAX_FILES) { 
            // 최대 개수를 초과하는 경우 경고 메시지 표시 및 초기화
            alert(`최대 ${MAX_FILES}개의 파일만 업로드할 수 있습니다.`);
            fileInput.value = ""; // 입력 초기화
            return;
        }

        files.forEach(file => {
            if (!uploadedFiles.some(uploadedFile => uploadedFile.name === file.name)) { 
                // 중복 방지: 이미 추가된 파일인지 확인
                uploadedFiles.push(file); // 새로 추가된 파일 목록에 추가
            } else {
                alert(`${file.name}은(는) 이미 추가된 파일입니다.`);
            }
        });

        updateFileList(); // 리스트 업데이트 (기존 및 새로 추가된 파일 모두 포함)
    });

    // **5. 삭제 버튼 이벤트 핸들러 추가**
    function attachRemoveHandlers() {
        document.querySelectorAll('.remove-file').forEach(button => {
            button.addEventListener('click', (e) => {
                const fileName = e.target.getAttribute('data-file'); // 삭제할 파일 이름 가져오기
                const columnName = e.target.getAttribute('data-column'); // 기존 파일 여부 확인

                if (columnName) { 
                    // 기존 파일 삭제 처리
                    if (!deletedFiles.includes(fileName)) { 
                        deletedFiles.push(fileName); // 삭제된 기존 파일 추적
                        existingFiles = existingFiles.filter(file => file !== fileName); // 기존 배열에서 제거
                    }
                } else { 
                    // 새로 추가한 파일 삭제 처리
                    uploadedFiles = uploadedFiles.filter(file => file.name !== fileName); // 업로드 리스트에서 제거
                    removedNewFiles.push(fileName); // 삭제된 새로 추가한 파일 추적
                }

                updateFileList(); // UI 업데이트 (리스트 갱신)
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

        // 삭제된 기존 파일 정보 전송
        deletedFiles.forEach((fileName) => {
            formData.append('deletedFiles[]', fileName);
            console.log(`Added to FormData (deleted): ${fileName}`);
        });

        // 삭제된 새로 추가한 파일 정보도 전송 (서버에서 무시하도록 처리)
        removedNewFiles.forEach(fileName => {
            formData.append('removedNewFiles[]', fileName);
            console.log(`Added to removedNewFiles(new): ${fileName}`);
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

    updateFileList(); // 초기 리스트 렌더링 (기존 및 새로 추가된 데이터 포함)
});
