async function uploadToServer (formObj) {
    console.log(formObj)
    const response = await axios({
        method: 'post',
        url: '/club/upload',
        data: formObj,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
    return response.data
}

async function removeFileToServer(uuid, fileName){
    const response = await axios.delete(`/club/remove/${uuid}_${fileName}`)
    return response.data
}