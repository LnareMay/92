async function uploadToServer(formObj) {
    console.log("Uploading to server...");
    try {
        const response = await axios({
            method: 'post',
            url: '/upload',
            data: formObj,
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        console.log("Upload response:", response.data);
        return response.data;
    } catch (error) {
        alert(error);
        alert("파일 업로드 중 오류가 발생했습니다.");
        throw error; // 오류를 호출한 곳으로 전달
    }
}

		

async function removeFileToServer(uuid, fileName){
    const response = await axios.delete(`/remove/${uuid}_${fileName}`)
    return response.data
}